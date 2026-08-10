## Why

`6.5.11-nes.patch.1` has already been published and tagged as an immutable RELEASE, while the newly completed runtime dependency CVE remediation and Jackson baseline update change the generated POM/BOM dependency graph. Continued development therefore needs a distinct NES patch version before any new artifact can be published.

## What Changes

- Advance the project development version to `6.5.11-nes.patch.2-SNAPSHOT`.
- Keep `springSecurityVersion=6.5.11` because the upstream Spring Security code baseline is unchanged.
- Update maintenance documentation so the next development target is distinct from the already published `6.5.11-nes.patch.1` release.
- Verify generated project coordinates and representative publication metadata use the patch 2 SNAPSHOT version.
- Exclude Nexus deployment, release tagging, and promotion to `6.5.11-nes.patch.2` RELEASE from this preparation change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `component-release`: Require post-release maintenance work to increment the NES patch version while preserving the upstream runtime identity version.

## Impact

- Build metadata: `gradle.properties` and generated project/POM/BOM coordinates.
- Documentation: maintenance requirements and current development-baseline references.
- OpenSpec: the component release contract gains an explicit immutable-version succession requirement.
- No Spring Security API, implementation, dependency version, Nexus asset, Git tag, or upstream runtime identity change.
