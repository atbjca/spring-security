## ADDED Requirements

### Requirement: Immutable releases advance to a new NES patch development version
After an NES RELEASE has been published or tagged, subsequent dependency, code, or publication metadata changes MUST use a higher NES patch version and MUST NOT reuse the immutable RELEASE coordinates. The project artifact version and upstream Spring Security identity version MUST remain independently managed.

#### Scenario: Maintenance continues after patch 1 release
- **WHEN** `6.5.11-nes.patch.1` is already published or tagged and new maintenance changes are integrated
- **THEN** the project development version is `6.5.11-nes.patch.2-SNAPSHOT` or a later explicitly approved NES patch SNAPSHOT
- **THEN** no artifact is published again as `6.5.11-nes.patch.1`

#### Scenario: Upstream code baseline is unchanged
- **WHEN** only NES maintenance changes are added without rebasing to a different upstream Spring Security release
- **THEN** `springSecurityVersion` remains `6.5.11`
- **THEN** the generated artifact coordinates use the incremented NES patch version

#### Scenario: Development and release documentation are reviewed
- **WHEN** maintainers inspect version documentation after the patch increment
- **THEN** current-development references identify patch 2 SNAPSHOT
- **THEN** historical patch 1 release evidence remains clearly attributable to the already published release
