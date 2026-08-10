# Bouncy Castle 2026 安全公告评估

## 基线与处置

- **升级前：** Bouncy Castle jdk18on 1.80.2
- **升级后：** Bouncy Castle jdk18on 1.84
- **组件：** `bcpkix-jdk18on`、`bcprov-jdk18on`、传递的 `bcutil-jdk18on`

Spring Security Crypto 将 BCPKIX 作为可选依赖，OAuth2 JOSE 测试使用 BCPKIX 和 BCProv。仓库未发现以下漏洞 API 的直接生产代码调用，但依赖平台和可选运行时基线仍需升级，以保护启用对应能力的下游消费者。

## CVE-2026-5588

- **严重性：** MODERATE
- **影响：** PKIX draft `CompositeVerifier` 可把空签名序列接受为有效签名。
- **受影响版本：** BCPKIX 1.49 至 1.83
- **修复版本：** 1.84
- **项目触发性：** 当前仓库未发现复合签名验证调用；属于条件性下游风险。

## CVE-2026-0636

- **严重性：** MODERATE
- **影响：** BCProv `LDAPStoreHelper` 对 LDAP 查询特殊字符处理不当，可能发生 LDAP 注入。
- **受影响版本：** BCProv 1.74 至 1.83
- **修复版本：** 1.84
- **项目触发性：** 当前仓库未调用 `LDAPStoreHelper`；属于条件性下游风险。

## 验证

- Crypto optional classpath 选择 `bcpkix-jdk18on:1.84`、`bcutil-jdk18on:1.84`、`bcprov-jdk18on:1.84`。
- OAuth2 JOSE 的 jdk18on Bouncy Castle 测试依赖统一选择 1.84。
- `:bjca-footstone-bpring-security-crypto:test` 与
  `:bjca-footstone-bpring-security-oauth2-jose:test` 在 Bouncy Castle 1.84 下通过；
  Gradle 输出为 `BUILD SUCCESSFUL`（21 actionable tasks）。

> 构建或测试工具通过其他旧坐标引入的 legacy Bouncy Castle 不属于本次发布/可选运行时基线变更，后续随构建依赖治理单独处理。
