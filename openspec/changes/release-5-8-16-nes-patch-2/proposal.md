## Why

`spring-security-5.8` has completed its patch 2 security and compatibility work but remains a `5.8.16-nes.patch.2-SNAPSHOT`. It needs an immutable, auditable RELEASE lifecycle before downstream NES components can adopt it.

## What Changes

- Freeze and release `5.8.16-nes.patch.2` from one dedicated Git commit.
- Use only the Nexus-verified `spring-framework-5.3:5.3.39-nes.patch.1` internal dependency.
- Reuse commit-correlated development evidence where valid; run only the release-version local publication, metadata scan, and consumer gates that remain necessary.
- Require every Gradle invocation to use `--no-daemon --no-parallel --max-workers=1` and keep all Maven, Gradle, Make, and Nexus actions in the global serial build slot.
- Verify complete target absence before the one deploy, then verify remote assets, RELEASE-only consumption, annotated tag, documentation, manifest, and archive.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `component-release`: Define the patch 2 RELEASE coordinates, serial Gradle execution constraint, reusable validation evidence, and immutable Git/Nexus finalization requirements.

## Impact

- **Repository:** `spring-security-5.8`
- **Release version:** `5.8.16-nes.patch.2`
- **Publication set:** 21 cataloged GAVs under `cn.bjca.footstone.bpring.security`
- **Upstream dependency:** `spring-framework-5.3:5.3.39-nes.patch.1`
- **External systems:** Nexus RELEASE, GitHub `origin`, and the central NES release manifest
- **Credentials:** Remain only in user-level Maven/Gradle configuration
