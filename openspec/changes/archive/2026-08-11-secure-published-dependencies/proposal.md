## Why

仓库内的 `resolutionStrategy` 能让本地测试使用较新的依赖，但不会自动进入 Maven POM、BOM 或 Gradle Module Metadata。下游消费者仍可能解析到 OpenSAML 3 的旧传递依赖、Bouncy Castle `jdk15on` 或 OpenID 的 Xerces 2.11.0。

此外，SAML 主 JAR 曾同时打入面向 OpenSAML 3 的 Java 8 class 和面向 OpenSAML 4 的 Java 11 class。`make deploy` 可以由 JDK 11+ 执行，但其发布结果必须能在真实 Java 8 JVM 中加载和运行。

## What Changes

- 将 Bouncy Castle 发布基线统一到 1.84 `jdk18on`，阻止 `jdk15on` 进入消费者依赖图
- 在发布元数据中明确 OpenSAML 3.4.6 所需的 Commons Collections 3.2.2、Guava 32.0.1-jre、XMLSec 2.2.6 和 Woodstox 5.4.0
- 从 SAML 依赖图排除不再需要的 Velocity 1.7 和 Commons Lang 2.x
- 保留现有 OpenID publication，并将其 Xerces 运行时固定为 2.12.2
- 从公共 platform/BOM 移除仅供仓库测试使用的 Logback 约束
- Java 8 SAML 主 JAR、sources JAR 和 Javadoc 只包含 OpenSAML 3 实现；OpenSAML 4 本次不发布
- 所有声明支持 Java 8 的 Spring Security 主 JAR 在发布前检查 class-file major version 不高于 52
- `make deploy` 前发布到隔离候选仓库，用真实 Java 8 分别运行 Maven 和 Gradle consumer，并保存依赖树、JVM 身份和候选构件 SHA-256
- Spring LDAP 继续使用当前可获得的 2.4.4；记录 Spring Security 认证入口在 LDAP bind 前拒绝空密码，因此 CVE-2026-41720 的上游漏洞路径不进入本项目认证运行时

## Capabilities

### New Capabilities

- `published-dependency-security`: 定义消费者可见的依赖基线、Java 8 发布边界和候选构件验证门禁

### Modified Capabilities

- `bouncycastle-upgrade`: 将消费者可见的最低版本从 1.79 提升到 1.84，并限定为 `jdk18on`

## Impact

- 构建与发布：`Makefile`、dependency platform、BOM、版本目录和公共发布元数据
- 模块：SAML、Crypto、OpenID，以及由通用发布插件覆盖的 Java 8 主 JAR
- 验证：新增最小 Maven/Gradle consumer 和 `make verify-published-security`
- 构建 JDK：保持 JDK 11+；Java 8 只作为发布结果的运行兼容目标

## Out Of Scope

- Jackson 版本或反序列化加固
- SBOM/VEX 平台、全仓库漏洞扫描和治理流程建设
- 创建或维护 Spring LDAP fork
- 发布 OpenSAML 4 或设计新的 Java 11 专用坐标
- `make deploy` 后从 Nexus 再下载同坐标构件；本 change 只阻止未经本地候选验证的部署
