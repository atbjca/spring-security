# Spring Security GAV 映射表 (GAV_MAPPING.md)

本表记录了原始 Spring Security 模块与重构后的内部版本坐标映射关系。

## 1. 全局变更规则
- **GroupId**: `org.springframework.security` -> `cn.bjca.footstone.bpring.security`
- **ArtifactId**: `spring-security-xxx` -> `bjca-footstone-bpring-security-xxx`
- **Version**: `5.8.16` -> `5.8.16-nes.patch.1`

## 2. 详细模块映射

| 原始 ArtifactId | 新 ArtifactId | 备注 |
| :--- | :--- | :--- |
| spring-security-core | bjca-footstone-bpring-security-core | 核心认证授权逻辑 |
| spring-security-web | bjca-footstone-bpring-security-web | Web 安全 Filter 支持 |
| spring-security-config | bjca-footstone-bpring-security-config | XML/Java 配置支持 |
| spring-security-oauth2-client | bjca-footstone-bpring-security-oauth2-client | OAuth2 客户端功能 |
| spring-security-oauth2-core | bjca-footstone-bpring-security-oauth2-core | OAuth2 核心协议支持 |
| spring-security-oauth2-jose | bjca-footstone-bpring-security-oauth2-jose | JOSE (JWT/JWE) 支持 |
| spring-security-oauth2-resource-server | bjca-footstone-bpring-security-oauth2-resource-server | 资源服务器支持 |
| spring-security-acl | bjca-footstone-bpring-security-acl | ACL 权限控制 |
| spring-security-cas | bjca-footstone-bpring-security-cas | CAS 对接 |
| spring-security-ldap | bjca-footstone-bpring-security-ldap | LDAP 对接 |
| ~~spring-security-remoting~~ | ~~bjca-footstone-bpring-security-remoting~~ | ~~远程调用安全~~ |
| spring-security-saml2-service-provider | bjca-footstone-bpring-security-saml2-service-provider | SAML2 支持 |
| spring-security-test | bjca-footstone-bpring-security-test | 测试框架支持 |
| spring-security-bom | bjca-footstone-bpring-security-bom | Maven BOM 文件 |
| spring-security-dependencies | bjca-footstone-bpring-security-dependencies | 内部依赖管理 |

## 3. Spring Framework 依赖重定向建议

在下游项目的 `dependencyManagement` 中，请执行以下替换：

| 原始坐标 | 推荐新坐标 |
| :--- | :--- |
| org.springframework:spring-framework-bom:5.3.39 | cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom:5.3.39-nes.patch.1 |
| org.springframework:spring-xxx:5.3.39 | cn.bjca.footstone.bpring:bjca-footstone-bpring-xxx:5.3.39-nes.patch.1 |

> [!TIP]
> 使用新坐标后，原有 Java 代码中的 `import org.springframework.security...` 无需任何修改即可直接运行。
