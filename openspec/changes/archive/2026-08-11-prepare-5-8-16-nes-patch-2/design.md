## Context

The repository has an immutable, Nexus-verified `5.8.16-nes.patch.1` release and tag. The branch now contains the next security fixes and must use a distinct development coordinate before any further candidate publication. Several release helpers still embed the old release version, so a version-only change would leave consumers and evidence pointed at stale artifacts.

## Goals / Non-Goals

**Goals:**

- Set the live Gradle version to `5.8.16-nes.patch.2-SNAPSHOT`.
- Make the publication verifier read the version from the live build metadata and pass it to both consumers.
- Keep candidate repository paths, dependency trees, evidence, and SHA-256 records bound to the same current version.
- Update live documentation and OpenSpec requirements without rewriting patch.1 archive evidence.

**Non-Goals:**

- Do not remove `-SNAPSHOT` or publish a RELEASE coordinate in this change.
- Do not create or push a patch.2 Git tag.
- Do not change runtime code, dependency baselines, CVE behavior, Java targets, or public APIs.
- Do not overwrite, delete, or reconcile existing patch.1 Nexus assets.

## Decisions

### Read the version from `gradle.properties`

`gradle.properties` is the repository source of truth used by Gradle. The shell verifier will parse its `version` key once and use that value for artifact existence checks, Maven `spring.security.version`, and Gradle `securityVersion`. This avoids duplicating a release coordinate in three consumers and prevents stale evidence after a version bump.

Using a separate manually maintained shell variable is rejected because it already caused the patch.1 coordinate to survive after the branch moved past that release. Calling a full Gradle `properties` task is also unnecessary for this simple metadata lookup and would add another build dependency to the shell preflight.

### Keep consumer defaults aligned and allow explicit injection

The Maven smoke POM defaults to the current snapshot and accepts `-Dspring.security.version` from the verifier. The Gradle smoke build accepts `-PsecurityVersion`. The verifier passes the parsed version to both, so local standalone consumers remain understandable while automated evidence is unambiguous.

The candidate Maven repository explicitly enables both release and snapshot policies. Maven uses a separate disposable local cache so resolver status and `.lastUpdated` files cannot contaminate the candidate bytes whose hashes are recorded.

The verifier also supplies a repository-controlled Maven global settings file whose mirror applies only to `external:*` repositories. This keeps ordinary remote dependency resolution on the approved Nexus proxy while preventing a user-level `mirrorOf=*` rule from redirecting the local `file://` candidate repository back to Nexus.

### Treat SNAPSHOT as development-only

The candidate repository may contain `5.8.16-nes.patch.2-SNAPSHOT` artifacts for Java 8 verification. `make deploy` remains the release owner and must not be used to publish this snapshot to the RELEASE repository. A later release change will freeze the version as `5.8.16-nes.patch.2`, verify Nexus absence, deploy once, reconcile remote assets, and then tag.

## Risks / Trade-offs

- [A malformed or missing version property produces an invalid candidate path] → Fail the verifier early unless the parsed value is non-empty and matches the expected internal version syntax.
- [A consumer silently falls back to patch.1] → Pass the version explicitly to Maven and Gradle and assert the candidate directory for the exact parsed version exists.
- [A SNAPSHOT is accidentally sent to a release repository] → Keep release deployment and tag creation out of this change; document the distinction in Nexus instructions.
- [Historical evidence is accidentally rewritten] → Restrict edits to live files and the new change; leave `openspec/changes/archive/**` untouched.

## Migration Plan

1. Update the live version and consumer/verifier inputs to `5.8.16-nes.patch.2-SNAPSHOT`.
2. Publish a candidate to the isolated local repository and run Java 8 Maven/Gradle consumers.
3. Review generated evidence and commit the development-version change.
4. For a formal release, create a separate release change that removes `-SNAPSHOT`, verifies the new RELEASE coordinate is absent from Nexus, deploys once, downloads and verifies remote assets, and creates `v5.8.16-nes.patch.2`.

## Open Questions

None for the development coordinate. The exact formal release timing and Nexus authorization remain release-coordinator decisions.
