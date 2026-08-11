## 1. Development version

- [x] 1.1 Set the live Gradle project version to `5.8.16-nes.patch.2-SNAPSHOT`
- [x] 1.2 Update live README, requirements, GAV, Nexus, and CVE identity documentation while preserving patch.1 archives

## 2. Version-aware candidate verification

- [x] 2.1 Derive the candidate version from `gradle.properties` and validate it before publication checks
- [x] 2.2 Pass the derived version explicitly to the Maven and Gradle Java 8 consumers
- [x] 2.3 Remove stale patch.1 defaults from live consumer fixtures and candidate artifact scans

## 3. Verification and completion

- [x] 3.1 Run shell syntax, diff, version-reference, and strict OpenSpec validation
- [x] 3.2 Run `make verify-published-security` and confirm evidence targets patch.2-SNAPSHOT with Java 8 consumers passing
- [ ] 3.3 Commit the development-version change, rerun the gate from a clean tracked worktree, and archive the completed change
