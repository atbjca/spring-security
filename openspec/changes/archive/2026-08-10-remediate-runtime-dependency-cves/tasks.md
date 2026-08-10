## 1. Spring Data Security Baseline

- [x] 1.1 Add the Spring Data 2025.0.13 release train and NES Spring Data Commons 3.5.13 dependency coordinates
- [x] 1.2 Replace the published Spring Security Data dependency with the NES Spring Data Commons artifact
- [x] 1.3 Remove affected official Spring Data Commons and Spring Framework artifacts from Spring Data-related test classpaths
- [x] 1.4 Resolve Spring Data and configuration dependency graphs to verify the approved NES artifacts and absence of affected official versions

## 2. LDAP and Crypto Security Baselines

- [x] 2.1 Upgrade Spring LDAP from 3.2.16 to 3.3.8 while retaining official Spring dependency exclusions
- [x] 2.2 Upgrade the aligned Bouncy Castle version family from 1.80.2 to 1.84
- [x] 2.3 Resolve LDAP, crypto, and OAuth2 JOSE dependency graphs to verify fixed versions and alignment

## 3. Security Documentation

- [x] 3.1 Update the project requirements CVE inventory for the eight runtime dependency CVEs
- [x] 3.2 Add Spring Data, Spring LDAP, Spring Framework, and Bouncy Castle advisory assessments with applicability and verification evidence

## 4. Compatibility Verification

- [x] 4.1 Run Spring Security Data and Spring Data-related configuration tests
- [x] 4.2 Run LDAP unit/integration tests including empty-password rejection coverage
- [x] 4.3 Run crypto and OAuth2 JOSE tests with Bouncy Castle 1.84

## 5. Final Review

- [x] 5.1 Review dependency reports, documentation, final diff, and working tree for scope compliance
