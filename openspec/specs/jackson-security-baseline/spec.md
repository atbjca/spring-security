# jackson-security-baseline Specification

## Purpose
TBD - created by archiving change upgrade-jackson-2-18-9. Update Purpose after archive.
## Requirements
### Requirement: Managed Jackson baseline excludes identified authorization bypasses
The project SHALL manage Jackson artifacts through a single BOM version that is not affected by CVE-2026-59889, CVE-2026-54515, or the related JsonView external-property advisory identified during this change.

#### Scenario: Dependency baseline is resolved
- **WHEN** the project dependencies platform resolves Jackson artifacts
- **THEN** the selected Jackson BOM and Jackson Databind version are 2.18.9 or a later explicitly approved version
- **THEN** Jackson 2.18.8 is not selected

### Requirement: Jackson artifacts remain version aligned
The project MUST upgrade Jackson through `com.fasterxml.jackson:jackson-bom` and MUST NOT apply an isolated Jackson Databind override as the remediation.

#### Scenario: Published dependency management is inspected
- **WHEN** the dependencies platform metadata is generated or resolved
- **THEN** Jackson core, annotations, Databind, datatypes, and dataformats use the BOM-aligned release line

### Requirement: Spring Security Jackson integrations remain compatible
The project SHALL preserve serialization and deserialization behavior for the Jackson integrations supplied by Spring Security modules.

#### Scenario: Jackson integration tests run
- **WHEN** the Jackson-focused tests for core, web, OAuth2 client, LDAP, CAS, and SAML modules execute with Jackson 2.18.9
- **THEN** the tests complete successfully without serialization format or mixin registration regressions

### Requirement: CVE status is auditable
The project SHALL record the affected dependency version, fixed version, applicability, and verification evidence for the Jackson advisories addressed by this change.

#### Scenario: Security inventory is reviewed
- **WHEN** a maintainer reviews the project CVE inventory
- **THEN** CVE-2026-59889 and CVE-2026-54515 are marked fixed by the Jackson 2.18.9 baseline
- **THEN** the related advisory without a CVE identifier is documented separately and not misrepresented as a CVE
