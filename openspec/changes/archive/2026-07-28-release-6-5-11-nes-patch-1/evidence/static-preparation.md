# Static RELEASE preparation evidence

- Recorded: 2026-07-27
- Owner lease: `wave2-security-65`, acquired `2026-07-27T06:21:02Z`, expires `2026-07-27T12:21:02Z`
- Repository/branch: `spring-security-6.5` / `6.5.x-bjca-patch`
- Initial HEAD: `887d046f3d9a600108734894407c6d697b1710cb`
- Origin fetch/push: `https://github.com/atbjca/spring-security.git`
- Active change: `release-6-5-11-nes-patch-1` (`spec-driven`, apply-ready)
- Java: Corretto 17.0.17; Gradle wrapper configured for 8.14.5
- Initial version: `6.5.11-nes.patch.1-SNAPSHOT`
- Target version: `6.5.11-nes.patch.1`
- Explicit exclusions: none

## Worktree boundary

The initial tracked worktree was clean. Untracked `.claude/`, `.codex/`, and
`.cursor/` are local tool directories and remain excluded from staging. The
untracked OpenSpec change is the only approved release work at preparation
start.

## Dependency gate

The sole declared internal upstream is Spring Framework
`6.2.19-nes.patch.1`. Central evidence records state `tagged`, release commit
`b2bb78eb3c6571aa17d1c1a52e6f258d4b768acb`, annotated tag
`v6.2.19-nes.patch.1`, Nexus POM/JAR checksums, and RELEASE consumer evidence.
The dependency gate is therefore satisfied. Security remains ordered after
Framework; this does not waive any Security local verification gate.

## Static preparation

`gradle.properties` now selects Security RELEASE `6.5.11-nes.patch.1` and the
version catalog selects Framework RELEASE `6.2.19-nes.patch.1`. The four
required component documents describe the RELEASE coordinates, BOM usage,
dependency ordering, Nexus host/repository/path, validation sequence, no
explicit exclusions, and unchanged Java packages.

## Reusable build evidence assessment

Ancestor commits `f255e7f08f8bf725653e50b813378acd736939ba` and
`93863713b6c0ef2af0cf27990b384f16c2b19cae` contain the prior test-failure
fixes. The operator explicitly directed this release run not to rerun
`make build` or `make test`; this is the approved exception for those two
expensive gates. The release diff contains no production or test source change.
Release-specific compilation/publication, complete metadata scanning, and a
minimal consumer remain mandatory and were executed below.

## Observed release-build flag

`build.gradle` assigns `releaseBuild = version.contains("SNAPSHOT")`. Static
search found no consumer of `releaseBuild`; `snapshotBuild` is the flag used by
documentation tasks. It is recorded for review and left unchanged to avoid an
unrelated build-logic change before the required build gate.

## Local RELEASE publication

At `2026-07-27T15:43:17+0800`, the coordinator ran the incremental local
publication without `clean` or project tests:

```text
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 GRADLE_OPTS='-Xmx4g -Dfile.encoding=UTF-8 -Dorg.gradle.workers.max=3' ./gradlew publishToMavenLocal -x test -PbuildSrc.skipTests=true --max-workers=3
```

The command completed successfully in `4m 47s`: 173 actionable tasks, 135
executed and 38 up-to-date. Javadoc warnings were non-fatal. The remote Gradle
build cache returned HTTP 403 and was disabled; this did not affect local
publication.

The complete publication set contains 19 POMs: ACL, aspects, BOM, CAS, config,
core, crypto, data, LDAP, messaging, OAuth2 client/core/JOSE/resource server,
RSocket, SAML2 service provider, taglibs, test, and web. There are no approved
exclusions and no additional generated publication.

All 19 POMs were parsed with `xmllint`. Internal
`cn.bjca.footstone` dependencies, parents, and plugins ending in `-SNAPSHOT`:
`0`. Generated Framework dependencies use `6.2.19-nes.patch.1`.

An offline Maven consumer resolved
`bjca-footstone-bpring-security-core:6.5.11-nes.patch.1` successfully in
`2.203s`. Its internal graph contains Security core/crypto
`6.5.11-nes.patch.1` and Framework AOP, beans, context, core, JCL, and
expression `6.2.19-nes.patch.1`, with no internal SNAPSHOT. A first attempt
using the `security-data` representative stopped because the unrelated external
`org.springframework.data:spring-data-commons:3.4.13` was not cached in Maven
offline mode; no Nexus publication occurred.

## Release commit and Nexus publication

The dedicated release commit is
`98f4b638701eb38006412e90e327adbbb8b896cb`. It contains only the approved
version, Framework RELEASE dependency, component documentation, and this
OpenSpec change. No production source, test source, or unrelated build logic is
part of the release diff.

Immediately before deploy, all 19 publication POMs and 18 binary paths returned
HTTP 404 from Nexus RELEASE. The coordinator then ran exactly once, from the
release commit:

```text
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 GRADLE_OPTS='-Xmx4g -Dfile.encoding=UTF-8 -Dorg.gradle.workers.max=3' ./gradlew publishAllPublicationsToNexusRepository -x test -PbuildSrc.skipTests=true --max-workers=3
```

The deploy completed successfully in `30s`: 173 actionable tasks, 62 executed
and 111 up-to-date. It did not run `clean` or project tests.

Post-deploy verification downloaded every one of the 19 remote POMs and all 18
expected JARs. All assets were valid and every remote POM had zero internal
SNAPSHOT findings. The representative core checksums are:

- POM: `3a9829677f355d94da6f2f6f7ccb1ed5406fe0ddf808273973389423320d7ace`
- JAR: `8a9513f7ad21b8615e1f8b2fe7341741460540dfded73a9cb8aa83a7c65702eb`

An isolated Maven repository then resolved the core artifact only from Nexus
repositories with snapshots disabled. The dependency tree completed in
`24.450s` and resolved Security core/crypto `6.5.11-nes.patch.1` plus Framework
AOP, beans, context, core, JCL, and expression `6.2.19-nes.patch.1`. No artifact
was taken from the user's normal local Maven repository.

Annotated tag `v6.5.11-nes.patch.1` was created locally. Tag object
`8d93c716af84b9f57bb0a34f5d57460864a72f5f` peels exactly to the release
commit. GitHub commit/tag push and remote verification remain pending because
the known `github.com:443` connectivity blocker persists; Nexus must not be
redeployed when the Git operation is retried.
