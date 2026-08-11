# security-advisory-traceability Specification

## Purpose

定义内部 Spring Security 版本到上游基线、CVE 回移提交/测试和候选发布证据的最小可追溯关系。

## ADDED Requirements

### Requirement: The internal release identifies its upstream source baseline

Release documentation MUST map the internal group and version to the upstream Spring Security source version/branch and MUST distinguish security-behavior backports from a complete upstream version upgrade.

#### Scenario: An internal coordinate is reviewed

- **WHEN** a reviewer inspects `cn.bjca.footstone.bpring.security:*:5.8.16-nes.patch.1`
- **THEN** the documentation MUST identify Spring Security 5.8.16/`5.8.x` as its source baseline
- **AND** it MUST state that the selected 5.8.24-5.8.27 security behaviors do not imply full functional equivalence with 5.8.27

### Requirement: Applicable CVEs map to commits and regression evidence

Each of the seven applicable 2026 Spring Security CVEs MUST identify its official advisory/range, upstream reference commit, local backport commit, affected module, and regression-test result.

#### Scenario: A backported CVE is audited

- **WHEN** a reviewer follows an in-scope CVE entry
- **THEN** the referenced local commit MUST exist in the repository
- **AND** the entry MUST identify the behavior and test evidence used to accept the fix

#### Scenario: CVE-2026-22748 is reviewed

- **WHEN** the 5.8 release is compared with the official affected range
- **THEN** the documentation MUST retain the 6.3+ version-boundary evidence for its not-applicable disposition

### Requirement: Candidate evidence records its source state

Candidate publication verification MUST record the current Git commit, whether tracked files differ from that commit, and an index of the generated dependency, Java 8, smoke, disposition, and artifact-hash evidence.

#### Scenario: Publication verification succeeds

- **WHEN** `make verify-published-security` completes
- **THEN** `build/reports/published-security/source-state.txt` MUST identify the tested Git commit and tracked worktree state
- **AND** `evidence-index.txt` MUST list evidence files that exist and are non-empty

### Requirement: Traceability remains proportional to this release

The current traceability change MUST NOT require SBOM/VEX generation, a scheduled advisory feed, Spring Framework attestation, or post-deploy Nexus reconciliation.

#### Scenario: Current evidence is complete

- **WHEN** the identity/CVE documentation and local candidate evidence are present
- **THEN** this change MAY complete without introducing broader governance systems
