## Context

Spring Security 5.8 的本地构建同时使用版本目录、dependency platform 和根级 `resolutionStrategy`。只有进入发布 POM/BOM/Gradle Module Metadata 的规则才能约束下游。SAML publication 还必须解决 OpenSAML 3/4 混合打包造成的 Java 8 不兼容。

## Goals / Non-Goals

**Goals:**

- 发布消费者解析到明确的 BC、OpenSAML 3 传递依赖和 Xerces 安全基线
- Java 8 主 artifact 不含 major 55 class，也不含 OpenSAML 4 实现
- `make deploy` 在上传前验证实际候选构件，而不是只验证仓库编译 classpath
- 保存足以复核版本、Java 8 运行和候选构件身份的最小证据

**Non-Goals:**

- 不要求用 Java 8 执行 Gradle 主构建或 `make deploy`
- 不引入漏洞扫描器、VEX/SBOM 系统或部署后远端构件验证
- 不处理 Jackson，不维护 Spring LDAP 或 Logback 的源码 fork
- 不发布 OpenSAML 4

## Decisions

### 1. 消费者安全规则必须进入发布元数据

BC 1.84、OpenSAML 3 的可修复传递依赖和 Xerces 2.12.2 由版本目录、publishable dependency platform、BOM 和模块依赖共同表达。根级 `resolutionStrategy` 仅保留为仓库自检兜底。

### 2. SAML Java 8 artifact 只发布 OpenSAML 3

主 JAR、sources JAR 和 Javadoc 只合并 `opensaml3Main`。`opensaml4Main` 仍可由当前 JDK 编译和测试，但不会进入本次 publication。通用 `checkJava8Artifact` 扫描所有 Spring module 主 JAR，SAML 再额外断言不存在 OpenSAML 4 class。

### 3. 遗留 SAML 依赖直接从发布图移除

OpenSAML 3.4.6 保持不变。Velocity 1.7、Commons Lang 2.x 和 BC `jdk15on` 从三个公开 OpenSAML 入口依赖中排除；Commons Collections、Guava、XMLSec、Woodstox 和 BC `jdk18on` 作为显式 runtime dependencies 发布。现有 SAML 回归测试和独立 consumer 负责证明该精简图仍可运行。

### 4. OpenID 保持发布并固定 Xerces

本 change 不改变已有模块坐标或下线 OpenID。`xercesImpl` 2.12.2 作为显式运行时依赖进入 POM 和 consumer 图，避免 NekoHTML 路径回落到 2.11.0。

### 5. Spring LDAP 采用运行时边界结论

当前公共和已配置仓库无法取得 Spring LDAP 2.4.5，且在本仓库内维护完整 fork 超出范围。Spring Security 的 `BindAuthenticator` 和 `AbstractLdapAuthenticationProvider` 均在调用 Spring LDAP bind 前拒绝 null/空密码，并有回归测试。因此继续发布 2.4.4，同时明确这不是对上游 Spring LDAP 本身已修复的声明。

### 6. Logback 不属于公共运行时约束

Logback 只服务于仓库测试。删除公共 platform/BOM 中的 Logback constraint，不让独立 Logback fork 的状态无条件阻塞 Spring Security publication。

### 7. 候选发布门禁在 deploy 前执行

`make deploy` 依赖 `verify-published-security`。门禁先发布选定模块到 `build/publications/repos`，再使用真实 Java 8 运行最小 Maven 和 Gradle consumer。两者都断言关键版本、禁止依赖和代表性类加载/加解密行为，并在 `build/reports/published-security` 保存解析树、Java 身份、处置说明和 SHA-256。

## Risks / Trade-offs

- OpenSAML 3 的传递依赖升级可能破坏运行时兼容：由 SAML 回归测试和双 consumer smoke 覆盖
- OpenSAML 4 暂不进入主 JAR：保持 Java 8 承诺优先，未来如需发布应使用明确的 Java 11+ artifact
- Spring LDAP 2.4.4 仍会被版本扫描器命中：证据只证明 Spring Security 入口不可达空密码 bind，不声称上游库已修复
- 本地候选验证不能证明 Nexus 上传过程未改变字节：本 change 的目标是阻止未经验证的候选构件进入 deploy；部署后验证可单独增加

## Verification

1. 生成并检查 platform、BOM、Crypto、SAML、LDAP 和 OpenID 的 POM/Module Metadata。
2. 发布任务自动运行 Java 8 class-file gate；SAML gate 同时检查 OpenSAML 4 不存在。
3. 运行相关 Crypto、SAML、LDAP 和 OAuth2 JOSE 回归测试。
4. `make verify-published-security` 在真实 Java 8 上运行 Maven/Gradle consumers。
5. 检查 `build/reports/published-security` 中的依赖树、JVM 身份、smoke 结果和候选 SHA-256。
