# Static RELEASE preparation evidence

- Component: `spring-security-5.8`
- Preparation HEAD: `06831821d9995de25089d2a8ba712bb8d35c6f70`
- Target version: `5.8.16-nes.patch.1`
- Internal upstream: `spring-framework-5.3:5.3.39-nes.patch.1`
- Upstream gate: satisfied; the central manifest records Spring Framework 5.3 as `tagged`, which is beyond `nexus-verified`.
- Approved exclusions: none.
- Tool directories `.claude/`, `.codex/`, and `.cursor/` remain untracked and excluded from release staging.

The successful full `make build` at ancestor commit
`74d3404b947200fb0bb856342ea47f67af78dde0` completed 429 tasks with zero
failures. Later targeted crypto, SAML2 service provider, and OAuth2 JOSE tests
also completed successfully. The intervening tracked changes contain no
production or test source changes, so this evidence is reused for the release
build/test gate.

## Local RELEASE publication

The coordinator ran the catalog-equivalent incremental local publication without
`clean` or project tests:

```text
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 ./gradlew publishToMavenLocal -x test -PbuildSrc.skipTests=true
```

The first attempt exited `1` after `1m 53s` because the forked javadoc process
used `US-ASCII` for existing Chinese source comments. No Nexus task ran. The
retry supplied UTF-8 to Gradle and forked JVMs, skipped `buildSrc` tests, and
completed successfully in `1m 58s` with 175 actionable tasks (158 executed,
17 up-to-date).

The publication set contains 20 Maven publications: ACL, aspects, BOM, CAS,
config, core, crypto, data, LDAP, messaging, OAuth2 client/core/JOSE/resource
server, OpenID, RSocket, SAML2 service provider, taglibs, test, and web. The
disabled remoting publication remained skipped.

The release tool scanned 33 generated and repository POM files. Internal
`cn.bjca.footstone` SNAPSHOT findings were `0`. In particular,
`bjca-footstone-bpring-security-data:5.8.16-nes.patch.1` references
`bjca-footstone-bpring-core:5.3.39-nes.patch.1`.

An offline Maven consumer resolved
`bjca-footstone-bpring-security-data:5.8.16-nes.patch.1` in `3.114s`. Its
internal graph contained only Security `5.8.16-nes.patch.1` artifacts and
Framework `5.3.39-nes.patch.1` artifacts (AOP, beans, context, expression,
core, and JCL). No internal SNAPSHOT was resolved.

Nexus absence, deploy, remote verification, tag, push, documentation closeout,
and OpenSpec archive remain pending and coordinator-controlled.
