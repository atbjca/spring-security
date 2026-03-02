# Nimbus 依赖升级报告

## 升级概述

升级 `com.nimbusds` 组件以修复已知安全漏洞（CVE-2023-52428、CVE-2025-53864、CVE-2025-8916）。

| 组件 | 升级前 | 升级后 | 升级原因 |
|------|--------|--------|---------|
| nimbus-jose-jwt | 9.22 | 10.8 | 修复 CVE-2023-52428、CVE-2025-53864、CVE-2025-8916 |
| oauth2-oidc-sdk | 9.35 | 11.33 | 兼容 nimbus-jose-jwt 10.x |

## 规避的 CVE

| CVE 编号 | 说明 |
|----------|------|
| CVE-2023-52428 | nimbus-jose-jwt 拒绝服务漏洞 |
| CVE-2025-53864 | nimbus-jose-jwt 安全漏洞 |
| CVE-2025-8916 | nimbus-jose-jwt 安全漏洞 |

## 变更文件清单

### 依赖配置

| 文件 | 变更说明 |
|------|---------|
| `dependencies/spring-security-dependencies.gradle` | nimbus-jose-jwt 9.22→10.8，oauth2-oidc-sdk 9.35→11.33 |
| `build.gradle` | 移除 nimbus-jose-jwt 版本锁定拒绝规则（原规则要求 nimbus-jose-jwt 随 oauth2-oidc-sdk 同步更新，现已统一升级） |

### 测试适配

nimbus-jose-jwt 10.x 和 oauth2-oidc-sdk 11.x 存在以下不兼容变更，需适配测试代码：

| 文件 | 变更说明 | 不兼容原因 |
|------|---------|-----------|
| `oauth2/oauth2-core/src/test/java/.../ClaimConversionServiceTests.java` | 替换 `com.nimbusds.jose.shaded.json.JSONArray/JSONObject` 为 `ArrayList/HashMap` | nimbus 10.x 移除了 shaded json-smart，改用 shaded Gson |
| `oauth2/oauth2-core/src/test/java/.../ClaimTypeConverterTests.java` | 同上 | 同上 |
| `oauth2/oauth2-jose/src/test/java/.../NimbusReactiveJwtDecoderTests.java` | 移除 `UnknownHostException` 堆栈断言 | nimbus 10.x 改变了异常链封装方式 |
| `oauth2/oauth2-client/src/test/java/.../ClientRegistrationsTests.java` | 移除错误消息精确匹配断言 | oauth2-oidc-sdk 11.x 在配置解析阶段提前校验 jwks_uri，错误消息有变化 |

## 验证结果

使用 Java 11（Amazon Corretto 11.0.29）构建和测试：

| 模块 | 测试结果 |
|------|---------|
| spring-security-oauth2-core | 全部通过 |
| spring-security-oauth2-jose | 243 tests，全部通过 |
| spring-security-oauth2-client | 1237 tests，全部通过 |
| spring-security-oauth2-resource-server | 全部通过 |

## 注意事项

- 主代码（`src/main`）无任何修改，所有变更仅涉及依赖声明和测试代码
- nimbus-jose-jwt 10.x 为大版本升级，内部从 shaded json-smart 迁移到 shaded Gson，但对外 API 保持兼容
- oauth2-oidc-sdk 11.x 为大版本升级，部分错误消息有变化，但核心 API 保持兼容
