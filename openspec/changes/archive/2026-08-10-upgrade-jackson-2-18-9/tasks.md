## 1. Dependency Baseline

- [x] 1.1 Upgrade the managed Jackson BOM from 2.18.8 to 2.18.9 without adding per-artifact overrides
- [x] 1.2 Resolve the dependencies platform and confirm Jackson 2.18.9 is selected while 2.18.8 is absent

## 2. Security Documentation

- [x] 2.1 Update the project requirements inventory with the Jackson 2.18.9 security baseline
- [x] 2.2 Add a Jackson advisory assessment covering CVE-2026-59889, CVE-2026-54515, and the related advisory without a CVE identifier

## 3. Compatibility Verification

- [x] 3.1 Run Jackson-focused tests for core and web integrations
- [x] 3.2 Run Jackson-focused tests for OAuth2 client, LDAP, CAS, and SAML integrations

## 4. Final Review

- [x] 4.1 Review the final diff and working tree to confirm the change is limited to the Jackson remediation and its OpenSpec artifacts
