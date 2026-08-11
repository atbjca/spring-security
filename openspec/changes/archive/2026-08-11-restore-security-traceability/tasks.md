## 1. Static traceability

- [x] 1.1 Document the internal group/version, upstream 5.8.16 source baseline, and security-behavior-only 5.8.27 boundary
- [x] 1.2 Confirm the seven CVE entries map official ranges and upstream references to local commits, modules, and tests
- [x] 1.3 Retain the version-boundary evidence for CVE-2026-22748
- [x] 1.4 Link the archived CVE backport and dependency/Java 8 changes from the release evidence document

## 2. Candidate evidence binding

- [x] 2.1 Record the current Git commit and tracked worktree state during candidate verification
- [x] 2.2 Generate an index for dependency trees, Java 8 identity, smoke results, dispositions, and candidate SHA-256 files
- [x] 2.3 Run the publication gate and verify every indexed evidence file exists and is non-empty

## 3. Completion

- [x] 3.1 Verify every documented local CVE commit exists in Git history
- [x] 3.2 Review the documentation and script diff for stale placeholders or expanded governance scope
- [x] 3.3 Run strict OpenSpec validation and archive the change
