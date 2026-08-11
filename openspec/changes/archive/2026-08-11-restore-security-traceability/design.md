## Context

`doc/CVE/CVE-2026-security-backports.md` 已记录七项适用 CVE 和 `CVE-2026-22748` 的不适用结论。`secure-published-dependencies` 已在 `make deploy` 前生成 Maven/Gradle 依赖树、Java 8 身份、smoke 结果和候选 SHA-256。缺少的是把内部发布身份、这些静态/动态证据和确切 source commit 连接起来的最小索引。

## Goals / Non-Goals

**Goals:**

- 让审核者从内部版本找到上游源码基线和七项安全回移
- 让每项适用 CVE 保持上游参考、本地提交和测试证据可查
- 让每次候选验证记录当前 source commit 与证据文件列表
- 区分“5.8.27 安全行为基线”与“完整升级到上游 5.8.27”

**Non-Goals:**

- 不创建 SBOM/VEX schema、生成器或治理状态机
- 不改变现有发布生命周期，不增加部署后远端校验
- 不重复实现依赖和 Java 8 gates

## Decisions

### 1. 复用现有 CVE 文档作为权威静态映射

已有表格包含官方 advisory、影响范围、上游参考提交、本地实现提交、模块和测试结果。直接修正并扩展该文档，避免再维护一份容易漂移的 JSON/VEX 副本。

### 2. 身份映射以发布版本为粒度

内部 group 为 `cn.bjca.footstone.bpring.security`，版本为 `5.8.16-nes.patch.1`，源码基于 Spring Security 5.8.16/`5.8.x`。七项回移使指定漏洞行为达到相应 5.8.24-5.8.27 修复水平，但不声明包含这些上游版本的全部功能与非安全改动。

### 3. 动态证据绑定当前 Git commit

候选验证在 `build/reports/published-security/source-state.txt` 中写入 `git rev-parse HEAD` 和 tracked worktree 是否有改动，并在 `evidence-index.txt` 列出依赖树、Java 8、smoke、处置和 SHA-256 文件。候选构件本身仍由 `candidate-sha256.txt` 精确绑定。

### 4. OpenSpec archive 是设计与验收记录，不替代发布证据

CVE backport 和 dependency/Java 8 changes 的 archive 路径进入文档索引；动态构件证据仍由每次 `make verify-published-security` 重新生成。这样既保留设计决策，也不会把机器构建产物提交到 Git。

## Verification

1. 检查文档中的七个本地提交均存在于 Git 历史。
2. 运行 `make verify-published-security`，确认 `source-state.txt` 和 `evidence-index.txt` 生成。
3. 检查 evidence index 中的每个生成文件存在且非空。
4. 运行 `openspec validate restore-security-traceability --strict`。
