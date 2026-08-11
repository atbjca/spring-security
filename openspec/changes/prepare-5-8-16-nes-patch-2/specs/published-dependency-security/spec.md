## ADDED Requirements

### Requirement: Candidate verification follows the declared project version

The publication verifier MUST derive the candidate version from the repository's declared Gradle project version and MUST use that exact value for local artifact lookup, Maven consumer resolution, Gradle consumer resolution, and candidate hash evidence.

#### Scenario: Snapshot candidate is verified

- **WHEN** `gradle.properties` declares `5.8.16-nes.patch.2-SNAPSHOT`
- **THEN** the isolated repository, Maven consumer, Gradle consumer, artifact existence checks, and SHA-256 scan MUST all target `5.8.16-nes.patch.2-SNAPSHOT`

#### Scenario: A stale hard-coded version remains

- **WHEN** any verifier or consumer resolves a version different from the declared Gradle project version
- **THEN** candidate verification MUST fail before Nexus publication begins

