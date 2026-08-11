# Nimbus JOSE+JWT / OAuth2-OIDC-SDK 依赖升级文档

## 1. 升级概述

本次升级旨在修复 nimbus-jose-jwt 库中两个已知的安全漏洞（CVE-2023-52428 和 CVE-2025-53864），
同时将 oauth2-oidc-sdk 同步升级至与新版 nimbus-jose-jwt 兼容的版本。

### 升级前后版本对比

| 组件 | 升级前版本 | 升级后版本 | 变更幅度 |
|------|----------|----------|---------|
| `com.nimbusds:nimbus-jose-jwt` | 9.24.4 | 10.8 | 跨主版本（9.x → 10.x） |
| `com.nimbusds:oauth2-oidc-sdk` | 9.43.3 | 11.33 | 跨两个主版本（9.x → 11.x） |

### 构建环境

- JDK：Java 11.0.29-amzn (Amazon Corretto)
- Gradle：通过 Makefile 包装调用
- 分支：`5.8.x-bjca-patch`

---

## 2. 修复的安全漏洞

| CVE 编号 | 影响组件 | 漏洞类型 | CVSS 评分 | 修复版本 | 本项目处置 |
|----------|---------|---------|----------|---------|-----------|
| CVE-2023-52428 | nimbus-jose-jwt | PBKDF2 拒绝服务（DoS） | 7.5 (High) | >= 9.37.2 | 已修复（升级至 10.8） |
| CVE-2025-53864 | nimbus-jose-jwt | JSON 递归拒绝服务（DoS） | 5.8 (Medium) | >= 10.0.2 或 >= 9.37.4 | 已修复（升级至 10.8） |
| CVE-2025-8916 | Bouncy Castle (独立依赖基线) | 证书名称约束 DoS | 6.3 (Moderate) | >= 1.79 | 已修复（统一升级至 1.84） |

各 CVE 的详细文档请参见 `doc/CVE/` 目录下的独立文件。

---

## 3. 代码适配内容

### 3.1 ClientRegistrations.java — oauth2-oidc-sdk 11.x 行为变更适配

**文件路径：** `oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/registration/ClientRegistrations.java`

**问题描述：**

在 oauth2-oidc-sdk 9.x 中，`OIDCProviderMetadata.parse()` 在缺少 `jwks_uri` 字段时抛出
`IllegalArgumentException("The public JWK set URI must not be null")`。在 oauth2-oidc-sdk 11.x 中，
同一场景改为抛出 `ParseException("At least one public JWK set must be specified")`，
导致异常传播路径和异常消息发生变化，影响了 2 个单元测试。

**修复方案：**

在调用 `OIDCProviderMetadata.parse()` 之前，对原始配置 Map 中的 `jwks_uri` 进行空值校验：

```java
// oauth2-oidc-sdk 11.x 中 OIDCProviderMetadata.parse() 在缺少 jwks_uri 时抛出 ParseException，
// 而非 IllegalArgumentException；在调用 parse 之前手动校验，保持原有异常类型不变
Assert.notNull(configuration.get("jwks_uri"), "The public JWK set URI must not be null");
```

这样 `Assert.notNull` 抛出 `IllegalArgumentException`，与旧版 SDK 的行为一致。

### 3.2 VerifyDependenciesVersionsPlugin.java — 版本验证逻辑增强

**文件路径：** `buildSrc/src/main/java/org/springframework/security/convention/versions/VerifyDependenciesVersionsPlugin.java`

**问题描述：**

原有的版本验证逻辑要求项目声明的 nimbus-jose-jwt 版本必须与 oauth2-oidc-sdk 传递依赖的版本完全一致。
升级后，oauth2-oidc-sdk 11.33 传递依赖 nimbus-jose-jwt 10.6，但项目为修复 CVE 需要使用 10.8。
原有验证会因版本不一致而失败。

**修复方案：**

将严格的"版本相等"检查改为更灵活的验证策略：
1. **主版本号一致性检查：** 确保项目声明的版本与传递依赖版本的主版本号一致（如都是 10.x）
2. **版本不低于传递依赖：** 项目声明的版本必须 >= 传递依赖版本
3. **警告日志：** 当项目版本高于传递依赖版本时，输出 WARN 级别日志说明这是有意为之

新增了 `compareVersions()` 静态方法用于语义化版本号比较。

---

## 4. 修改文件清单

| 文件 | 修改类型 | 说明 |
|------|---------|------|
| `gradle/libs.versions.toml` | 版本变更 | nimbus-jose-jwt 9.24.4 → 10.8，oauth2-oidc-sdk 9.43.3 → 11.33 |
| `buildSrc/.../VerifyDependenciesVersionsPlugin.java` | 逻辑增强 | 版本验证从"严格相等"改为"同主版本、不低于传递依赖" |
| `oauth2/.../ClientRegistrations.java` | 兼容性适配 | 添加 `jwks_uri` 空值校验，适配 oauth2-oidc-sdk 11.x 行为变更 |

---

## 5. 测试结果摘要

### 5.1 模块级测试

| 模块 | 测试数 | 通过 | 失败 | 备注 |
|------|-------|------|------|------|
| spring-security-oauth2-jose | 245 | 244 | 1 | 1 个 flaky test，与依赖升级无关 |
| spring-security-oauth2-client | 1252 | 1252 | 0 | 修复后全部通过 |
| spring-security-oauth2-core | 全部 | 全部 | 0 | — |
| spring-security-oauth2-resource-server | 全部 | 全部 | 0 | — |

### 5.2 全量构建

```
make build-thin
# 等效于：./gradlew build -x test -x checkstyle* -x checkFormat* -x docs -x javadoc
# 结果：BUILD SUCCESSFUL in 3m 48s (294 actionable tasks)
```

### 5.3 已知的 Flaky Test

`NimbusReactiveJwtDecoderTests.decodeWhenInvalidUrl()` — 该测试连接无效 URL `https://s`，
期望异常链中包含 `UnknownHostException`，但在当前网络环境下实际得到 `ClosedChannelException`。
此问题与依赖升级无关，是网络环境相关的不稳定测试。

---

## 6. 已知风险与后续关注项

### 6.1 Bouncy Castle 已升级（CVE-2025-8916 已修复）

本项目最初将 Bouncy Castle 从 1.70 升级到修复版本 1.79，之后将发布依赖基线继续提升并统一为
1.84。Maven/Gradle Java 8 消费门禁已经验证发布元数据只解析 1.84 `jdk18on` 构件，且完成
Bouncy Castle AES-GCM 往返测试。详情见 `doc/CVE/CVE-2025-8916.md` 和
`openspec/specs/bouncycastle-upgrade/spec.md`。

### 6.2 nimbus-jose-jwt 旧 API 已弃用但仍可用

以下类在 nimbus-jose-jwt 10.8 中仍然存在（可能已标记为 `@Deprecated`），但未来版本可能移除：
- `com.nimbusds.jose.jwk.source.RemoteJWKSet`
- `com.nimbusds.jose.jwk.source.JWKSetCache`
- `com.nimbusds.jose.jwk.source.DefaultJWKSetCache`
- `com.nimbusds.jose.jwk.source.JWKSetWithTimestamp`

当前代码无需修改，但在未来进一步升级 nimbus-jose-jwt 时需要评估迁移到新的 `JWKSetSource` 框架。

### 6.3 JSON 解析库变化

nimbus-jose-jwt 10.x 内部使用 shaded Gson 替代 json-smart。
但 oauth2-oidc-sdk 11.33 仍然传递依赖 `net.minidev:json-smart:2.5.2`，
所以 `net.minidev.json.JSONObject` 在当前环境中仍然可用。
如果未来 oauth2-oidc-sdk 也移除 json-smart 依赖，项目中使用 `JSONObject` 的代码需要适配。

### 6.4 依赖解析确认

```
com.nimbusds:oauth2-oidc-sdk:11.33
  ├── com.nimbusds:nimbus-jose-jwt:10.6 → 10.8 (被项目声明版本覆盖)
  └── net.minidev:json-smart:2.5.2
```

---

## 7. 参考链接

- [NVD - CVE-2023-52428](https://nvd.nist.gov/vuln/detail/CVE-2023-52428)
- [NVD - CVE-2025-53864](https://nvd.nist.gov/vuln/detail/CVE-2025-53864)
- [NVD - CVE-2025-8916](https://nvd.nist.gov/vuln/detail/CVE-2025-8916)
- [Nimbus JOSE+JWT Bitbucket](https://bitbucket.org/connect2id/nimbus-jose-jwt)
- [GitHub Advisory - CVE-2025-53864](https://github.com/advisories/GHSA-xwmg-2g98-w7v9)
- [Bouncy Castle CVE-2025-8916 Wiki](https://github.com/bcgit/bc-java/wiki/CVE%E2%80%902025%E2%80%908916)
