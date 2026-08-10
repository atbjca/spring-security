## Why

The project currently manages Jackson through BOM version 2.18.8, which is affected by CVE-2026-59889, CVE-2026-54515, and a related JsonView bypass advisory. Jackson 2.18.9 provides the fixes while remaining on the existing 2.18 maintenance line, minimizing compatibility risk for Spring Security 6.5.11 and Spring Framework 6.2.19.

## What Changes

- Upgrade the managed Jackson BOM from 2.18.8 to 2.18.9.
- Record the affected advisories, fixed version, and verification result in the project CVE inventory.
- Verify Jackson-backed Spring Security modules compile and their serialization tests pass with the upgraded BOM.

## Capabilities

### New Capabilities

- `jackson-security-baseline`: Requires the published dependency baseline to use a Jackson release that is not affected by the identified deserialization authorization bypasses.

### Modified Capabilities

None.

## Impact

- Dependency management: `gradle/libs.versions.toml` and the published dependencies platform.
- Documentation: the CVE requirements inventory and a dedicated Jackson advisory assessment.
- Verification: Jackson integration in core, web, OAuth2 client, LDAP, CAS, and SAML modules.
- No public Spring Security API or serialized data format is intentionally changed.
