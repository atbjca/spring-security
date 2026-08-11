# published-dependency-security Specification

## Purpose
Define consumer-visible dependency baselines, the Java 8 publication boundary, and candidate verification required before deployment.
## Requirements
### Requirement: Published metadata carries the supported dependency graph

Generated Maven POMs, the Spring Security BOM/dependency platform, and Gradle Module Metadata MUST carry the versions and exclusions required by downstream consumers and MUST NOT rely solely on repository-local resolution strategies.

#### Scenario: OpenSAML 3 consumer metadata is resolved

- **WHEN** a consumer resolves the published SAML module
- **THEN** it MUST resolve Commons Collections 3.2.2, Guava 32.0.1-jre, XMLSec 2.2.6, Woodstox 5.4.0, and Bouncy Castle 1.84 `jdk18on`
- **AND** it MUST NOT resolve Velocity 1.7, Commons Lang 2.x, or a Bouncy Castle `jdk15on` artifact

#### Scenario: OpenID consumer metadata is resolved

- **WHEN** a consumer resolves the published OpenID module
- **THEN** it MUST resolve Xerces 2.12.2

### Requirement: Java 8 compatibility is enforced on publications

The repository MAY use JDK 11 or newer to build and deploy, but every Spring Security main publication declared Java 8-compatible MUST contain only production classes with class-file major version 52 or lower.

#### Scenario: The SAML main artifact is generated

- **WHEN** the Java 8 SAML publication is assembled
- **THEN** its main JAR, sources JAR, and Javadoc MUST contain the OpenSAML 3 implementation
- **AND** the main JAR MUST NOT contain OpenSAML 4 implementation classes or a class-file major version above 52

#### Scenario: A Java 8 module is published

- **WHEN** a Maven or Artifactory publication task runs for a Spring Security module
- **THEN** the main JAR class-version check MUST run before upload
- **AND** publication MUST fail if a production class exceeds major version 52

### Requirement: Real Java 8 consumers gate deploy

`make deploy` MUST verify candidate publications using independent Maven and Gradle consumers on a real Java 8 JVM before Nexus publication begins.

#### Scenario: Candidate consumers pass

- **WHEN** the candidate modules are published to the isolated local repository
- **THEN** Maven and Gradle consumers MUST resolve the required security versions without project-local force rules
- **AND** both consumers MUST execute representative OpenSAML 3 initialization, BC encryption, LDAP/OpenID/Xerces class loading, and OpenSAML 4 absence checks on Java 8

#### Scenario: A candidate violates the release boundary

- **WHEN** either consumer resolves a prohibited/below-baseline dependency, a JAR contains an unsupported class, or a Java 8 smoke test fails
- **THEN** `make deploy` MUST stop before publishing to Nexus

### Requirement: Spring LDAP applicability is documented at the Spring Security boundary

The publication MUST record that Spring LDAP 2.4.5 is unavailable from the approved repositories and that Spring Security rejects null or empty passwords before invoking an LDAP bind through its supported authentication entry points.

#### Scenario: Empty credentials are submitted

- **WHEN** authentication reaches `BindAuthenticator` or an `AbstractLdapAuthenticationProvider` implementation with an empty password
- **THEN** Spring Security MUST raise `BadCredentialsException` before invoking Spring LDAP bind behavior

#### Scenario: The CVE disposition is reviewed

- **WHEN** CVE-2026-41720 evidence is inspected
- **THEN** it MUST distinguish Spring Security path non-reachability from an upstream Spring LDAP patch claim
- **AND** it MUST NOT claim that Spring LDAP 2.4.4 itself is fixed

### Requirement: Public dependency management excludes test-only Logback

The published Spring Security BOM and dependency platform MUST NOT export a Logback version constraint that exists only for repository tests.

#### Scenario: Public dependency management is inspected

- **WHEN** the generated platform POM, BOM, and module metadata are inspected
- **THEN** a test-only Logback constraint MUST be absent

### Requirement: Minimal publication evidence is retained

Candidate verification MUST produce the Maven dependency tree, Gradle resolved runtime graph, Java 8 runtime identity, smoke-test result summary, security disposition, and SHA-256 values for primary candidate JAR/POM/module files.

#### Scenario: A candidate release is audited

- **WHEN** `make verify-published-security` succeeds
- **THEN** the evidence MUST be available under `build/reports/published-security`
- **AND** it MUST identify the exact Java 8 runtime and candidate bytes that were accepted
