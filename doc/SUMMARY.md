# Spring Security 5.7.x CVE 漏洞修复报告

## 项目概述

本项目针对 Spring Security 5.7.14 的 4 个已知 CVE 漏洞进行了系统性的研究、影响评估、代码修复、测试编写和文档记录。

| 项目 | 详情 |
|------|------|
| 基线版本 | Spring Security 5.7.14 |
| 源分支 | `5.7.x` |
| 工作分支 | `5.7.x-bjca-patch` |
| 构建环境 | Java 11（Amazon Corretto 11.0.29），产出兼容 Java 8+ |
| 完成日期 | 2026-03-02 |

---

## CVE 评估与处置总览

| CVE 编号 | 漏洞名称 | CVSS | 评估结论 | 处置方式 |
|----------|---------|------|---------|---------|
| CVE-2024-38827 | Locale 依赖授权绕过 | 4.8 Medium | 不受影响 | 仅文档记录 |
| CVE-2025-22228 | BCrypt 密码静默截断 | 7.4 HIGH | **受影响** | **代码修复 + 测试** |
| CVE-2025-22233 | DataBinder 大小写绕过 | 3.1 Low | 不适用 | 仅文档记录 |
| CVE-2025-22234 | BCrypt 长度限制破坏时序防护 | 5.3 Medium | **受影响** | **合并在 CVE-2025-22228 修复中** |

---

## 各 CVE 详细说明

### CVE-2024-38827 — 不受影响

- **漏洞**: `String.toLowerCase()`/`toUpperCase()` 的无参调用在特定语言环境下导致授权规则失效
- **原因**: 当前基线 5.7.14 已包含官方修复 commit `0eaffb37e7`（该修复正是 5.7.14 发布的一部分）
- **处置**: 无需代码修改，创建文档 `doc/CVE/CVE-2024-38827.md`

### CVE-2025-22228 — 已修复

- **漏洞**: `BCrypt.hashpw()` 对超过 72 字节的密码静默截断，不同密码可能产生相同哈希值，导致认证绕过
- **官方修复**: commit `46f0dc6d`（"Enforce BCrypt password length"），修复版本 5.7.16
- **本地修复**: 在 `BCrypt.hashpw(byte[], String, boolean)` 方法中添加密码字节长度校验
- **合并策略**: 同时 backport 了 CVE-2025-22234 的回归修复，仅对新密码编码操作做 72 字节限制，验证操作不受限制
- **测试**: 新增 5 个测试用例，全部 31 个测试通过
- **Commit**: `e9fec59376`

### CVE-2025-22233 — 不适用

- **漏洞**: `DataBinder.isAllowed()` 的 `disallowedFields` 模式匹配存在大小写绕过
- **原因**: **该漏洞属于 Spring Framework**（`spring-context` 模块），非 Spring Security 代码库范围
- **处置**: 无法通过修改 Spring Security 代码修复；如需修复应升级 Spring Framework 依赖至 5.3.43+
- **文档**: `doc/CVE/CVE-2025-22233.md`

### CVE-2025-22234 — 已修复（合并）

- **漏洞**: CVE-2025-22228 的修复对所有密码操作执行长度限制，破坏了 `DaoAuthenticationProvider` 的时序攻击防护
- **官方修复**: commit `c1aa99fdd2`（"Enforce BCrypt password length for new passwords only"），修复版本 5.7.17
- **本地修复**: 已在 CVE-2025-22228 的修复中通过 `!for_check` 条件一并解决，从未引入回归
- **文档**: `doc/CVE/CVE-2025-22234.md`

---

## 代码变更清单

### 修改的文件

| 文件 | 变更说明 |
|------|---------|
| `crypto/src/main/java/org/springframework/security/crypto/bcrypt/BCrypt.java` | 在 `hashpw()` 方法中添加密码 72 字节长度校验（+10 行） |
| `crypto/src/test/java/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoderTests.java` | 新增 5 个 CVE 修复验证测试用例（+76 行） |

### 核心修复代码

```java
// CVE-2025-22228 修复：防止超过 72 字节的密码被静默截断
// CVE-2025-22234 修复：仅对新密码编码操作做限制，保持向后兼容
if (!for_check && passwordb.length > 72) {
    throw new IllegalArgumentException("password cannot be more than 72 bytes");
}
```

### 新增测试用例

| 测试方法 | 验证场景 |
|---------|---------|
| `encodeAndMatchNormalLengthPassword` | 正常长度密码的编码和验证不受影响 |
| `encodeWhenPasswordAtMaxLengthThenSuccess` | 恰好 72 字节的密码正常编码和验证 |
| `encodeWhenPasswordOverMaxLengthThenThrowIllegalArgumentException` | 超过 72 字节的新密码编码时抛出异常 |
| `matchesWhenPasswordOverMaxLengthThenAllowToMatch` | 超长密码验证时不抛异常，保持向后兼容 |
| `encodeWhenMultiBytePasswordOverMaxByteLengthThenThrowIllegalArgumentException` | 多字节 UTF-8 字符按字节数而非字符数校验 |

---

## Git 提交记录

```
e9fec59376 修复 CVE-2025-22228：BCrypt 密码长度校验（合并 CVE-2025-22234 回归修复）
```

---

## 文档清单

| 文件路径 | 说明 |
|---------|------|
| `doc/REQUIREMENTS.md` | 项目需求文档 |
| `doc/SUMMARY.md` | 本总结文档 |
| `doc/CVE/CVE-2024-38827.md` | CVE-2024-38827 详细报告（不受影响） |
| `doc/CVE/CVE-2025-22228.md` | CVE-2025-22228 详细报告（已修复） |
| `doc/CVE/CVE-2025-22233.md` | CVE-2025-22233 详细报告（不适用） |
| `doc/CVE/CVE-2025-22234.md` | CVE-2025-22234 详细报告（合并修复） |

---

## 构建与测试说明

### 环境要求

项目使用 Java 11+ 编译（通过 `--release 8` 编译器选项产出 Java 8 兼容字节码）：

```bash
sdk use java 11.0.29-amzn
```

### 运行测试

```bash
./gradlew :spring-security-crypto:test --tests "org.springframework.security.crypto.bcrypt.BCryptPasswordEncoderTests" --no-daemon
```

预期结果：31 tests completed, 0 failed — BUILD SUCCESSFUL

---

## 遗留事项与建议

1. **CVE-2025-22233**: 该漏洞位于 Spring Framework `spring-context` 模块。如需完整修复，建议将项目依赖的 Spring Framework 版本升级至 5.3.43+
2. **持续关注**: 建议定期检查 [Spring Security 安全公告](https://spring.io/security) 获取新的 CVE 信息
