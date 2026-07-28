## Why

`spring-security-5.8` currently targets `5.8.16-nes.patch.1` but does not yet have a complete, immutable, and independently auditable RELEASE lifecycle. The release must survive session loss, preserve unrelated work, and publish metadata that references only approved internal RELEASE dependencies.

## What Changes

- Freeze and inventory the `spring-security-5.8` worktree before release edits.
- Create RELEASE metadata for `5.8.16-nes.patch.1` and replace all internal SNAPSHOT references with approved RELEASE versions.
- Run the component-specific build, tests, local publication/effective-POM scan, and consumer verification.
- Form a dedicated release commit containing component release documentation and no unrelated changes.
- Verify every discovered target GAV is absent from Nexus RELEASE before the coordinator-authorized deploy.
- Download and verify published POM/binary assets and checksums from Nexus, then run RELEASE-only consumption.
- Create annotated tag `v5.8.16-nes.patch.1` on the exact release commit only after Nexus verification.
- Record Git, Nexus, documentation, and manifest evidence before archiving this change.

## Capabilities

### New Capabilities

- `component-release-spring-security-5.8`: Release `spring-security-5.8` `5.8.16-nes.patch.1` with reproducible local gates, immutable Nexus publication, exact Git tagging, documentation, and archive evidence.

### Modified Capabilities

None.

## Impact

- **Repository:** `spring-security-5.8`
- **Release version:** `5.8.16-nes.patch.1`
- **Representative GAVs:** `cn.bjca.footstone.bpring.security:bjca-footstone-bpring-security-core:5.8.16-nes.patch.1`
- **Internal RELEASE dependencies:** `spring-framework-5.3`
- **Explicit exclusions:** none
- **Release documentation:** `README.adoc`, `doc/GAV_MAPPING.md`
- **External systems:** Nexus RELEASE and the repository's configured `origin`
- **Credentials:** Remain exclusively in user-level Gradle/Maven configuration and are never copied into this change or Git
