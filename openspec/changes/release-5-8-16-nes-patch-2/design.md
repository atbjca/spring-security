## Context

The branch `5.8.x-bjca-patch` currently contains the completed security backports and dependency-publication work at `5.8.16-nes.patch.2-SNAPSHOT`. The prior immutable release is `5.8.16-nes.patch.1`. The release catalog defines one internal dependency, `spring-framework-5.3`, whose `5.3.39-nes.patch.1` RELEASE is already `nexus-verified` with no internal SNAPSHOT metadata. The target publication set contains 21 POM/JAR GAVs under `cn.bjca.footstone.bpring.security` and has no exclusions.

The development line already has reusable evidence from the completed security gate and Java 8 Maven/Gradle consumers. The release-specific work therefore focuses on freezing the version, proving generated RELEASE metadata, validating a representative local consumer, publishing exactly once after a complete Nexus absence check, and reconciling Nexus/Git/OpenSpec/manifest evidence.

The user's machine is resource-constrained. Git, documentation, OpenSpec, and read-only Nexus checks may be performed concurrently when their inputs are independent, but all Make, Maven, and Gradle commands acquire one global build slot. No build command may use Gradle parallelism or more than one worker.

## Goals / Non-Goals

**Goals:**

- Produce `5.8.16-nes.patch.2` from one reviewed release commit on `5.8.x-bjca-patch`.
- Consume only the Nexus-verified Spring Framework RELEASE and prohibit internal SNAPSHOT references in all generated publications.
- Reuse valid development evidence and run only the minimum release-specific local install, POM/BOM scan, and representative consumer checks before deployment.
- Serialize every build command with a global build slot and explicit Gradle `--no-daemon --no-parallel --max-workers=1` flags.
- Require complete Nexus RELEASE absence for all 21 publications before one deploy, and treat RELEASE coordinates as immutable.
- Automatically run legal/open-source compliance checks and surface only concrete risks for human attention.
- Let the release CLI perform approved TAG/PUSH operations directly after all gates pass, without ceremonial user confirmations.
- Keep credentials and `.claude/`, `.codex/`, `.cursor/` out of commits and evidence.

**Non-Goals:**

- No source-code fixes, dependency upgrades, feature work, refactors, or next-SNAPSHOT advancement.
- No broad `make build`, broad test suite, or repeated development verification when archived evidence remains valid.
- No overwrite, delete, partial retry, or redeploy of any existing or partially existing RELEASE coordinate.
- No concurrent Maven/Gradle/Make invocation, even when different repositories are otherwise independent.

## Decisions

### 1. Create a release-specific OpenSpec and state machine

Use this change as the sole active OpenSpec for the component and advance the manifest through `planned -> openspec-ready -> prepared -> locally-verified -> release-committed -> deployed -> nexus-verified -> tagged -> documented -> archived`. A missing, contradictory, or partial checkpoint blocks progression. This keeps a resumed session from mistaking a prior SNAPSHOT or archived preparation change for a RELEASE.

### 2. Reuse development evidence, retain release-specific gates

The archived Java 8 security consumer and targeted regression evidence is reusable because it covers the current production sources and the current candidate line. The release still performs `make install` (or its exact single-slot equivalent), scans every generated POM/BOM, and runs a representative consumer against only the local RELEASE publications plus the verified framework RELEASE. This is faster than a broad rebuild while still checking the version-dependent publication boundary.

Alternative considered: rerun `make build` and all tests. Rejected because it duplicates already valid evidence, violates the requested throughput constraint, and does not add release-coordinate coverage.

### 3. Enforce one global build slot

Acquire a workspace-wide lock before every Make, Maven, or Gradle command and release it only after the command exits. Pass `--no-daemon --no-parallel --max-workers=1` to every Gradle invocation, including nested verification scripts and consumers. Maven commands are single-process without `-T`. Documentation, Git, OpenSpec, and HTTP reads may run concurrently only when they do not overlap a build write or mutate the same evidence file.

Alternative considered: one build per independent component in parallel. Rejected because local CPU/memory pressure and shared Gradle/Maven caches can make release results unreliable.

### 4. Validate the dependency graph before touching Nexus

Require the central manifest to show `spring-framework-5.3` at `nexus-verified`, then check the generated publication metadata for internal SNAPSHOT versions and unexpected internal coordinates. The framework BOM and all first-party framework assets are read-only prerequisites; this release must never redeploy them.

### 5. Fail closed on Nexus RELEASE existence

Enumerate the catalog's complete 21-publication set and query both POM and binary assets (POM-only publications have no binary) immediately before deployment. Any 2xx asset, checksum, metadata, or partial module result blocks the release. Only a complete absence permits one deploy. After deployment, retries are limited to verification reads; corrections require a new NES patch version.

### 6. Separate irreversible operations from evidence reconciliation

Version preparation, local verification, documentation, and the release commit are reversible and component-owned. Nexus deploy, Git push, and tag creation occur only after the coordinator sees the recorded absence and local evidence. TAG/PUSH commands are executed directly by the release CLI once their preconditions are satisfied, then remote refs are independently checked. No credentials are printed or copied into evidence.

### 7. Automate compliance assessment

Run the repository's available legal/open-source checks and record their exit status and sanitized findings in the release evidence. A clean result advances automatically; a concrete license, CVE, or policy risk stops the release and is reported to the coordinator. A human confirmation is not required merely to acknowledge a clean gate.

## Risks / Trade-offs

- **Existing or partial Nexus assets** -> Query all 21 GAVs before deploy; never overwrite or redeploy a conflicted version.
- **Hidden internal SNAPSHOT in a secondary POM/BOM** -> Scan every generated and downloaded POM/BOM, not only the representative core artifact.
- **Wrong upstream lifecycle state** -> Require the manifest's framework dependency to be `nexus-verified` and record its version before preparation.
- **Concurrent builds exhaust the workstation or corrupt shared caches** -> Use a global build lock and one Gradle worker with parallelism disabled.
- **A resumed session repeats an irreversible action** -> Make manifest state, Nexus absence, deployment result, and remote Git refs explicit idempotency checks.
- **Local tooling directories enter the release commit** -> Explicitly exclude `.claude/`, `.codex/`, and `.cursor/` during staging and diff review.
- **Compliance issue is hidden by a broad pass/fail summary** -> Preserve sanitized command output and report concrete findings before state transition.

## Migration Plan

1. Validate the catalog, upstream framework manifest, branch/worktree, and active OpenSpec; acquire the component lease.
2. Freeze the version to `5.8.16-nes.patch.2`, update only release documentation and required OpenSpec evidence, and perform the single-slot local publication/POM/consumer checks.
3. Create and review the dedicated release commit; record its SHA in the manifest.
4. Run complete Nexus absence and compliance checks. If all pass, execute one single-slot RELEASE deploy.
5. Verify all expected Nexus assets and checksums, run a RELEASE-only consumer, create/push the annotated tag and branch, and reconcile remote refs.
6. Update component and central documentation, validate/archive this OpenSpec, release the lease, and record the archive path.

Before deployment, rollback means restoring the working version and discarding only uncommitted release-preparation changes. After any successful Nexus upload, rollback is prohibited; preserve the immutable artifacts and issue a new NES patch for corrections.

## Open Questions

- The existing Gradle build file assigns `releaseBuild = version.contains("SNAPSHOT")`, which appears semantically inverted but is currently unused outside that assignment. Confirm during static preparation that it does not affect publication behavior; fix only if the release gate proves it is relevant.
- The central release manifest must provide the concrete owner lease and evidence paths at apply time; this change defines the required checkpoints but does not edit `nes-docs`.
