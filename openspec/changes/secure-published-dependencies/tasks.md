## 1. Consumer-visible dependency graph

- [x] 1.1 Raise the shared Bouncy Castle baseline and published constraints to 1.84 `jdk18on`
- [x] 1.2 Publish OpenSAML 3 runtime baselines for Commons Collections, Guava, XMLSec, Woodstox, and Bouncy Castle
- [x] 1.3 Exclude Velocity 1.7, Commons Lang 2.x, and Bouncy Castle `jdk15on` from the SAML consumer graph
- [x] 1.4 Keep OpenID published and constrain its Xerces runtime to 2.12.2
- [x] 1.5 Remove the test-only Logback constraint from public platform/BOM metadata

## 2. Java 8 publication boundary

- [x] 2.1 Package only `opensaml3Main` output and sources in the Java 8 SAML publication
- [x] 2.2 Exclude OpenSAML 4 from the SAML Javadoc and published main JAR
- [x] 2.3 Add a major-version-52 gate to all Spring Security main JAR publication tasks
- [x] 2.4 Add a SAML-specific publication assertion that rejects OpenSAML 4 implementation classes

## 3. Runtime applicability

- [x] 3.1 Record the unavailable Spring LDAP 2.4.5 repository result and retain Spring LDAP 2.4.4
- [x] 3.2 Add or confirm regression coverage that Spring Security rejects empty passwords before LDAP bind

## 4. Independent release verification

- [x] 4.1 Publish platform, BOM, Crypto, Core, Web, SAML, LDAP, and OpenID candidates to an isolated local repository
- [x] 4.2 Add Maven and Gradle consumers that assert required and prohibited dependency versions
- [x] 4.3 Run representative Maven and Gradle smoke tests on a real Java 8 JVM
- [x] 4.4 Wire candidate verification into `make deploy`
- [x] 4.5 Store dependency graphs, Java 8 identity, smoke results, dispositions, and candidate SHA-256 under `build/reports/published-security`

## 5. Regression and completion

- [x] 5.1 Run relevant Crypto and SAML/OpenSAML 3 regression tests
- [x] 5.2 Run relevant LDAP empty-password and OAuth2 JOSE BC regression tests
- [x] 5.3 Review generated POM/module metadata and the full source diff for accidental coordinate or API changes
- [ ] 5.4 Run strict OpenSpec validation and archive the completed change
