## 1. Ownership and release preflight

- [ ] 1.1 Confirm this change is apply-ready and exclusively assigned to `spring-security-5.8`.
- [ ] 1.2 Acquire the central manifest owner lease and record owner/session/timestamps.
- [ ] 1.3 Record branch, origin URLs, HEAD, active OpenSpec changes, toolchain, current version, target version, and the 21 approved publication GAVs.
- [ ] 1.4 Inventory tracked and untracked changes; exclude `.claude/`, `.codex/`, `.cursor/`, credentials, and unrelated files from every stage/commit.
- [ ] 1.5 Reconfirm `spring-framework-5.3:5.3.39-nes.patch.1` is Nexus-verified and contains no internal SNAPSHOT metadata.
- [ ] 1.6 Complete and record the engineering legal/open-source review; continue automatically when no concrete blocker exists.

## 2. RELEASE preparation

- [ ] 2.1 Change the component version from `5.8.16-nes.patch.2-SNAPSHOT` to `5.8.16-nes.patch.2`.
- [ ] 2.2 Replace every internal dependency, parent, BOM, plugin, and example reference with its approved RELEASE version.
- [ ] 2.3 Discover the complete publication set and reconcile it exactly with the 21-GAV catalog allowlist.
- [ ] 2.4 Update the Make/release command path so every Gradle invocation explicitly uses `--no-daemon --no-parallel --max-workers=1`.
- [ ] 2.5 Update required component documentation with patch 2 RELEASE coordinates, upstream versions, publication set, exclusions, verification commands, Nexus location, and immutable rollback policy.
- [ ] 2.6 Review the complete diff and confirm it contains no unrelated source changes, credentials, or unapproved artifacts.

## 3. Proportional local verification

- [ ] 3.1 Obtain the coordinator-controlled global build slot; confirm no other Make, Gradle, Maven, consumer, or deploy command is running.
- [ ] 3.2 Audit archived patch 2 development/security evidence, its source commit ancestry, and the intervening diff.
- [ ] 3.3 Record reusable build/test evidence and do not rerun broad `make build` or full tests unless the intervening diff invalidates coverage.
- [ ] 3.4 Run the approved release-version local publication with `--no-daemon --no-parallel --max-workers=1` and no concurrent build process.
- [ ] 3.5 Scan every generated POM/BOM in the 21-GAV publication set for internal `cn.bjca.footstone` SNAPSHOT references; block on any match.
- [ ] 3.6 Run a representative consumer using only locally prepared patch 2 RELEASE publications and approved upstream RELEASEs.
- [ ] 3.7 Record commands, timestamps, exit status, toolchain, artifact set, POM scan, evidence reuse, and consumer result.
- [ ] 3.8 Recheck the worktree/diff and advance the manifest to `locally-verified` only when all required gates pass.

## 4. Dedicated release commit

- [ ] 4.1 Stage only files covered by this release change.
- [ ] 4.2 Inspect the staged diff for version, internal RELEASE dependencies, single-worker command enforcement, documentation, and absence of unrelated files/secrets.
- [ ] 4.3 Create the dedicated `5.8.16-nes.patch.2` release commit.
- [ ] 4.4 Record the release commit SHA and prove the verified build inputs match that commit.
- [ ] 4.5 Push preparation only under coordinator control; do not deploy or tag from an unapproved child session.

## 5. Nexus absence and deployment authorization

- [ ] 5.1 From the release commit, regenerate or verify the exact 21-GAV publication set.
- [ ] 5.2 Immediately before deploy, query Nexus RELEASE for every target POM, binary, classifier, checksum, metadata, and possible partial asset.
- [ ] 5.3 Block if any patch 2 target asset already exists; never delete, overwrite, or redeploy it.
- [ ] 5.4 Verify every publication repository identifier maps to non-empty user-level credentials for the intended RELEASE repository without printing credential values.
- [ ] 5.5 Have the coordinating session review worktree, OpenSpec, upstream, local verification, release commit, credential routing, and Nexus absence evidence.
- [ ] 5.6 Preview and internally authorize the exact single-worker deploy command; no separate ceremonial user token confirmation is required.

## 6. Single deploy and remote verification

- [ ] 6.1 Execute the deploy once from the recorded release commit while holding the global build slot and explicit CLI execute safeguard.
- [ ] 6.2 Record sanitized deploy start/end times, exit status, module results, and uploaded publication set.
- [ ] 6.3 Download and verify the complete remote POM set plus representative main/classifier artifacts from Nexus RELEASE.
- [ ] 6.4 Scan all downloaded POM/BOM metadata for internal SNAPSHOT references and official/NES duplicate first-party coordinates.
- [ ] 6.5 Record immutable asset URLs and checksums.
- [ ] 6.6 Run the RELEASE-only consumer smoke test with internal SNAPSHOT repositories disabled.
- [ ] 6.7 Advance to `nexus-verified` only when remote assets and consumer gates pass; otherwise record `partial-failure` and do not redeploy or tag.

## 7. Git finalization

- [ ] 7.1 Confirm `v5.8.16-nes.patch.2` is absent locally/remotely and the release commit is unchanged.
- [ ] 7.2 Create annotated tag `v5.8.16-nes.patch.2` on the exact release commit after Nexus verification.
- [ ] 7.3 Push the release branch and annotated tag under coordinator control without asking the user to echo TAG/PUSH tokens.
- [ ] 7.4 Verify the remote tag object, peeled release commit, and that the remote branch contains the release commit.
- [ ] 7.5 If Git push fails, retry only Git after reconciliation and never repeat Nexus deployment.

## 8. Documentation, reconciliation, and archive

- [ ] 8.1 Verify permanent component documentation accurately describes the published patch 2 RELEASE and its 21-GAV scope.
- [ ] 8.2 Update the central manifest with release commit, tag object/target, Nexus URLs/checksums, evidence paths, exclusions, timestamps, and final notes.
- [ ] 8.3 Reconcile component Git, remote tag, Nexus, documentation, OpenSpec, and manifest; resolve every substantive mismatch.
- [ ] 8.4 Update the human-readable run report only after remote artifact and tag verification.
- [ ] 8.5 Validate the change strictly, sync the main spec, archive the OpenSpec, and record the archive path.
- [ ] 8.6 Advance the manifest to `archived`, release the owner lease, and preserve unrelated worktree files.
