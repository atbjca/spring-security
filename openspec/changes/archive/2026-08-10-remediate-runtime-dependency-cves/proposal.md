## Why

The current dependency baseline exposes published Spring Security modules and consumers to eight runtime or optional-runtime CVEs through Spring Data Commons 3.4.13, its transitive official Spring Core 6.2.15, Spring LDAP 3.2.16, and Bouncy Castle 1.80.2. Maintained or upstream-fixed replacements are available and should be adopted before the next release without mixing in lower-priority build/test dependency migrations.

## What Changes

- Replace official Spring Data Commons 3.4.13 with the released NES fork `cn.bjca.footstone.bpring.data:bjca-footstone-bpring-data-commons:3.5.13-nes.patch.1`.
- Prevent official `org.springframework.data:spring-data-commons` and `org.springframework:spring-core:6.2.15` from remaining on affected published or test dependency paths.
- Upgrade Spring LDAP from 3.2.16 to a fixed 3.3.x release and retain Spring Security's explicit empty-password rejection behavior.
- Upgrade Bouncy Castle from 1.80.2 to 1.84 through the shared version catalog.
- Record applicability, mitigation, fixed versions, and dependency-resolution evidence for the eight addressed CVEs.
- Verify Spring Data, LDAP, crypto, OAuth2 JOSE, and affected configuration integrations.
- Exclude the previously identified build/test-only dependency CVEs from this change; they require a separate compatibility-focused change.

## Capabilities

### New Capabilities

- `runtime-dependency-security-baseline`: Requires published and optional-runtime dependency paths to use the approved fixed Spring Data Commons, Spring LDAP, Spring Framework, and Bouncy Castle baselines.

### Modified Capabilities

- `cve-documentation-alignment`: Extend the auditable CVE inventory to cover the runtime dependency vulnerabilities and their verification evidence.

## Impact

- Dependency management: `gradle/libs.versions.toml`, the shared dependencies platform, Spring Data, LDAP, crypto, OAuth2 JOSE, and configuration test dependency graphs.
- Published metadata: Spring Data Commons changes from the official GAV to the NES fork GAV; Spring LDAP and Bouncy Castle versions change.
- Documentation: `doc/REQUIREMENTS.md` and dedicated CVE assessments.
- Verification: focused dependency insight plus Spring Data, LDAP, crypto, OAuth2 JOSE, and configuration tests.
- No intentional Spring Security public API, Java package, authentication flow, or serialized data format change.
