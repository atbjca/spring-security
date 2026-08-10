# Jackson 2026 安全公告评估

## 评估范围

- **项目基线：** Spring Security 6.5.11-nes.patch.2-SNAPSHOT，维护分支 `6.5.x-bjca-patch`
- **升级前：** `com.fasterxml.jackson:jackson-bom:2.18.8`
- **升级后：** `com.fasterxml.jackson:jackson-bom:2.18.9`
- **处置原则：** 通过 BOM 统一对齐 Jackson core、annotations、databind、datatype 和 dataformat，不单独覆盖 `jackson-databind`

本评估针对 Jackson 2.18.8 命中的两个 CVE 以及一个尚未分配 CVE 编号的 JsonView 外部属性公告。2.21.5 不作为本次目标版本，因为跨越多个 minor 维护线会扩大 6.5.x 的兼容性回归面；后续如需采用更新的 Jackson minor 线，应单独评估。

## CVE-2026-59889

- **组件：** Jackson Databind
- **严重性：** HIGH
- **问题类型：** 特定多态/外部类型属性反序列化场景下的授权绕过
- **受影响基线：** Jackson 2.18.8
- **修复基线：** Jackson 2.18.9

### 本项目影响

Spring Security 代码和测试中使用 Jackson 主要用于安全对象的显式 mixin/module 注册、OAuth2/OIDC 数据模型以及 WebAuthn、LDAP、CAS、SAML 集成。仓库未发现将不可信 JSON 直接绑定到应用自定义多态类型并依赖外部类型属性授权的公共实现。项目平台仍可能被下游应用导入，因此旧 BOM 会把风险传递给消费者。

### 处置结论

**已修复。** 依赖平台升级至 Jackson BOM 2.18.9；不增加独立 Databind 覆盖，确保所有 Jackson 模块版本一致。下游应用若自行覆盖 Jackson 版本，仍需检查最终运行时依赖树。

## CVE-2026-54515

- **组件：** Jackson Databind
- **严重性：** HIGH
- **问题类型：** 特定 JsonView/外部属性组合导致的反序列化访问控制绕过
- **受影响基线：** Jackson 2.18.8
- **修复基线：** Jackson 2.18.9

### 本项目影响

Spring Security 的 Jackson 支持以安全对象 mixin 和模块为主，没有发现使用该公告所描述的应用自定义 JsonView 外部属性绑定路径。若下游服务使用受影响配置并从网络接收 JSON，则风险取决于其自身 ObjectMapper 配置，不能仅由 Spring Security API 使用方式排除。

### 处置结论

**已修复。** 统一 Jackson BOM 2.18.9，保留 2.18.x 维护线。

## JsonView external-property advisory（CVE 未分配）

- **组件：** Jackson Databind
- **公告状态：** 已有上游安全公告/GHSA 记录，但本次评估时尚未获得已分配的 CVE 编号
- **受影响基线：** Jackson 2.18.8
- **修复基线：** Jackson 2.18.9

该条目与 JsonView 和外部属性处理组合有关。由于尚无正式 CVE 编号，项目只按上游公告记录，不人为创建或推断 CVE 编号。

### 处置结论

**基线已覆盖。** Jackson 2.18.9 随 BOM 统一升级；应用自定义 JsonView/`@JsonUnwrapped`/外部类型配置仍应由应用自身进行专项审计。

## 验证要求与证据

1. 依赖平台约束显示 `jackson-bom:2.18.9`；在 web 的 optional 配置中，WebAuthn4J 的更高约束使有效 Jackson core/annotations/databind/dataformat 版本解析为 2.20.0。两者均满足“2.18.9 或更高”的安全基线，且不再选择 2.18.8。
2. core、web、OAuth2 client、LDAP、CAS 和 SAML 中的 Jackson/mixin 测试应通过。
3. 最终 diff 不应包含单独的 `jackson-databind` 版本覆盖或公共 API/序列化格式改动。

## 参考

- Jackson BOM 版本变更：`gradle/libs.versions.toml`
- 项目需求与 CVE 台账：`doc/REQUIREMENTS.md`
- Jackson 官方安全公告与对应 GHSA/NVD 条目（以发布时的公开编号为准）
