# Spring Security GAV 映射表

## 全局变更规则

- **GroupId**: `org.springframework.security` → `cn.bjca.footstone.bpring.security`
- **ArtifactId**: `spring-security-xxx` → `bjca-footstone-bpring-security-xxx`
- **Version**: `6.5.11` → `6.5.11-nes.patch.1`

## 模块映射

| 原始 ArtifactId | 新 ArtifactId |
| :--- | :--- |
| spring-security-core | bjca-footstone-bpring-security-core |
| spring-security-acl | bjca-footstone-bpring-security-acl |
| spring-security-aspects | bjca-footstone-bpring-security-aspects |
| spring-security-cas | bjca-footstone-bpring-security-cas |
| spring-security-web | bjca-footstone-bpring-security-web |
| spring-security-config | bjca-footstone-bpring-security-config |
| spring-security-crypto | bjca-footstone-bpring-security-crypto |
| spring-security-data | bjca-footstone-bpring-security-data |
| spring-security-ldap | bjca-footstone-bpring-security-ldap |
| spring-security-messaging | bjca-footstone-bpring-security-messaging |
| spring-security-oauth2-client | bjca-footstone-bpring-security-oauth2-client |
| spring-security-oauth2-core | bjca-footstone-bpring-security-oauth2-core |
| spring-security-oauth2-jose | bjca-footstone-bpring-security-oauth2-jose |
| spring-security-oauth2-resource-server | bjca-footstone-bpring-security-oauth2-resource-server |
| spring-security-rsocket | bjca-footstone-bpring-security-rsocket |
| spring-security-saml2-service-provider | bjca-footstone-bpring-security-saml2-service-provider |
| spring-security-taglibs | bjca-footstone-bpring-security-taglibs |
| spring-security-test | bjca-footstone-bpring-security-test |
| spring-security-bom | bjca-footstone-bpring-security-bom |

以上 19 个坐标已由本地 `publishToMavenLocal` 生成的 publication/POM 清单复核；
当前没有额外 publication 或显式排除项。

## Spring Framework 依赖重定向

| 原始坐标 | 新坐标 |
| :--- | :--- |
| org.springframework:spring-framework-bom:6.2.19 | cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom:6.2.19-nes.patch.1 |
| org.springframework:spring-xxx | cn.bjca.footstone.bpring:bjca-footstone-bpring-xxx |

> 下游 Java 代码中的 `import org.springframework.security...` 无需修改。

## RELEASE 仓库与验证

- Nexus 主机：`192.168.131.36:8088`
- 仓库：`releases`
- 路径：`/repository/releases/`
- Gradle 属性：`nexusReleaseUrl`
- 显式排除：无

依赖顺序为 Spring Framework `6.2.19-nes.patch.1` RELEASE 在前，Spring
Security `6.5.11-nes.patch.1` 在后。发布前复用已批准的开发 build/test 证据，
无 `clean` 地执行 `publishToMavenLocal -x test`，扫描全部生成 POM/BOM 中的
内部 `-SNAPSHOT`，并完成 RELEASE-only 的代表性消费验证。
