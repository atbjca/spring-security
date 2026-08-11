## ADDED Requirements

### Requirement: Released coordinates advance to the next development snapshot

After an immutable NES patch release is Nexus-verified and tagged, the maintained branch MUST advance to the next NES patch `-SNAPSHOT` before accepting additional implementation commits or generating new candidate publications.

#### Scenario: Patch 1 is already released

- **WHEN** `5.8.16-nes.patch.1` exists as a Nexus-verified release with tag `v5.8.16-nes.patch.1`
- **THEN** subsequent branch development MUST use `5.8.16-nes.patch.2-SNAPSHOT`
- **AND** no subsequent candidate or deployment MAY reuse `5.8.16-nes.patch.1`

#### Scenario: Patch 2 is prepared for formal release

- **WHEN** the coordinator begins the explicit patch.2 release workflow
- **THEN** a dedicated release change MUST remove `-SNAPSHOT`, verify the complete `5.8.16-nes.patch.2` target set is absent from Nexus RELEASE, and bind deployment and tagging to one release commit

