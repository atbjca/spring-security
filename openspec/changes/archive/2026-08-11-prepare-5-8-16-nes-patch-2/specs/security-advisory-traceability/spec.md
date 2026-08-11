## ADDED Requirements

### Requirement: Candidate and release identities remain distinct

Live security evidence MUST identify `5.8.16-nes.patch.2-SNAPSHOT` as the current development candidate and MUST retain `5.8.16-nes.patch.1` and `v5.8.16-nes.patch.1` as the previous immutable release identity.

#### Scenario: Current candidate evidence is reviewed

- **WHEN** a reviewer inspects live CVE, GAV, README, Nexus, or generated candidate evidence
- **THEN** the current candidate coordinate MUST be `cn.bjca.footstone.bpring.security:*:5.8.16-nes.patch.2-SNAPSHOT`
- **AND** the evidence MUST NOT imply that the snapshot overwrites or replaces patch.1

#### Scenario: Historical release evidence is reviewed

- **WHEN** a reviewer inspects patch.1 archive evidence or the existing release tag
- **THEN** it MUST remain bound to `5.8.16-nes.patch.1` and `v5.8.16-nes.patch.1`

