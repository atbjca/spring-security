## Why

`5.8.16-nes.patch.1` is an immutable release that already has a Nexus-verified tag, but the branch remained on that release version while accumulating the next set of security fixes. Continuing development or candidate publication under the released coordinate risks an attempted overwrite and makes generated evidence identify the wrong lifecycle state.

## What Changes

- Set the branch development version to `5.8.16-nes.patch.2-SNAPSHOT`.
- Make candidate verification derive the current Gradle project version instead of hard-coding `5.8.16-nes.patch.1`.
- Update live release, GAV, Nexus, and CVE traceability documentation to distinguish the immutable patch.1 release from the current patch.2-SNAPSHOT development line.
- Keep archived patch.1 release evidence and the `v5.8.16-nes.patch.1` tag unchanged.
- Reserve removal of `-SNAPSHOT`, Nexus release deployment, and creation of `v5.8.16-nes.patch.2` for a later explicit release change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `component-release`: Require the maintained branch to advance to the next SNAPSHOT after an immutable release and prevent reuse of a released coordinate.
- `published-dependency-security`: Require candidate verification and evidence generation to use the current declared project version rather than a stale hard-coded release version.
- `security-advisory-traceability`: Identify patch.2-SNAPSHOT as the current candidate while retaining patch.1 as the historical immutable release.

## Impact

- Build metadata: `gradle.properties` and generated candidate repository paths.
- Publication verification: `scripts/verify-published-security.sh` and its evidence output.
- Documentation: current release identity, GAV mapping, Nexus deployment instructions, and 2026 CVE traceability.
- No runtime API, dependency baseline, CVE implementation, Java target, or published patch.1 artifact changes.
