## ADDED Requirements

### Requirement: Actual internal upstreams are reconciled before release
The release process MUST declare and verify both actual internal upstreams: Spring Framework `6.2.19-nes.patch.1` and Spring Data Commons `3.5.13-nes.patch.1`. An already published baseline whose current-run manifest entry is stale MUST be reconciled through live Nexus evidence before `spring-security-6.5` advances beyond `planned`.

#### Scenario: Both upstream baselines are verified
- **WHEN** Spring Framework is `nexus-verified` and Spring Data Commons has complete RELEASE assets, checksums, RELEASE-only metadata, and a reconciled `nexus-verified` baseline manifest entry
- **THEN** dependency preparation may advance to `dependency-reconciled`

#### Scenario: Manifest or Nexus evidence is incomplete
- **WHEN** either actual upstream lacks live RELEASE evidence or its manifest state does not represent that evidence
- **THEN** Spring Security local publication, release commit, and deploy are blocked

### Requirement: Build-tool execution is globally serialized
Every Make, Maven, or Gradle command in the release train MUST hold the single global build slot. Every Gradle invocation MUST include `--no-daemon --no-parallel --max-workers=1`, regardless of project defaults or wrapper scripts.

#### Scenario: Serialized Gradle command is authorized
- **WHEN** the component owns the global build slot and the exact command contains all required Gradle serialization flags
- **THEN** the command may execute and the slot is released after completion or failure

#### Scenario: Concurrent or multi-worker execution is requested
- **WHEN** another build-tool process holds the slot or any required Gradle serialization flag is absent
- **THEN** execution is blocked without starting Gradle

### Requirement: Complete Spring Security publication set
The RELEASE process MUST discover, publish, and verify exactly the approved 19-GAV Spring Security publication set at `6.5.11-nes.patch.2`, with no exclusions and no unexpected publication.

#### Scenario: Complete set matches
- **WHEN** the local and remote inventories contain the BOM plus the 18 approved JAR publications under `cn.bjca.footstone.bpring.security`
- **THEN** the publication-set gate passes

#### Scenario: Set is incomplete or contains an unexpected GAV
- **WHEN** any approved GAV is absent or any unapproved GAV is present
- **THEN** release progression stops and deploy or finalization is forbidden

### Requirement: Automated legal and open-source compliance gate
The coordinator MUST inspect release-relevant licenses, notices, third-party dependency changes, source/binary publication shape, credential isolation, and known disclosure risks. It MUST request operator input only when a concrete compliance risk requires a decision.

#### Scenario: No concrete compliance risk exists
- **WHEN** the automated review finds no new or unresolved licensing, notice, disclosure, or credential issue
- **THEN** the compliance gate passes without a manual confirmation prompt

#### Scenario: Concrete compliance risk exists
- **WHEN** the review finds a new incompatible license, missing required notice, credential exposure, or unresolved disclosure risk
- **THEN** the release is blocked and the finding is reported before any deploy

### Requirement: CLI-internal tag and push authorization
After objective Nexus and Git prerequisites pass, the release CLI SHALL generate and consume the exact TAG and PUSH authorization internally and SHALL record the resulting local and remote evidence without repeated operator confirmation.

#### Scenario: Nexus and release commit are verified
- **WHEN** state is `nexus-verified`, the release commit is unchanged, and the target tag is absent
- **THEN** the CLI creates, pushes, and verifies the annotated tag and approved branch using internal authorization

#### Scenario: A prerequisite is not satisfied
- **WHEN** Nexus verification, commit identity, target absence, or remote safety is uncertain
- **THEN** the CLI does not create or push a tag

## MODIFIED Requirements

### Requirement: Exclusive and safe component preparation
The release process MUST assign one active owner to `spring-security-6.5` and MUST inspect branch, origin, tracked and untracked changes, active OpenSpec changes, central manifest state, and global build-slot ownership before modifying `spring-security-6.5`. Local tool directories `.claude/`, `.codex/`, and `.cursor/` MUST remain excluded from release staging.

#### Scenario: Worktree is safe
- **WHEN** the owner lease is active, the global build slot is free or owned for the current build command, and all tracked changes are covered by the approved release change
- **THEN** preparation may update the component release version and documentation

#### Scenario: Unrelated work exists
- **WHEN** unrelated tracked changes, another active owner, or conflicting central-record writes are detected
- **THEN** preparation stops without cleaning, discarding, staging, or committing that work

#### Scenario: Tool directories are untracked
- **WHEN** `.claude/`, `.codex/`, or `.cursor/` entries exist in the worktree
- **THEN** they are recorded as excluded local tooling and never staged or committed

### Requirement: Complete internal RELEASE metadata
Every generated RELEASE POM or BOM for `6.5.11-nes.patch.2` MUST use approved RELEASE versions for internal `cn.bjca.footstone` dependencies, parents, imported BOMs, and plugins, including Spring Framework `6.2.19-nes.patch.1` and Spring Data Commons `3.5.13-nes.patch.1`.

#### Scenario: Generated metadata is clean
- **WHEN** the complete 19-publication local set contains no internal version ending in `-SNAPSHOT` and contains the approved internal upstream versions
- **THEN** the metadata gate passes

#### Scenario: Internal SNAPSHOT or wrong upstream remains
- **WHEN** any generated publication contains an internal SNAPSHOT or an unapproved internal upstream version
- **THEN** local verification fails and deploy is forbidden

### Requirement: Proportional local verification
The component MUST bind approved development build/test evidence to the source, test, and dependency inputs of the patch 2 release commit. When no intervening change invalidates that evidence, broad build/test reruns SHALL be omitted; local publication, complete POM/BOM scan, and representative consumer validation remain mandatory and MUST execute under the global build slot with single-worker Gradle flags.

#### Scenario: Development evidence remains valid
- **WHEN** focused dependency-remediation and Jackson verification is recorded and no later source, test, or dependency-baseline change invalidates it
- **THEN** the evidence is reused and no broad `make build` or `make test` rerun is required

#### Scenario: Evidence is invalidated
- **WHEN** a later source, test, or dependency-baseline change affects previously verified behavior
- **THEN** only the invalidated focused verification is rerun before local release gates continue

#### Scenario: Release-specific local gates pass
- **WHEN** serialized local publication, the complete metadata scan, and local RELEASE consumer validation succeed
- **THEN** commands and evidence are recorded and the component may become `locally-verified`

#### Scenario: A local gate fails
- **WHEN** a required release-specific command fails or is skipped
- **THEN** the component cannot be committed for release or deployed

### Requirement: Auditable release commit and documentation
The component SHALL have one dedicated release commit containing `6.5.11-nes.patch.2`, `springSecurityVersion=6.5.11`, approved internal RELEASE dependencies, current component release documentation, OpenSpec evidence, and no unrelated source or tool-directory changes.

#### Scenario: Release diff is approved
- **WHEN** the staged diff matches this OpenSpec, includes the target version and current release documentation, excludes unrelated files and credentials, and all local gates pass
- **THEN** the release commit SHA is recorded in the central manifest

#### Scenario: Documentation or diff is incomplete
- **WHEN** current documentation still presents patch 2 as SNAPSHOT, advertises patch 1 as the new target, or the diff contains unapproved files
- **THEN** release commit creation is blocked

### Requirement: Target absence before immutable deployment
Immediately before deployment, the coordinator MUST verify that every expected POM, BOM, JAR, sources JAR, Javadoc JAR, checksum, metadata path, and partial asset for all 19 target GAVs is absent from Nexus RELEASE at `6.5.11-nes.patch.2`.

#### Scenario: Complete target set is absent
- **WHEN** no complete or partial target-version asset exists for any of the 19 GAVs
- **THEN** the coordinator may authorize the single deploy after reviewing all other gates

#### Scenario: Any target asset exists
- **WHEN** Nexus contains any complete or partial `6.5.11-nes.patch.2` asset
- **THEN** deploy is blocked and the existing assets are recorded for immutable-state investigation

### Requirement: Coordinator-controlled deployment and remote verification
Only the coordinating session SHALL execute the single Nexus publication from the recorded release commit, while holding the global build slot and using Gradle `--no-daemon --no-parallel --max-workers=1` without `clean` or tests. After deployment, it MUST verify the complete 19-GAV remote set, downloaded POM/BOM metadata, checksums, exclusions, and RELEASE-only consumer resolution.

#### Scenario: Remote release is valid
- **WHEN** all expected assets are downloadable, all internal metadata is RELEASE-only, checksums are recorded, and the RELEASE-only consumer passes
- **THEN** the component advances to `nexus-verified`

#### Scenario: Publication is incomplete or uncertain
- **WHEN** expected assets are missing, inconsistent, unexpected, or only partially present
- **THEN** the component becomes `partial-failure`, is not redeployed at the same version, and cannot be tagged

### Requirement: Exact annotated release tag
After Nexus verification, the coordinator MUST create annotated tag `v6.5.11-nes.patch.2` on the exact release commit used to build and deploy `6.5.11-nes.patch.2`, then verify the remote branch, tag object, and peeled commit.

#### Scenario: Tag and push are valid
- **WHEN** the annotated tag points to the recorded release commit and both commit and tag are verified on `origin`
- **THEN** tag object IDs and remote evidence are recorded and the component may advance to `tagged`

#### Scenario: Nexus is not verified
- **WHEN** remote artifact verification is incomplete or failed
- **THEN** no release tag is created or pushed

### Requirement: Immutable releases advance to a new NES patch development version
After an NES RELEASE has been published or tagged, subsequent dependency, code, or publication metadata changes MUST use a higher NES patch version and MUST NOT reuse immutable RELEASE coordinates. The project artifact version and upstream Spring Security identity version MUST remain independently managed.

#### Scenario: Patch 2 is released
- **WHEN** `6.5.11-nes.patch.2` is published or tagged and later maintenance changes are integrated
- **THEN** continued development uses `6.5.11-nes.patch.3-SNAPSHOT` or a later explicitly approved NES patch SNAPSHOT
- **THEN** no artifact is published again as patch 1 or patch 2

#### Scenario: Upstream code baseline is unchanged
- **WHEN** only NES maintenance changes are added without rebasing to a different upstream Spring Security release
- **THEN** `springSecurityVersion` remains `6.5.11`
- **THEN** generated artifact coordinates use the incremented NES patch version

#### Scenario: Historical release evidence is reviewed
- **WHEN** maintainers update current development or release documentation
- **THEN** patch 1 and patch 2 evidence remains attributable to the correct immutable release
- **THEN** current-development references identify the next approved version rather than rewriting historical evidence
