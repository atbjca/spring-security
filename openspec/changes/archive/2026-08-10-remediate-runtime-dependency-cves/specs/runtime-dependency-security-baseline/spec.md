## ADDED Requirements

### Requirement: Spring Data runtime uses the approved NES security baseline
The project MUST publish and test Spring Security Data against `cn.bjca.footstone.bpring.data:bjca-footstone-bpring-data-commons:3.5.13-nes.patch.1` or a later explicitly approved NES release that fixes CVE-2026-41721, CVE-2026-41716, CVE-2026-41711, and CVE-2026-41695.

#### Scenario: Spring Data dependency graph is resolved
- **WHEN** the Spring Security Data compile and runtime dependency graphs are inspected
- **THEN** the approved NES Spring Data Commons artifact is selected
- **THEN** `org.springframework.data:spring-data-commons:3.4.13` is absent
- **THEN** `org.springframework:spring-core:6.2.15` is absent
- **THEN** the NES Spring Framework 6.2.19 baseline supplies Spring Core

### Requirement: Spring Data auxiliary tests do not reintroduce vulnerable official artifacts
Spring Data JPA or other auxiliary test dependencies MUST NOT place an affected official Spring Data Commons or official Spring Framework implementation alongside the NES artifacts.

#### Scenario: Configuration test classpath is resolved
- **WHEN** the Spring Security configuration test runtime classpath is inspected
- **THEN** no affected official Spring Data Commons version is selected
- **THEN** no official Spring Core implementation duplicates the NES Spring Core implementation

### Requirement: Spring LDAP uses a fixed empty-password baseline
The project MUST manage Spring LDAP at version 3.3.8 or a later explicitly approved fixed release and MUST preserve Spring Security's rejection of empty or null authentication credentials.

#### Scenario: LDAP dependencies and authentication tests are evaluated
- **WHEN** the LDAP compile/runtime graph and empty-password tests are run
- **THEN** Spring LDAP 3.2.16 is absent
- **THEN** empty and null credentials cannot result in an authenticated bind

### Requirement: Bouncy Castle artifacts use the fixed aligned family
The project MUST manage Bouncy Castle `bcpkix`, `bcprov`, and transitive `bcutil` artifacts at version 1.84 or a later explicitly approved aligned version.

#### Scenario: Crypto dependency graph is resolved
- **WHEN** crypto and OAuth2 JOSE dependency graphs are inspected
- **THEN** Bouncy Castle 1.80.2 is absent
- **THEN** all selected Bouncy Castle artifacts use the same approved version

### Requirement: Runtime dependency upgrades preserve supported integrations
The project SHALL retain compatible Spring Data, LDAP, crypto, and OAuth2 JOSE behavior after the security baseline changes.

#### Scenario: Focused regression suite executes
- **WHEN** the affected module and integration tests execute with the new dependency baseline
- **THEN** they complete without new failures attributable to the dependency upgrades
