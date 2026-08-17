## Why

`spring-security-6.5` now contains dependency-security and Jackson maintenance changes under `6.5.11-nes.patch.2-SNAPSHOT`, so those changes require a distinct immutable RELEASE rather than reuse of patch 1 coordinates. The release must be dependency-safe, auditable across sessions, and executable without concurrent or multi-worker build activity on the constrained release workstation.

## What Changes

- Promote the component from `6.5.11-nes.patch.2-SNAPSHOT` to `6.5.11-nes.patch.2` while retaining the upstream identity `springSecurityVersion=6.5.11`.
- Require both actual internal upstreams, Spring Framework `6.2.19-nes.patch.1` and Spring Data Commons `3.5.13-nes.patch.1`, to be verified RELEASE baselines before local publication or deploy.
- Reconcile the stale `planned` Spring Data Commons entry in the `nes-release-2026-08-14` manifest against its already published Nexus RELEASE before allowing `spring-security-6.5` to advance.
- Publish and verify the complete 19-GAV Spring Security set with no internal SNAPSHOT metadata and no exclusions.
- Reuse the focused development verification already recorded for the dependency-remediation and Jackson changes when no later source or test change invalidates it; do not require broad `make build` or `make test` reruns solely for release preparation.
- Serialize every Maven, Gradle, and Make invocation through the global build slot; execute Gradle with `--no-daemon --no-parallel --max-workers=1`.
- Verify complete Nexus target absence immediately before the single deploy, never overwrite or redeploy an existing RELEASE, and use a new NES patch after any partial immutable publication.
- Let the release CLI handle deployment authorization, annotated tag creation, and push tokens internally without repeated operator prompts; record evidence after each irreversible action.
- Exclude `.claude/`, `.codex/`, `.cursor/`, credentials, and unrelated files from all staging and release evidence.
- Perform the legal and open-source compliance review automatically and interrupt the release only when a concrete risk is found.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `component-release`: Advance the Spring Security 6.5 immutable release contract to patch 2, add the actual Spring Data Commons upstream gate, enforce globally serialized single-worker Gradle execution, and cover the complete 19-GAV release set.

## Impact

- **Repository:** `spring-security-6.5`
- **Release version/tag:** `6.5.11-nes.patch.2` / `v6.5.11-nes.patch.2`
- **Publication set:** 19 GAVs under `cn.bjca.footstone.bpring.security`
- **Internal RELEASE dependencies:** `spring-framework-6.2` `6.2.19-nes.patch.1`; `spring-data-commons-3.5` `3.5.13-nes.patch.1`
- **Central records:** `nes-docs/release/catalog/components.json` and `nes-docs/release/runs/nes-release-2026-08-14/manifest.json`
- **Release documentation:** `README.adoc`, `doc/QUICK_START.md`, `doc/USER_MANUAL.md`, `doc/GAV_MAPPING.md`, and other current-version references found during preparation
- **External systems:** Nexus RELEASE and the repository's configured `origin`
- **Source behavior:** No application source or API change is introduced by this release change
