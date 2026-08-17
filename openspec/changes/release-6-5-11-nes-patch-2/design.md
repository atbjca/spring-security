## Context

`spring-security-6.5` is on branch `6.5.x-bjca-patch` at development version `6.5.11-nes.patch.2-SNAPSHOT`. The current HEAD contains the completed Spring Data, Spring LDAP, Bouncy Castle, and Jackson remediation work plus its focused development verification. Patch 1 is already immutable in Nexus and Git, so these changes must be released as patch 2.

The central catalog declares 19 Spring Security publications and currently lists only `spring-framework-6.2` as an upstream. The actual publication graph also contains the direct internal dependency `cn.bjca.footstone.bpring.data:bjca-footstone-bpring-data-commons:3.5.13-nes.patch.1`. Spring Framework `6.2.19-nes.patch.1` is already `nexus-verified` in the 2026-08-14 run manifest. Spring Data Commons is already published, but its entry in that manifest is stale at `planned` and must be reconciled as a verified baseline before this release proceeds.

The release workstation has limited resources. Documentation, Git inspection, Nexus reads, and independent OpenSpec preparation may overlap, but every Make, Maven, or Gradle process shares one global build slot. Every Gradle invocation uses `--no-daemon --no-parallel --max-workers=1`; the release workflow does not rely on the repository's `org.gradle.parallel=true` default.

The complete publication set is:

1. `bjca-footstone-bpring-security-acl`
2. `bjca-footstone-bpring-security-aspects`
3. `bjca-footstone-bpring-security-bom`
4. `bjca-footstone-bpring-security-cas`
5. `bjca-footstone-bpring-security-config`
6. `bjca-footstone-bpring-security-core`
7. `bjca-footstone-bpring-security-crypto`
8. `bjca-footstone-bpring-security-data`
9. `bjca-footstone-bpring-security-ldap`
10. `bjca-footstone-bpring-security-messaging`
11. `bjca-footstone-bpring-security-oauth2-client`
12. `bjca-footstone-bpring-security-oauth2-core`
13. `bjca-footstone-bpring-security-oauth2-jose`
14. `bjca-footstone-bpring-security-oauth2-resource-server`
15. `bjca-footstone-bpring-security-rsocket`
16. `bjca-footstone-bpring-security-saml2-service-provider`
17. `bjca-footstone-bpring-security-taglibs`
18. `bjca-footstone-bpring-security-test`
19. `bjca-footstone-bpring-security-web`

All use group `cn.bjca.footstone.bpring.security`, version `6.5.11-nes.patch.2`; the BOM is a POM publication and the other 18 are JAR publications. There are no approved exclusions.

## Goals / Non-Goals

**Goals:**

- Produce one immutable, auditable `6.5.11-nes.patch.2` RELEASE and annotated tag `v6.5.11-nes.patch.2`.
- Make the actual Spring Framework and Spring Data Commons RELEASE graph a hard prerequisite.
- Reconcile the stale Spring Data Commons central manifest entry through live Nexus evidence without redeploying or retagging that baseline.
- Reuse still-valid focused development verification while retaining release-specific local publication, metadata, absence, remote, and consumer gates.
- Prevent concurrent or multi-worker build execution and keep release activity within the global build slot.
- Preserve unrelated work, exclude tool directories, and keep credentials out of Git and evidence.
- Allow the release CLI to create and consume TAG/PUSH authorization internally once objective gates pass, without repeated user confirmations.
- Keep Nexus RELEASE immutable and recover without overwriting or blindly redeploying the same version.

**Non-Goals:**

- Do not change application source, tests, APIs, or dependency baselines as part of release preparation.
- Do not rerun broad `make build` or `make test` merely to recreate development evidence.
- Do not deploy, modify, or retag Spring Framework or Spring Data Commons baseline releases.
- Do not publish `6.5.11-nes.patch.1` again.
- Do not create the next development SNAPSHOT as part of this release change.
- Do not delete or overwrite Nexus assets, rewrite Git history, or store credentials in project files.

## Decisions

### 1. Reconcile the actual dependency graph before component preparation

The release catalog is updated so `spring-security-6.5` depends on both `spring-framework-6.2` and `spring-data-commons-3.5`. Before the Spring Security manifest entry advances, the coordinator performs live, read-only Nexus verification of the already published Spring Data Commons POM, main JAR, sources JAR, and Javadoc JAR, records checksums, scans its POM for internal SNAPSHOT references, and advances that baseline entry from stale `planned` to `nexus-verified`.

Spring Framework remains a verification-only baseline using its existing complete-set evidence. Neither baseline is deployed or tagged in this run.

Alternative rejected: trust documentation that says Spring Data Commons is released while leaving the central manifest at `planned`. That would make dependency scheduling and recovery unsafe in a fresh session.

### 2. Use one owner lease and one global build slot

The component owner lease protects repository writes. A separate central build-slot lease protects all Make, Maven, and Gradle commands across components. Only one build process may hold that slot, and it must be released after each command, including failures.

Non-build work may proceed concurrently when it writes disjoint files. No two sessions edit the same component repository or central manifest section concurrently.

Alternative rejected: serialize every documentation and Git inspection operation. Those operations do not consume the constrained build resources and needlessly slow the train.

### 3. Override Gradle parallel defaults on every invocation

Release commands invoke Gradle directly or through a verified wrapper command with:

```text
--no-daemon --no-parallel --max-workers=1
```

Local publication is incremental and skips tests and `clean`:

```text
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 ./gradlew --no-daemon --no-parallel --max-workers=1 publishToMavenLocal -x test -PbuildSrc.skipTests=true
```

The single Nexus deploy uses the same constraints:

```text
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 ./gradlew --no-daemon --no-parallel --max-workers=1 publishAllPublicationsToNexusRepository -x test -PbuildSrc.skipTests=true
```

The central catalog command/evidence must reflect the safe command rather than silently relying on the current Makefile, whose Gradle calls do not override `org.gradle.parallel=true`.

Alternative rejected: use `make install` or `make deploy` unchanged. Those targets currently run `clean` and do not enforce the required daemon, parallelism, or worker limits.

### 4. Reuse development verification proportionally

The release records the latest source commit and the archived OpenSpec tasks for dependency remediation, Jackson alignment, dependency-graph inspection, and focused affected-module tests. That evidence is reusable only if no subsequent source, test, or dependency-baseline change exists between the verified commit and the release commit.

Version promotion, release documentation, OpenSpec evidence, and central manifest edits do not invalidate the focused test evidence. Release-specific checks are still mandatory: local publication, complete generated POM/BOM scan, representative local consumer, exact release diff, and remote RELEASE-only consumer after deploy.

Alternative rejected: rerun all build and test targets without an invalidating change. It adds hours of workstation load without increasing evidence quality for a metadata-only release diff.

### 5. Bind the complete 19-GAV set to one release commit

Promote only `version` to `6.5.11-nes.patch.2`; retain `springSecurityVersion=6.5.11`. Update current release documentation and discover the generated publications to confirm all 19 expected GAVs and no unexpected publications. Scan every generated POM/BOM for internal `-SNAPSHOT` versions and for the approved Spring Framework and Spring Data Commons RELEASE coordinates.

The dedicated release commit contains only approved version, documentation, OpenSpec, and release metadata changes. `.claude/`, `.codex/`, `.cursor/`, credentials, caches, and unrelated files are never staged.

Alternative rejected: verify only the representative core GAV. A multi-publication release can otherwise leave hidden SNAPSHOT metadata or omit a module.

### 6. Treat absence as the final pre-deploy gate

Immediately before deploy, query Nexus RELEASE for every expected asset path for all 19 GAVs, including POM and binary/source/Javadoc assets as applicable. Any complete or partial target-version asset blocks deployment. The absence timestamp and result are tied to the release commit and deploy authorization.

Only one deploy attempt is authorized. If any asset appears or the outcome is uncertain, reconcile remote state first; never repeat deployment for the same coordinates merely because the command failed locally.

Alternative rejected: check only representative core POM/JAR paths. Partial assets in another module would make the version immutable and unsafe to redeploy.

### 7. Automate compliance and CLI authorization without weakening gates

The coordinator reviews license/notice changes, new dependency licenses, source/binary publication shape, credential isolation, and known release-risk records. No manual “compliance satisfied” confirmation is requested when there is no concrete finding. A concrete legal, licensing, or disclosure risk blocks the release and is reported.

After `nexus-verified`, the CLI internally creates and consumes the exact TAG and PUSH authorization for `v6.5.11-nes.patch.2`, then records tag object, peeled commit, remote branch, and remote tag evidence. These internal tokens do not bypass state checks and are never committed.

Alternative rejected: repeatedly interrupt the operator for deterministic tag/push steps after all objective gates have passed.

### 8. Preserve immutability through reconciliation and archive

After deploy, verify all 19 remote publications, scan downloaded POM/BOM metadata, record asset URLs and checksums, and run a consumer with snapshot repositories disabled. Create and push the annotated tag only after `nexus-verified`.

Keep the OpenSpec active until component Git, remote Git, Nexus, component documentation, catalog, and manifest agree. Archive only from `documented`; record the archive path and release the owner/build leases.

If Nexus publication is verified but Git push fails, retry only Git. If publication is partial or invalid, preserve evidence and plan a higher NES patch; never overwrite patch 2.

## State Progression

```text
planned
  -> dependency-reconciled
  -> openspec-ready
  -> prepared
  -> locally-verified
  -> release-committed
  -> deployed
  -> nexus-verified
  -> tagged
  -> documented
  -> archived
```

`blocked`, `partial-failure`, and `superseded` require explicit reconciliation; none authorizes redeploying existing RELEASE coordinates.

## Risks / Trade-offs

- **[Risk] Spring Data Commons exists in Nexus but the manifest remains stale** → Verify its complete RELEASE assets and metadata, then update the baseline entry before acquiring the Spring Security release lease.
- **[Risk] Catalog dependency omission permits unsafe scheduling** → Add `spring-data-commons-3.5` to the declared dependency list and validate catalog/manifest topology.
- **[Risk] Repository defaults enable parallel Gradle workers** → Supply all three Gradle serialization flags on every invocation while holding the global build slot.
- **[Risk] Reused development evidence no longer matches release inputs** → Compare the verified source/test/dependency files to the release commit and rerun only invalidated focused checks.
- **[Risk] One of 19 modules is missing or retains a SNAPSHOT** → Reconcile the generated complete set and scan every POM/BOM locally and remotely.
- **[Risk] Multi-module deploy partially succeeds** → Stop, inventory Nexus, mark `partial-failure`, and select a higher NES patch instead of redeploying.
- **[Risk] Tool files contaminate the release commit** → Stage explicit approved paths and inspect the staged diff before commit.
- **[Risk] Automated compliance misses a material change** → Fail closed on new licenses, missing notices, new third-party dependencies, credential exposure, or unresolved advisory risk.
- **[Risk] Session loss occurs after an irreversible action** → Recover from manifest, Nexus, Git, and OpenSpec evidence; never infer that deploy should be repeated.

## Migration Plan

1. Reconcile catalog topology and the Spring Data Commons baseline manifest state using live Nexus evidence.
2. Acquire the component lease, record repository state, create the release evidence skeleton, and advance to `openspec-ready`.
3. Promote version/documentation, validate the 19-GAV local publication set, and reuse or selectively refresh development evidence.
4. Publish locally and run metadata/consumer gates under the global build slot.
5. Create and push the dedicated release commit, then verify complete Nexus absence.
6. Execute the single serialized deploy and perform complete remote verification.
7. Create/push/verify the annotated tag using CLI-internal authorization.
8. Reconcile documentation and central records, archive the OpenSpec, and release all leases.

Rollback before deploy consists of reverting only the uncommitted or dedicated release-preparation changes. After any Nexus asset exists, rollback by deletion or overwrite is forbidden; recovery uses reconciliation and, if necessary, a higher NES patch version.

## Open Questions

None. The release remains blocked until the Spring Data Commons baseline manifest reconciliation is complete.
