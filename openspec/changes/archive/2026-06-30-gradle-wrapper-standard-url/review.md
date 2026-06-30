# Review：Gradle Wrapper URL 标准化

## Review 日期
2026-06-30

## 变更摘要

| 项目 | 内容 |
|------|------|
| 变更名称 | gradle-wrapper-standard-url |
| 变更类型 | 配置标准化 |
| 复杂度 | 低 |
| 风险 | 低 |

## Review 要点

### 1. 提案合理性 ✅
- 问题描述清晰：file:// 硬编码路径不可移植
- 参考方案来自同一组织的 spring-boot-3.5 项目，可信度高
- 非目标明确，避免范围蔓延

### 2. 设计完整性 ✅
- 架构图清晰展示了 Gradle Wrapper 缓存机制
- 文件变更清单完整，无遗漏
- 行为对比表覆盖了主要场景
- 验证方案具体可执行

### 3. 任务分解 ✅
- 4 个任务，边界清晰
- 执行顺序合理（有依赖的任务标记清楚）
- 时间估算合理

### 4. 潜在问题

**Q1: Python 依赖**
- 当前脚本使用 Python3 计算 MD5 hash
- macOS 默认有 Python3，但某些最小化环境可能没有
- **决议**：保持 Python 实现。macOS 标配 Python3，收益 > 成本。

**Q2: 网络首次下载**
- 如果用户未执行 setup-gradle 且 ~/dev 下没有对应 zip，首次会从网络下载
- **决议**：这是预期行为，与标准 Gradle Wrapper 完全一致

**Q3: 多版本 Gradle**
- ~/dev 下有多个版本的 gradle zip（5.2 到 8.14.5）
- setup-gradle 会处理所有找到的 zip
- **决议**：这是合理行为，可以一次性缓存所有版本

### 5. 与 spring-boot-3.5 的差异

| 方面 | spring-boot-3.5 | 本变更 |
|------|------------------|--------|
| setup-gradle 脚本 | ✅ 相同 | 注释改为中文 |
| gradle-wrapper.properties | https URL ✅ | https URL ✅ |
| Makefile setup-gradle 依赖 | ✅ | ✅ |

## 结论

**通过** ✅

变更风险低，方案成熟（直接复用 spring-boot-3.5 验证过的方案），建议批准并执行。
