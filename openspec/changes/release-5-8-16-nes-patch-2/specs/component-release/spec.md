## MODIFIED Requirements

### Requirement: Exclusive and safe component preparation

The release process MUST assign one active owner to `spring-security-5.8` and MUST inspect branch, origin, tracked and untracked changes, active OpenSpec changes, and the central release manifest before modifying `spring-security-5.8`. The only allowed active component change is `release-5-8-16-nes-patch-2`; `.claude/`, `.codex/`, and `.cursor/` MUST be excluded from staging, commits, and release evidence.

#### Scenario: Worktree is safe
- **WHEN** the owner lease is active, this change is apply-ready, and all tracked changes are covered by the approved release change
- **THEN** preparation may update the component release version and documentation

#### Scenario: Unrelated work exists
- **WHEN** unrelated tracked changes, a different active OpenSpec change, or another active owner are detected
- **THEN** preparation stops without cleaning, discarding, staging, or committing that work

### Requirement: Complete internal RELEASE metadata

Every generated RELEASE POM or BOM for `5.8.16-nes.patch.2` MUST use approved RELEASE versions for internal `cn.bjca.footstone` dependencies, parents, imported BOMs, and plugins. `spring-framework-5.3` at `5.3.39-nes.patch.1` MUST be recorded as `nexus-verified` in the central manifest before local preparation or deploy begins.

#### Scenario: Generated metadata is clean
- **WHEN** the complete 21-GAV local publication set contains no internal version ending in `-SNAPSHOT` and consumes the verified Spring Framework RELEASE
- **THEN** the metadata gate passes

#### Scenario: Internal SNAPSHOT or unverified upstream remains
- **WHEN** any generated publication contains an internal SNAPSHOT reference or the framework prerequisite is not `nexus-verified`
- **THEN** local verification fails and deploy is forbidden

### Requirement: Proportional local verification

The component MUST reuse valid archived development build, relevant security regression, Java 8, and consumer evidence when the covered source inputs remain unchanged. It MUST still complete the release-version local installation or effective-POM generation, scan every generated POM/BOM, and run representative local consumer validation against the release commit. A broad `make build` or broad test suite MUST NOT be required solely because the version changes.

#### Scenario: Minimum local gates pass
- **WHEN** the single-slot release-version install, complete metadata scan, and representative local consumer validation succeed
- **THEN** commands and evidence are recorded and the component may become `locally-verified`

#### Scenario: Reusable evidence is stale or a minimum gate fails
- **WHEN** covered source inputs changed after the archived evidence or a required release-specific gate fails or is missing
- **THEN** the component cannot be committed for release or deployed until the affected gate is completed

### Requirement: Auditable release commit and documentation

The component SHALL have one dedicated release commit containing `5.8.16-nes.patch.2`, verified internal RELEASE dependency updates, `README.adoc`, `doc/GAV_MAPPING.md`, approved exclusions, and no unrelated source changes. The release commit MUST be the exact input for deployment and annotated tagging.

#### Scenario: Release diff is approved
- **WHEN** the staged diff matches this OpenSpec and all local gates pass
- **THEN** the release commit SHA is recorded in the central manifest

#### Scenario: Documentation or diff is incomplete
- **WHEN** component documentation still presents the target as SNAPSHOT or the diff contains unapproved files, credentials, `.claude/`, `.codex/`, or `.cursor/`
- **THEN** release commit creation is blocked

### Requirement: Target absence before immutable deployment

Immediately before deployment, the coordinator MUST verify that all 21 catalog-defined POM/JAR GAVs for `5.8.16-nes.patch.2` are absent from Nexus RELEASE. A target is absent only when every required POM and binary is HTTP 404 and no checksum, metadata, or partial module asset exists.

#### Scenario: Complete target set is absent
- **WHEN** no POM, binary, checksum, metadata, or partial module asset exists for every target GAV
- **THEN** the coordinator may authorize one deploy after reviewing all other gates

#### Scenario: Any target asset exists
- **WHEN** Nexus contains any complete or partial target-version asset or returns an unexpected status
- **THEN** deploy is blocked and the existing assets are recorded for investigation without overwrite, deletion, retry, or redeploy

### Requirement: Coordinator-controlled deployment and remote verification

Only the coordinating main session SHALL execute the catalog deploy command after all preconditions pass. Every Make, Maven, and Gradle command MUST acquire the global build slot; every Gradle invocation MUST include `--no-daemon --no-parallel --max-workers=1`, and Maven MUST run without `-T`. After one deployment, the process MUST download and verify all expected POM and binary assets, metadata, checksums, and RELEASE-only consumer resolution.

#### Scenario: Remote release is valid
- **WHEN** all expected assets are downloadable, metadata is RELEASE-only, checksums are recorded, and the consumer smoke test passes
- **THEN** the component advances to `nexus-verified`

#### Scenario: Publication is incomplete or uncertain
- **WHEN** expected assets are missing, inconsistent, or only partially present
- **THEN** the component becomes `partial-failure`, is not redeployed at the same version, and cannot be tagged

### Requirement: Exact annotated release tag

After Nexus verification, the release CLI MUST create annotated tag `v5.8.16-nes.patch.2` on the exact release commit used to build and deploy `5.8.16-nes.patch.2`, push the approved release commit and tag to `origin`, and independently verify the remote branch SHA, tag object, and peeled tag target. No ceremonial TAG/PUSH user confirmation is required after the specified gates pass.

#### Scenario: Tag and push are valid
- **WHEN** the annotated tag points to the recorded release commit and both commit and tag are verified on `origin`
- **THEN** tag object IDs and remote evidence are recorded

#### Scenario: Nexus is not verified
- **WHEN** remote artifact verification is incomplete or failed
- **THEN** no release tag is created or pushed

### Requirement: Explicit exclusions

The release process MUST honor and verify these exclusions: none. Local tooling directories `.claude/`, `.codex/`, and `.cursor/` are not release content and MUST remain excluded from staging, commit, publish inventory, and evidence.

#### Scenario: Exclusions are honored
- **WHEN** local publications, deploy tasks, and remote assets omit every excluded item and no included POM depends on it
- **THEN** exclusion verification passes

#### Scenario: Excluded content is present
- **WHEN** an excluded module is published or referenced by an included RELEASE POM, or a local tooling directory is staged
- **THEN** finalization fails and the immutable failure procedure applies

### Requirement: Evidence-backed documentation and archive

The component OpenSpec MUST remain active until Nexus assets, remote Git references, component documentation, and the central run manifest agree. The lifecycle MUST progress through `openspec-ready`, `prepared`, `locally-verified`, `release-committed`, `deployed`, `nexus-verified`, `tagged`, and `documented` before archive; the owner lease MUST be released after all component writes complete.

#### Scenario: Completion evidence agrees
- **WHEN** the component is `nexus-verified`, tagged, remotely verified, documented, and reconciled in the manifest
- **THEN** the OpenSpec change may be archived and its archive path and final `archived` state recorded

#### Scenario: Evidence is missing or mismatched
- **WHEN** any required Git, Nexus, documentation, or manifest evidence is absent or inconsistent
- **THEN** archive is blocked

### Requirement: Immutable partial-failure handling

Existing RELEASE assets MUST NOT be overwritten, deleted, retried, or blindly redeployed.

#### Scenario: Partial assets exist
- **WHEN** a failed or interrupted deployment leaves any target-version asset in Nexus
- **THEN** the component records `partial-failure` and requires a newly approved NES patch version

#### Scenario: Nexus succeeded but Git push failed
- **WHEN** Nexus verification is complete and only commit or tag push failed
- **THEN** the release CLI retries only the Git operation without redeploying

## ADDED Requirements

### Requirement: Automated compliance gate

The release process MUST run available legal and open-source compliance checks automatically before deployment and record sanitized results. A clean result MUST advance without a user acknowledgement; a concrete license, CVE, policy, or credential risk MUST block release and be reported.

#### Scenario: Compliance gate is clean
- **WHEN** the automated compliance checks complete without a concrete risk
- **THEN** the release may proceed to the Nexus absence gate without user confirmation

#### Scenario: Compliance gate finds a risk
- **WHEN** a concrete legal, open-source, CVE, policy, or credential risk is found
- **THEN** release state remains blocked and the specific risk is reported
