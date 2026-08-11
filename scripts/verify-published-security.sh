#!/usr/bin/env bash

set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
candidate_repo="${repo_root}/build/publications/repos"
candidate_repo_uri="file://${candidate_repo}"
maven_local_repo="${repo_root}/build/published-security/maven-repository"
security_version="$(awk -F= '$1 == "version" { print substr($0, index($0, "=") + 1); exit }' "${repo_root}/gradle.properties")"
evidence_dir="${repo_root}/build/reports/published-security"

if [[ ! "${security_version}" =~ ^5\.8\.16-nes\.patch\.[0-9]+(-SNAPSHOT)?$ ]]; then
	echo "Invalid or missing Spring Security project version: ${security_version:-<empty>}" >&2
	exit 1
fi

find_java8_home() {
	if [[ -n "${JAVA8_HOME:-}" && -x "${JAVA8_HOME}/bin/javac" ]]; then
		printf '%s\n' "${JAVA8_HOME}"
		return
	fi
	for candidate in "${HOME}"/.sdkman/candidates/java/8.*; do
		if [[ -x "${candidate}/bin/javac" ]]; then
			printf '%s\n' "${candidate}"
			return
		fi
	done
	echo "A Java 8 JDK is required; set JAVA8_HOME" >&2
	exit 1
}

java8_home="$(find_java8_home)"

rm -rf "${candidate_repo}"
rm -rf "${maven_local_repo}"
rm -rf "${evidence_dir}"
mkdir -p "${evidence_dir}"

{
	echo "java8.home=${java8_home}"
	"${java8_home}/bin/java" -version 2>&1
} > "${evidence_dir}/java8-runtime.txt"

if git -C "${repo_root}" diff --quiet && git -C "${repo_root}" diff --cached --quiet; then
	tracked_worktree=clean
else
	tracked_worktree=modified
fi
{
	echo "source.commit=$(git -C "${repo_root}" rev-parse HEAD)"
	echo "source.trackedWorktree=${tracked_worktree}"
} > "${evidence_dir}/source-state.txt"

"${repo_root}/gradlew" --no-daemon --no-parallel -PbuildSrc.skipTests=true \
	:bjca-footstone-bpring-security-dependencies:publishMavenJavaPublicationToLocalRepository \
	:bjca-footstone-bpring-security-bom:publishMavenJavaPublicationToLocalRepository \
	:bjca-footstone-bpring-security-crypto:publishMavenJavaPublicationToLocalRepository \
	:bjca-footstone-bpring-security-core:publishMavenJavaPublicationToLocalRepository \
	:bjca-footstone-bpring-security-web:publishMavenJavaPublicationToLocalRepository \
	:bjca-footstone-bpring-security-saml2-service-provider:publishMavenJavaPublicationToLocalRepository \
	:bjca-footstone-bpring-security-ldap:publishMavenJavaPublicationToLocalRepository \
	:bjca-footstone-bpring-security-openid:publishMavenJavaPublicationToLocalRepository \
	-x test

maven_consumer="${repo_root}/tests/published-security/maven"
JAVA_HOME="${java8_home}" PATH="${java8_home}/bin:${PATH}" mvn -q -f "${maven_consumer}/pom.xml" \
	-gs "${maven_consumer}/global-settings.xml" \
	-s "${maven_consumer}/settings.xml" \
	-U \
	-DcandidateRepository="${candidate_repo_uri}" \
	-Dspring.security.version="${security_version}" \
	-Dmaven.repo.local="${maven_local_repo}" \
	clean package dependency:build-classpath \
	-Dmdep.outputFile="${maven_consumer}/target/classpath.txt"

JAVA_HOME="${java8_home}" PATH="${java8_home}/bin:${PATH}" mvn -q -f "${maven_consumer}/pom.xml" \
	-gs "${maven_consumer}/global-settings.xml" \
	-s "${maven_consumer}/settings.xml" \
	-U \
	-DcandidateRepository="${candidate_repo_uri}" \
	-Dspring.security.version="${security_version}" \
	-Dmaven.repo.local="${maven_local_repo}" dependency:tree \
	-DoutputFile="${evidence_dir}/maven-dependency-tree.txt"

maven_classpath="$(<"${maven_consumer}/target/classpath.txt")"
grep -Eq "/bcpkix-jdk18on/1\.84/" "${maven_consumer}/target/classpath.txt"
grep -Eq "/bcprov-jdk18on/1\.84/" "${maven_consumer}/target/classpath.txt"
grep -Eq "/guava/32\.0\.1-jre/" "${maven_consumer}/target/classpath.txt"
grep -Eq "/xmlsec/2\.2\.6/" "${maven_consumer}/target/classpath.txt"
grep -Eq "/woodstox-core/5\.4\.0/" "${maven_consumer}/target/classpath.txt"
grep -Eq "/xercesImpl/2\.12\.2/" "${maven_consumer}/target/classpath.txt"
if grep -Eq "jdk15on|/velocity/1\.7/|/commons-lang/2\." "${maven_consumer}/target/classpath.txt"; then
	echo "Maven consumer resolved a prohibited legacy dependency" >&2
	exit 1
fi
JAVA_HOME="${java8_home}" "${java8_home}/bin/java" \
	-cp "${maven_consumer}/target/classes:${maven_classpath}" security.consumer.PublishedSecuritySmoke

gradle_consumer="${repo_root}/tests/published-security/gradle"
JAVA_HOME="${java8_home}" PATH="${java8_home}/bin:${PATH}" "${repo_root}/gradlew" \
	--no-daemon --no-parallel -p "${gradle_consumer}" \
	-PcandidateRepository="${candidate_repo}" \
	-PsecurityVersion="${security_version}" clean verifySecurityGraph run

cp "${gradle_consumer}/build/security-evidence/runtime-classpath.txt" \
	"${evidence_dir}/gradle-runtime-classpath.txt"

artifact_root="${candidate_repo}/cn/bjca/footstone/bpring/security"
for artifact in \
	bjca-footstone-bpring-security-dependencies \
	bjca-footstone-bpring-security-bom \
	bjca-footstone-bpring-security-crypto \
	bjca-footstone-bpring-security-core \
	bjca-footstone-bpring-security-web \
	bjca-footstone-bpring-security-saml2-service-provider \
	bjca-footstone-bpring-security-ldap \
	bjca-footstone-bpring-security-openid; do
	test -d "${artifact_root}/${artifact}/${security_version}"
done

(
	cd "${candidate_repo}"
	find "cn/bjca/footstone/bpring/security" -path "*/${security_version}/*" -type f \
		\( -name '*.jar' -o -name '*.pom' -o -name '*.module' \) -print | sort \
		| env LC_ALL=C xargs shasum -a 256
) > "${evidence_dir}/candidate-sha256.txt"

cat > "${evidence_dir}/disposition.txt" <<EOF
Spring LDAP CVE-2026-41720: Spring LDAP 2.4.5 is not available from the approved repositories.
Spring Security LDAP entry points reject null and empty passwords before LDAP bind:
BindAuthenticator and AbstractLdapAuthenticationProvider.
Published dependency remains spring-ldap-core 2.4.4; this is a reachability disposition,
not a claim that the upstream Spring LDAP library itself is patched.
OpenSAML 4: withheld from the Java 8 SAML main artifact.
Velocity 1.7, Commons Lang 2.x, and Bouncy Castle jdk15on: absent from the consumer graph.
EOF

cat > "${evidence_dir}/smoke-results.txt" <<EOF
Maven consumer on Java 8: passed
Gradle consumer on Java 8: passed
OpenSAML 3 initialization: passed
OpenSAML 4 absence assertion: passed
Bouncy Castle AES-GCM round trip: passed
LDAP, OpenID, and Xerces class loading: passed
EOF

for evidence in \
	source-state.txt \
	java8-runtime.txt \
	maven-dependency-tree.txt \
	gradle-runtime-classpath.txt \
	smoke-results.txt \
	disposition.txt \
	candidate-sha256.txt; do
	test -s "${evidence_dir}/${evidence}"
	echo "${evidence}"
done > "${evidence_dir}/evidence-index.txt"

echo "Published Maven/Gradle consumers and Java 8 smoke tests passed"
