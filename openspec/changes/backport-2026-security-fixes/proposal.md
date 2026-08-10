## Why

当前内部版本 `5.8.16-nes.patch.1` 缺少 2026 年影响 Spring Security 5.8 的七项上游安全修复，覆盖 HTTP 安全响应头、认证时序、SAML、请求缓存和 X.509 认证。继续按当前基线发布会使通用安全库在多个可选模块中保留已知漏洞，因此需要一次完整、可审计的 5.8.27 等价安全回移。

## What Changes

- 回移并验证 `CVE-2026-22732`、`CVE-2026-22746`、`CVE-2026-40988`、`CVE-2026-41003`、`CVE-2026-41694`、`CVE-2026-41706` 和 `CVE-2026-47838`
- 保持 Java 8、OpenSAML 3 与当前 5.8 公共 API/二进制兼容性，不直接复制依赖 Java 17 或新主版本 API 的实现
- 为每项 CVE 增加触发条件、失败行为和修复行为的回归测试，并记录上游提交与本地回移提交的对应关系
- 将 `CVE-2026-22748` 明确记录为仅影响 6.3+ 的不适用项，防止 NVD CPE 宽泛匹配造成误回移
- 形成一个可供发布门禁校验的“5.8.27 等价安全基线”结果

## Capabilities

### New Capabilities

- `spring-security-2026-cve-backports`: 定义七项 2026 Spring Security 5.8 CVE 的回移范围、兼容性约束、回归验证和处置证据

### Modified Capabilities

无。

## Impact

- 受影响模块：`core`、`web`、`saml2-service-provider` 及其测试
- 受影响认证路径：`DaoAuthenticationProvider`、SAML 登录/登出、`CookieRequestCache`、X.509 principal extraction
- 受影响发布基线：所有 `5.8.16-nes.patch.1` 内部 Spring Security 构件
- 兼容性约束：Java 8、Servlet API 4、OpenSAML 3.4.6，以及现有公开 API 和序列化行为

