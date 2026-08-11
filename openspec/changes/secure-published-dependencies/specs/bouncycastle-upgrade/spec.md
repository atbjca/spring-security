# bouncycastle-upgrade Delta Specification

## MODIFIED Requirements

### Requirement: Bouncy Castle Version Must Be Upgraded Past the CVE-2025-8916 Fix Level

The build configuration and published dependency metadata MUST resolve org.bouncycastle:bcpkix-jdk18on and org.bouncycastle:bcprov-jdk18on to version 1.84 or newer approved compatible patch, and MUST NOT resolve any jdk15on variant.

#### Scenario: Version catalog declares the security baseline

- **WHEN** the project version catalog defines the shared Bouncy Castle version
- **THEN** that version is 1.84 or newer
- **AND** both bcpkix-jdk18on and bcprov-jdk18on reference the same shared version source
- **AND** no published dependency uses a jdk15on artifactId

### Requirement: Dependency Platform Must Export the Upgraded Bouncy Castle Coordinates

The dependency platform, generated Maven metadata, and Gradle Module Metadata MUST export Bouncy Castle jdk18on coordinates using the shared 1.84 or newer approved version so downstream consumers resolve the fixed artifacts consistently.

#### Scenario: Published dependency constraints are evaluated

- **WHEN** the generated platform POM, BOM, and module metadata are inspected
- **THEN** org.bouncycastle:bcpkix-jdk18on is present as a managed dependency
- **AND** org.bouncycastle:bcprov-jdk18on is present as a managed dependency
- **AND** both resolve to the shared 1.84 or newer approved version
- **AND** jdk15on coordinates are excluded or absent

#### Scenario: Independent consumers resolve Bouncy Castle

- **WHEN** clean Maven and Gradle consumers use Crypto or SAML publications
- **THEN** both consumers resolve only the approved jdk18on version and no older or mixed Bouncy Castle line

### Requirement: Upgrade Must Preserve Existing Crypto and Certificate Workflows

The repository and representative consumers MUST pass regression validation for modules that depend on Bouncy Castle primitives or certificate-related processing after the 1.84 upgrade.

#### Scenario: Regression validation is executed

- **WHEN** the change is implemented
- **THEN** the relevant crypto tests pass
- **AND** the relevant saml2 certificate, encryption, and signature tests pass
- **AND** the relevant oauth2-jose signing or certificate tests pass
- **AND** representative Maven and Gradle consumer smoke tests pass

### Requirement: CVE Disposition Documentation Must Reflect the Upgrade Outcome

Project security evidence MUST state the resolved Bouncy Castle version, consumer-visible coordinates, CVE-2025-8916 status, and the absence of older jdk15on or mixed-version artifacts.

#### Scenario: Security documentation is reviewed after implementation

- **WHEN** the upgrade is completed
- **THEN** the CVE documentation states the Bouncy Castle 1.84 or newer approved version
- **AND** it records that the dependency version is no longer within the affected range
- **AND** it includes Maven and Gradle consumer dependency evidence
- **AND** it preserves relevant notes about actual repository usage versus theoretical exploitability
