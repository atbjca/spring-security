## 1. Internal dependency reconciliation

- [ ] 1.1 Confirm the central catalog target is `spring-security-6.5` `6.5.11-nes.patch.2` with the complete 19-GAV publication list, wave 1, no exclusions, and dependencies on both `spring-framework-6.2` and `spring-data-commons-3.5`.
- [ ] 1.2 Confirm the 2026-08-14 manifest records Spring Framework `6.2.19-nes.patch.1` as a live `nexus-verified` baseline with complete-set and zero-internal-SNAPSHOT evidence; do not deploy or retag it.
- [ ] 1.3 Perform live read-only Nexus verification of the already published Spring Data Commons `3.5.13-nes.patch.1` POM, main JAR, sources JAR, and Javadoc JAR; record immutable URLs, timestamps, SHA-256 checksums, and downloaded POM metadata.
- [ ] 1.4 Scan the downloaded Spring Data Commons POM for internal SNAPSHOT references and verify its Spring Framework references use approved RELEASE coordinates.
- [ ] 1.5 Reconcile `spring-data-commons-3.5` in `release/runs/nes-release-2026-08-14/manifest.json` from stale `planned` to verification-only `nexus-verified`, recording the live assets/evidence and explicitly prohibiting deploy or retag in this run.
- [ ] 1.6 Update and validate the central catalog dependency topology and safe local/deploy command records so fresh sessions cannot omit Spring Data Commons or invoke parallel Gradle defaults.
- [ ] 1.7 Advance `spring-security-6.5` to `dependency-reconciled` only after both internal upstream baselines and the catalog/manifest topology agree.

## 2. Worktree freeze, ownership, and compliance

- [ ] 2.1 Confirm this change is apply-ready, acquire the exclusive `spring-security-6.5` owner lease, and record owner/session/timestamps in the central manifest.
- [ ] 2.2 Record branch, HEAD, `origin` fetch/push URLs, tracking state, active OpenSpec changes, Java/Gradle toolchain, current version `6.5.11-nes.patch.2-SNAPSHOT`, target version, tag, and all 19 target GAVs without exposing credentials.
- [ ] 2.3 Inventory tracked, staged, ignored, and untracked paths; stop on unrelated tracked work and record `.claude/`, `.codex/`, and `.cursor/` as excluded local tooling that must never be staged.
- [ ] 2.4 Verify no other session is writing this component or the same central manifest fields, while allowing disjoint read-only, documentation, or Git inspection work to continue concurrently.
- [ ] 2.5 Run the automatic legal/open-source compliance review for license/notice changes, dependency-license changes, source/binary publication shape, credential isolation, and unresolved disclosure risks; proceed without operator confirmation only when no concrete risk exists.
- [ ] 2.6 Create the component evidence skeleton, record both upstream baseline references and the no-exclusions policy, and advance the manifest to `openspec-ready`.

## 3. RELEASE metadata and documentation preparation

- [ ] 3.1 Set only the project artifact version to `6.5.11-nes.patch.2` and retain `springSecurityVersion=6.5.11`; do not change application source, tests, dependency baselines, or the next development version.
- [ ] 3.2 Verify every internal dependency, parent, imported BOM, constraint, and plugin used in generated publications is an approved RELEASE, including Spring Framework `6.2.19-nes.patch.1` and Spring Data Commons `3.5.13-nes.patch.1`.
- [ ] 3.3 Reconcile Gradle project/publication configuration with the expected BOM plus 18 JAR publications and confirm no unexpected or excluded publication exists.
- [ ] 3.4 Update `README.adoc`, `doc/QUICK_START.md`, `doc/USER_MANUAL.md`, `doc/GAV_MAPPING.md`, and other current-version references so patch 2 is the target RELEASE while patch 1 remains clearly historical.
- [ ] 3.5 Document the two required internal RELEASE baselines, single-worker Gradle commands, local/remote consumer gates, Nexus location, no-exclusions policy, immutable failure handling, and 19-GAV inventory.
- [ ] 3.6 Review the complete diff and verify it contains only release version, release documentation, OpenSpec/evidence, and approved central-record changes, with no credentials or unrelated source changes; advance to `prepared`.

## 4. Development evidence reuse and local release gates

- [ ] 4.1 Bind the archived dependency-remediation, Jackson, dependency-graph, and focused affected-module test evidence to the verified source commit and list the files/inputs covered by that evidence.
- [ ] 4.2 Compare the verified commit with the release candidate and confirm no intervening source, test, or dependency-baseline change invalidates the focused evidence; record that broad `make build` and `make test` are intentionally not rerun.
- [ ] 4.3 If an invalidating change is found, run only the affected focused verification while holding the global build slot; every Gradle command must include `--no-daemon --no-parallel --max-workers=1`.
- [ ] 4.4 Acquire the single global build slot and publish locally without `clean` or tests using `JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 ./gradlew --no-daemon --no-parallel --max-workers=1 publishToMavenLocal -x test -PbuildSrc.skipTests=true`.
- [ ] 4.5 Release the global build slot after local publication, including on failure, and record command, start/end timestamps, exit status, sanitized output, and the discovered complete publication inventory.
- [ ] 4.6 Scan every locally generated POM/BOM for internal `-SNAPSHOT` references, unapproved internal versions, missing Spring Framework/Spring Data Commons RELEASE metadata, and publication-set mismatches.
- [ ] 4.7 Acquire the global build slot and run a representative local consumer against the prepared patch 2 RELEASE and approved upstream RELEASEs only; if Maven is used, run one non-`-T` process, then release the slot.
- [ ] 4.8 Record consumer dependency-tree evidence proving no internal SNAPSHOT, official duplicate Spring implementation, or unintended repository is selected.
- [ ] 4.9 Recheck worktree ownership and the release diff, then advance to `locally-verified` only when all release-specific local gates pass.

## 5. Dedicated release commit and branch publication

- [ ] 5.1 Stage only paths covered by this OpenSpec using explicit path lists; exclude `.claude/`, `.codex/`, `.cursor/`, caches, credentials, generated local artifacts, and unrelated user files.
- [ ] 5.2 Inspect the staged diff for target version, upstream RELEASEs, 19-GAV metadata, current documentation, OpenSpec evidence, no exclusions, and absence of source/test changes not covered by reusable evidence.
- [ ] 5.3 Create the dedicated `release: prepare spring-security 6.5.11-nes.patch.2` commit and record its SHA as the sole build/deploy/tag source.
- [ ] 5.4 Verify the worktree and local publications correspond to the recorded commit, advance to `release-committed`, and let the release CLI push the approved branch commit without an additional operator confirmation.
- [ ] 5.5 Verify the remote branch SHA after push; if push fails, preserve local state and retry only Git without repeating local publication.

## 6. Complete Nexus absence and single deploy

- [ ] 6.1 From the recorded release commit, verify the expected complete 19-GAV publication set and construct the full Nexus RELEASE asset checklist for POM/BOM, main, sources, Javadoc, checksum, metadata, and possible partial paths.
- [ ] 6.2 Query every checklist path immediately before deploy and record a timestamped absence result tied to the release commit.
- [ ] 6.3 Block the release if any complete or partial `6.5.11-nes.patch.2` asset exists; record immutable evidence and do not overwrite, delete, or redeploy the version.
- [ ] 6.4 Have the coordinating session review dependency reconciliation, ownership, compliance, reusable verification, local publication, POM scan, consumer, release commit, remote branch, and complete absence evidence.
- [ ] 6.5 Acquire the global build slot and execute the Nexus publication exactly once from the release commit using `JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 ./gradlew --no-daemon --no-parallel --max-workers=1 publishAllPublicationsToNexusRepository -x test -PbuildSrc.skipTests=true` through the release CLI's internal authorization.
- [ ] 6.6 Release the global build slot after deploy, including on failure, and record sanitized start/end timestamps, exit status, output, and the manifest transition to `deployed` only when the command completes successfully.
- [ ] 6.7 If deploy is interrupted or uncertain, inventory Nexus before any other action; mark `partial-failure` when assets exist and never repeat the same version deploy.

## 7. Complete remote verification

- [ ] 7.1 Download and inventory all expected remote POM/BOM and main/sources/Javadoc assets for the 19 GAVs, verifying version, packaging, module completeness, and absence of unexpected publications.
- [ ] 7.2 Calculate and record SHA-256 checksums and immutable Nexus URLs for the complete release set.
- [ ] 7.3 Scan every downloaded POM/BOM for internal SNAPSHOT references and verify the approved Spring Framework and Spring Data Commons RELEASE coordinates are retained.
- [ ] 7.4 Acquire the global build slot and run a RELEASE-only consumer with all snapshot repositories disabled; if Maven is used, execute one non-`-T` process, then release the slot.
- [ ] 7.5 Record the remote consumer dependency tree and prove the target Spring Security RELEASE, approved NES Framework/Data dependencies, and no official duplicate Spring implementation are selected.
- [ ] 7.6 Advance to `nexus-verified` only when all 19 publications, checksums, metadata scans, and the remote consumer pass; otherwise preserve immutable evidence and do not tag.

## 8. Annotated tag and CLI-controlled Git push

- [ ] 8.1 Confirm local and remote `v6.5.11-nes.patch.2` are absent and the recorded release commit SHA remains unchanged after `nexus-verified`.
- [ ] 8.2 Let the release CLI internally generate and consume `TAG:spring-security-6.5:v6.5.11-nes.patch.2`, create the annotated tag on the exact release commit, and record the tag object and peeled target without asking the operator again.
- [ ] 8.3 Let the release CLI internally generate and consume `PUSH:spring-security-6.5:v6.5.11-nes.patch.2`, push the approved branch and tag, and restore any temporary remote transport configuration.
- [ ] 8.4 Independently verify the remote branch SHA, annotated tag object, and peeled target commit, then advance to `tagged`.
- [ ] 8.5 If Nexus is verified but Git push fails, retry only the Git operation and never redeploy patch 2.

## 9. Documentation, reconciliation, archive, and lease release

- [ ] 9.1 Update component release notes and evidence with the release commit, tag, complete Nexus inventory, checksums, upstream baselines, POM scans, consumer results, compliance result, commands, and timestamps.
- [ ] 9.2 Reconcile `spring-security-6.5` in the central catalog, 2026-08-14 manifest, and human-readable run report with version, state, branch, release commit, tag, 19 GAVs, Nexus URLs, evidence paths, dependency baselines, and no-exclusions policy.
- [ ] 9.3 Verify component Git, remote Git, Nexus, OpenSpec tasks, component documentation, catalog topology, baseline states, and central manifest all agree; resolve every mismatch before advancing to `documented`.
- [ ] 9.4 Strictly validate `release-6-5-11-nes-patch-2` and all affected main specs without modifying unrelated artifacts.
- [ ] 9.5 Commit and push only the approved post-release component documentation/evidence paths, excluding all local tool directories and unrelated work.
- [ ] 9.6 Archive this OpenSpec only after state is `documented`, synchronize the modified `component-release` main spec, and strictly validate the archive and main specs.
- [ ] 9.7 Record the OpenSpec archive path and final `archived` state in the central manifest, commit/push only the explicit central release-record files, and verify their remote SHAs.
- [ ] 9.8 Release the component owner lease and confirm the global build slot is free; do not create the next development SNAPSHOT in this change.
