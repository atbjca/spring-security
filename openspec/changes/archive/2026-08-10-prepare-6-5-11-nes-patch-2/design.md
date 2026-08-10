## Context

`v6.5.11-nes.patch.1` points to the release commit whose artifacts were verified in Nexus. The branch now contains completed dependency security work that changes Spring Data Commons, Spring LDAP, Bouncy Castle, and Jackson metadata. Maven RELEASE coordinates are immutable, so these changes cannot be published under patch 1.

The project maintains two related versions: `version` controls artifact coordinates, while `springSecurityVersion` is injected into `SpringSecurityCoreVersion` to identify the unchanged upstream Spring Security baseline.

## Goals / Non-Goals

**Goals:**

- Establish `6.5.11-nes.patch.2-SNAPSHOT` as the next development artifact version.
- Preserve `springSecurityVersion=6.5.11`.
- Keep documentation clear about the released patch 1 baseline versus the patch 2 development target.
- Verify representative project and publication metadata before archiving the preparation change.

**Non-Goals:**

- Publish SNAPSHOT or RELEASE artifacts to Nexus.
- Promote the version to `6.5.11-nes.patch.2` RELEASE.
- Create or push `v6.5.11-nes.patch.2`.
- Change dependency versions, Java APIs, runtime behavior, or the upstream Spring Security identity.

## Decisions

1. Increment only the NES maintenance suffix.
   - Set `version=6.5.11-nes.patch.2-SNAPSHOT`.
   - Keep the upstream major, minor, and patch baseline at 6.5.11 because this fork has not rebased to another upstream Spring Security release.
   - Alternative rejected: use `6.5.12`. That would incorrectly claim an upstream code baseline that this branch does not contain.

2. Use a SNAPSHOT during integration.
   - The current dependency and documentation changes remain under review and have not passed the complete immutable RELEASE workflow.
   - Alternative rejected: set patch 2 RELEASE immediately. RELEASE metadata must only be created by a dedicated publication change after local and Nexus gates are satisfied.

3. Preserve historical release documentation.
   - Documents that describe the already published patch 1 release remain historical evidence.
   - Documents that describe the current branch or next development target move to patch 2 SNAPSHOT.
   - This avoids rewriting release evidence while preventing current-build documentation from advertising patch 1.

4. Treat patch 2 publication as a separate future change.
   - That change will remove `-SNAPSHOT`, regenerate and inspect all publication metadata, verify target absence in Nexus, deploy once, validate remote assets, and create the annotated tag.

## Risks / Trade-offs

- [Risk] Documentation may mix the released version and development target → Mitigation: update only current-development references and retain explicitly historical patch 1 evidence.
- [Risk] Generated metadata may still use patch 1 because of a secondary version source → Mitigation: inspect Gradle properties and representative publication/version output.
- [Risk] Existing completed OpenSpec work overlaps the same working tree → Mitigation: preserve unrelated Jackson artifacts and restrict this change to version succession and its documentation.
- [Trade-off] Consumers cannot use patch 2 until a later release change → Accepted; publishing is intentionally separated from development preparation.

## Migration Plan

1. Update the project artifact version and current-development documentation.
2. Resolve the Gradle project version and inspect representative publication metadata.
3. Confirm `springSecurityVersion` remains 6.5.11 and no release/tag operation occurred.
4. Archive this preparation change; create a separate release change when patch 2 is ready for Nexus.

Rollback consists of restoring the version and current-development documentation to their previous values; no data or API migration is involved.

## Open Questions

None.
