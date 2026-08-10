## Context

Spring Security 6.5.11 currently imports Jackson BOM 2.18.8 through the shared dependencies platform. Jackson is optional in several published modules, but the platform controls the version selected by project tests and by consumers that import the platform. Version 2.18.8 is affected by authorization-bypass advisories in specialized deserialization configurations; version 2.18.9 fixes those issues.

The 6.5.x maintenance line intentionally remains on Jackson 2.18.x. Moving to 2.21.x would cross multiple minor releases and expand the regression surface without being necessary to address the identified vulnerabilities.

## Goals / Non-Goals

**Goals:**

- Remove the known Jackson 2.18.8 advisory matches from the managed dependency baseline.
- Preserve alignment across all Jackson artifacts by upgrading the BOM as a unit.
- Verify Spring Security Jackson modules and mixins retain their serialization and deserialization behavior.
- Document the vulnerability assessment and evidence used for the release decision.

**Non-Goals:**

- Upgrade to Jackson 2.19, 2.20, 2.21, or Jackson 3.
- Change Spring Security public APIs or serialized object schemas.
- Modify application-specific JsonView or JsonUnwrapped usage outside this repository.
- Address non-Jackson dependency advisories in the same change.

## Decisions

1. Upgrade `com.fasterxml.jackson:jackson-bom` from 2.18.8 to 2.18.9.
   - This is the smallest version change containing the fixes.
   - Alternative considered: 2.21.5. It is not currently affected by the identified advisories, but it crosses three minor release lines and is unnecessary for this maintenance patch.

2. Upgrade through the BOM only.
   - All Jackson core, datatype, dataformat, and module artifacts must remain version-aligned.
   - A direct `jackson-databind` override is rejected because it can create an unsupported mixed Jackson classpath.

3. Verify focused Jackson behavior before broader module checks.
   - Run Jackson and mixin tests in core, web, OAuth2 client, LDAP, CAS, and SAML modules.
   - Resolve dependency metadata to confirm 2.18.9 is selected and 2.18.8 is absent.

4. Update the CVE inventory in the same change.
   - Record CVE-2026-59889 and CVE-2026-54515 as fixed by the 2.18.9 baseline.
   - Record the related GHSA without assigning it a CVE identifier that has not been issued.

## Risks / Trade-offs

- [Risk] Patch release behavior changes could affect custom serializers or mixins → Mitigation: run all repository Jackson-focused tests across published integration modules.
- [Risk] Dependency management precedence in downstream applications may override the project BOM → Mitigation: document the resolved project baseline and advise downstream products to verify their effective dependency tree.
- [Risk] Updating only documentation could diverge from the resolved dependency graph → Mitigation: make dependency-resolution verification a required task.
- [Trade-off] Remaining on 2.18.x does not adopt later Jackson improvements → Accepted to minimize maintenance-line compatibility risk; later minor upgrades require a separate change.

## Migration Plan

1. Change the Jackson BOM catalog entry to 2.18.9.
2. Update CVE inventory and add a dedicated assessment document.
3. Run dependency resolution and Jackson-focused tests.
4. If verification fails, revert the single BOM version change and investigate the failing module before release.

## Open Questions

None.
