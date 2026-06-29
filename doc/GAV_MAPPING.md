# Spring Security GAV 映射表

## 全局变更规则

- **GroupId**: `org.springframework.security` → `cn.bjca.footstone.bpring.security`
- **ArtifactId**: `spring-security-xxx` → `bjca-footstone-bpring-security-xxx`
- **Version**: `6.5.11` → `6.5.11-nes.patch.1-SNAPSHOT`

## 模块映射

| 原始 ArtifactId | 新 ArtifactId |
| :--- | :--- |
| spring-security-core | bjca-footstone-bpring-security-core |
| spring-security-web | bjca-footstone-bpring-security-web |
| spring-security-config | bjca-footstone-bpring-security-config |
| spring-security-crypto | bjca-footstone-bpring-security-crypto |
| spring-security-oauth2-client | bjca-footstone-bpring-security-oauth2-client |
| spring-security-oauth2-core | bjca-footstone-bpring-security-oauth2-core |
| spring-security-oauth2-jose | bjca-footstone-bpring-security-oauth2-jose |
| spring-security-oauth2-resource-server | bjca-footstone-bpring-security-oauth2-resource-server |
| spring-security-saml2-service-provider | bjca-footstone-bpring-security-saml2-service-provider |
| spring-security-javascript | bjca-footstone-bpring-security-javascript |
| spring-security-test | bjca-footstone-bpring-security-test |
| spring-security-bom | bjca-footstone-bpring-security-bom |
| spring-security-dependencies | bjca-footstone-bpring-security-dependencies |

## Spring Framework 依赖重定向

| 原始坐标 | 新坐标 |
| :--- | :--- |
| org.springframework:spring-framework-bom:6.2.19 | cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom:6.2.19-nes.patch.1-SNAPSHOT |
| org.springframework:spring-xxx | cn.bjca.footstone.bpring:bjca-footstone-bpring-xxx |

> 下游 Java 代码中的 `import org.springframework.security...` 无需修改。
