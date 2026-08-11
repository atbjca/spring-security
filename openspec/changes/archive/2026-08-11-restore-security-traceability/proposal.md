## Why

七项 2026 CVE 已有上游提交、本地回移提交和测试记录，依赖与 Java 8 候选验证也会生成哈希和运行证据，但这些信息分散在 CVE 文档、OpenSpec archive 和 `build/reports`。发布审核需要一个简短、稳定的索引说明内部版本对应哪个上游基线，以及到哪里查找具体补丁和候选构件证据。

## What Changes

- 在现有 `doc/CVE/CVE-2026-security-backports.md` 中明确内部坐标、上游源码基线和“安全行为等价但非完整版本等价”的边界
- 保留七项 CVE → 上游参考提交 → 本地提交 → 回归测试的现有表格，并清理过时占位说明
- 索引已归档的 CVE backport 与依赖/Java 8 publication changes
- 让 `make verify-published-security` 记录候选构件对应的当前 Git commit、tracked worktree 状态和证据文件清单
- 不生成 SBOM/VEX，不新增 Nexus 回下载或持续公告治理平台

## Capabilities

### New Capabilities

- `security-advisory-traceability`: 定义当前内部版本所需的最小 CVE、提交、测试和候选发布证据索引

### Modified Capabilities

无。

## Impact

- 文档：`doc/CVE/CVE-2026-security-backports.md`
- 发布验证：`scripts/verify-published-security.sh` 生成额外的 commit/evidence index
- 不修改运行时代码、依赖版本、发布坐标或 Nexus 流程

## Out Of Scope

- CycloneDX、VEX、SBOM 签名和证据治理平台
- 部署后从 Nexus 下载并逐项比较构件
- 定时公告 feed 或 Spring Framework 跨仓 attestation
- Jackson 和外部 Logback fork 的源码修复
