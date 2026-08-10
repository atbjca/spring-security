## Context

Spring Security 6.5.11 currently publishes or manages Spring Data Commons 3.4.13, Spring LDAP 3.2.16, and Bouncy Castle 1.80.2. Spring Data Commons also introduces official `org.springframework:spring-core:6.2.15` alongside the NES Spring Framework fork, producing both a CVE match and a duplicate implementation of the same Java packages.

The organization already publishes a compatible Spring Data Commons 3.5.13 NES fork at `cn.bjca.footstone.bpring.data:bjca-footstone-bpring-data-commons:3.5.13-nes.patch.1`. It includes the Spring Data security fixes and depends on the existing Spring Framework 6.2.19 NES fork. Spring LDAP 3.3.8 and Bouncy Castle 1.84 are the smallest upstream fixed baselines for the remaining dependency advisories.

## Goals / Non-Goals

**Goals:**

- Remove the eight identified runtime and optional-runtime CVE matches from the managed dependency baseline.
- Use the released internal Spring Data Commons fork and eliminate the official Spring Data Commons 3.4.13 and Spring Core 6.2.15 paths.
- Preserve Spring Security's explicit rejection of empty LDAP passwords.
- Keep Bouncy Castle artifacts aligned at 1.84.
- Produce dependency-resolution, regression-test, and documentation evidence suitable for audit.

**Non-Goals:**

- Upgrade or refactor the separately identified build/test-only OkHttp, Jetty, Jython, SnakeYAML, HtmlUnit, or Commons IO dependencies.
- Modify Spring Security public APIs, authentication semantics, Java packages, or serialized formats.
- Publish a new Spring Data Commons, Spring LDAP, or Bouncy Castle artifact from this repository.
- Change the internal Spring Framework 6.2.19 NES baseline.

## Decisions

1. Replace Spring Data Commons by GAV, not by an official-version override.
   - Use `cn.bjca.footstone.bpring.data:bjca-footstone-bpring-data-commons:3.5.13-nes.patch.1` directly in the data module and dependency platform.
   - The fork preserves `org.springframework.data.*` packages and the `spring.data.commons` module name while depending on NES Spring Framework artifacts.
   - Alternative rejected: retain official 3.4.13 and backport four fixes locally. A maintained 3.5.13 fork already exists and has full regression evidence.

2. Align the Spring Data release train used by tests with Spring Data Commons 3.5.13.
   - Move the official Spring Data BOM used for auxiliary/test modules from 2024.1.13 to 2025.0.13.
   - Exclude official Spring Data Commons and official Spring Framework artifacts where Spring Data JPA is used for tests, then supply the matching NES artifacts.
   - This prevents duplicate classes from official and NES GAVs on the same classpath.

3. Upgrade Spring LDAP to 3.3.8.
   - This is the first fixed 3.3.x release and remains on the Spring Framework 6.2 generation.
   - Existing exclusions for official Spring Framework and Spring Data dependencies remain in place.
   - Alternative rejected: document only the `BindAuthenticator` mitigation. Spring LDAP is an exported API dependency and downstream direct use would remain exposed.

4. Upgrade Bouncy Castle as one aligned version family.
   - Set the catalog version to 1.84 so `bcpkix-jdk18on`, `bcprov-jdk18on`, and `bcutil-jdk18on` resolve together.
   - Alternative rejected: remove the optional dependency. Bouncy Castle-backed crypto behavior remains a supported integration.

5. Treat dependency resolution as a release gate.
   - Verify that affected versions and official duplicate GAVs are absent from the relevant compile, optional, and test classpaths.
   - Focus tests on data, config data integration, LDAP, crypto, and OAuth2 JOSE.

## Risks / Trade-offs

- [Risk] Spring Data release-train movement can expose binary incompatibilities in Spring Data JPA tests → Mitigation: align JPA and Commons at 3.5.13 and run the affected configuration tests.
- [Risk] Internal and official Spring artifacts can coexist because their Maven groups differ → Mitigation: inspect resolved classpaths and add explicit exclusions where external Spring Data modules are test dependencies.
- [Risk] Spring LDAP 3.3.8 may change transitive dependency metadata → Mitigation: retain exclusions and run LDAP unit, integration, and configuration tests.
- [Risk] Bouncy Castle 1.84 may alter provider behavior or certificate parsing → Mitigation: run crypto and OAuth2 JOSE tests, including X.509/signature fixtures.
- [Trade-off] Build/test-only CVEs remain visible in broad SCA scans → Accepted for this change; they require separate migrations with different compatibility risk.

## Migration Plan

1. Update version catalog and dependency declarations.
2. Resolve classpaths and remove official duplicate Spring Data/Spring Framework paths.
3. Update CVE inventory and add component-specific assessments.
4. Run focused regression tests and final dependency checks.
5. If verification fails, revert the dependency declarations as a unit; no data or API migration is required.

## Open Questions

None.
