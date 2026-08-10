## 1. Baseline and traceability

- [x] 1.1 Freeze the worktree and record the 5.8 branch, configured build JDK/toolchains, Java 8 target baseline, OpenSAML 3.4.6 source set, current version, and affected modules
- [x] 1.2 Create the seven-CVE evidence table with official advisory ranges, upstream commits, 5.8 compatibility notes, expected fix behavior, and test owners
- [x] 1.3 Record CVE-2026-22748 as not applicable only after preserving official evidence that its affected range starts at 6.3

## 2. Core and web backports

- [x] 2.1 Backport Content-Length tracking for addHeader, addIntHeader, setHeader, and setIntHeader in OnCommittedResponseWrapper
- [x] 2.2 Add deterministic response-wrapper tests for all four header APIs and non-Content-Length delegation
- [x] 2.3 Backport DaoAuthenticationProvider's always-perform-additional-check behavior with a Java 8 compatible API and safe default
- [x] 2.4 Add account-state tests proving the additional checker runs and the original DisabledException/LockedException/AccountExpiredException is preserved
- [x] 2.5 Backport relative-only Servlet and Reactive CookieRequestCache restoration and reject absolute, scheme-relative, and backslash escape targets
- [x] 2.6 Add request-cache tests for valid relative restoration, malicious cookie values, cookie clearing, and both servlet/reactive implementations

## 3. SAML backports

- [x] 3.1 Inventory every OpenSAML 3 REDIRECT inflate helper and apply a shared 1 MiB capped output stream before XML parsing
- [x] 3.2 Add compressed-payload boundary and over-cap regression tests for login, logout, and all copied Saml2Utils entry points
- [x] 3.3 Backport Java 8 compatible HTML form generation/encoding for SAML POST action, names, and values
- [x] 3.4 Add form-injection tests covering quotes, angle brackets, ampersands, and valid decoded form semantics
- [x] 3.5 Reorder SAML login and logout validation so applicable signatures are verified before decryption and semantic validation
- [x] 3.6 Add OpenSAML 3 tests proving invalid signatures prevent decryptor invocation and do not expose a decryption oracle
- [x] 3.7 Run valid signed/unsigned-where-allowed SAML login, logout, POST, and REDIRECT compatibility suites

## 4. X.509 backport

- [x] 4.1 Add the structured SubjectX500PrincipalExtractor using X500Principal/LdapName while preserving explicit legacy extractor configuration
- [x] 4.2 Switch the default servlet X.509 filter/configuration path to the structured extractor without removing public compatibility types
- [x] 4.3 Add certificate fixtures for multiple CNs, embedded DN text, malformed names, and explicit emailAddress extraction
- [x] 4.4 Verify Java/XML configuration and normal X.509 authentication flows

## 5. Compatibility, evidence, and release gate

- [x] 5.1 Run the configured JDK build with Java 8-targeted production classes, then execute affected core/web/config/SAML OpenSAML 3.4.6 consumer smoke tests on a real Java 8 JVM
- [x] 5.2 Run binary/API compatibility checks and inspect deprecation/default-behavior documentation
- [x] 5.3 Map each local fix commit and regression result back to its CVE evidence row
- [x] 5.4 Run the complete security regression suite and block release on any unresolved CVE or compatibility failure
