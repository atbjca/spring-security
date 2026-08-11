## Verification Evidence

Validated on 2026-08-11.

### Release gate

`make verify-published-security` completed successfully. The repository build and candidate publication ran on the configured JDK 11 toolchain; Maven and Gradle consumers ran on:

```text
openjdk version "1.8.0_472"
OpenJDK Runtime Environment Corretto-8.472.08.1
```

Both consumers completed `PublishedSecuritySmoke`, which initializes OpenSAML 3, asserts that an OpenSAML 4 implementation class is absent, completes a Bouncy Castle AES-GCM round trip, and loads the supported LDAP, OpenID, and Xerces paths.

Critical resolved versions were identical for the security baselines in both consumers:

```text
commons-collections:commons-collections:3.2.2
com.google.guava:guava:32.0.1-jre
org.apache.santuario:xmlsec:2.2.6
com.fasterxml.woodstox:woodstox-core:5.4.0
org.bouncycastle:bcpkix-jdk18on:1.84
org.bouncycastle:bcprov-jdk18on:1.84
xerces:xercesImpl:2.12.2
```

Neither graph contains Bouncy Castle `jdk15on`, Velocity 1.7, or Commons Lang 2.x. Generated public dependency-management metadata contains no Logback constraint.

The gate writes the exact Maven tree, Gradle runtime graph, Java identity, smoke results, disposition, and primary candidate artifact SHA-256 values to `build/reports/published-security`.

### Module regressions

The following command completed successfully:

```text
./gradlew --no-daemon --no-parallel -PbuildSrc.skipTests=true \
  :bjca-footstone-bpring-security-crypto:test \
  :bjca-footstone-bpring-security-saml2-service-provider:opensaml3Test \
  :bjca-footstone-bpring-security-ldap:test \
  :bjca-footstone-bpring-security-ldap:integrationTest \
  :bjca-footstone-bpring-security-oauth2-jose:test
```

### Spring LDAP CVE-2026-41720

Spring LDAP 2.4.5 was not available from Maven Central or the configured Nexus repositories. The publication therefore retains 2.4.4 and does not claim that upstream Spring LDAP is patched.

The supported Spring Security authentication paths reject empty passwords before LDAP bind:

- `BindAuthenticator.authenticate`
- `AbstractLdapAuthenticationProvider.authenticate`

Coverage is provided by `BindAuthenticatorTests.emptyPasswordIsRejected` and `LdapAuthenticationProviderTests.testEmptyOrNullPasswordThrowsException`.
