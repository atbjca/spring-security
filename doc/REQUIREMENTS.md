# Spring Security 5.8.16 维护分支需求文档

## 任务主题

Spring Security 5.8.16 维护分支——CVE 漏洞修复（CVE-2025-22228、CVE-2025-22234、CVE-2025-22233、CVE-2025-41249）、Nexus 私服发布配置、nimbus-jose-jwt 依赖升级

## 分支信息

| 属性 | 值 |
|------|---|
| **基础版本** | Spring Security 5.8.16（origin/5.8.x） |
| **工作分支** | `5.8.x-bjca-patch` |
| **版本号** | `5.8.16-bjca-patch-SNAPSHOT` |
| **Group** | `libiao.test.org.springframework.security` |

## CVE 漏洞修复清单

| CVE 编号 | CVSS | 影响组件 | 处置结果 | 说明 |
|----------|------|---------|---------|------|
| CVE-2025-22228 | 7.4 HIGH | Spring Security (BCrypt) | **已修复** | BCrypt 密码 72 字节长度限制绕过，在 `BCrypt.hashpw()` 中添加条件检查 |
| CVE-2025-22234 | 5.3 MEDIUM | Spring Security (BCrypt) | **已覆盖** | CVE-2025-22228 的回归修复，采用合并修复策略一步到位，从未暴露于此漏洞 |
| CVE-2025-22233 | 3.1 LOW | Spring Framework (DataBinder) | **不适用** | 漏洞位于 Spring Framework spring-context 模块，非 Spring Security 代码范围 |
| CVE-2025-41249 | 7.5 HIGH | Spring Framework (AnnotationsScanner) | **不适用** | 漏洞位于 Spring Framework spring-core 模块，配套 CVE-2025-41248 仅影响 6.x |

## 依赖升级

| 依赖 | 升级前 | 升级后 | 修复的 CVE |
|------|--------|--------|-----------|
| nimbus-jose-jwt | 9.24.4 | 10.8 | CVE-2023-52428、CVE-2025-53864 |
| oauth2-oidc-sdk | 9.43.3 | 11.33 | （随 nimbus-jose-jwt 同步升级） |

> 注：CVE-2025-8916 需升级 Bouncy Castle 才能修复，不在本次范围内。

## Nexus 私服发布配置

- `settings.gradle`：pluginManagement 仓库配置
- `build.gradle`：allprojects 依赖仓库 + `plugins.withType(MavenPublishPlugin)` 发布仓库
- `gradle.properties`：`projectGroup` 属性
- `Makefile`：clean / build-thin / test / install / deploy / stop / docs 命令
- 详见 `doc/NEXUS_DEPLOY.md`

## Git Commit 历史

| Commit | 内容 |
|--------|------|
| `0045d902cb` | 配置 Nexus 私服发布与项目初始化（Phase 1 & 2） |
| `c5654eff5f` | 升级 nimbus-jose-jwt 至 10.8 / oauth2-oidc-sdk 至 11.33（Phase 3） |
| `9ca4c02192` | 修复 CVE-2025-22228 BCrypt 密码长度限制绕过，合并修复 CVE-2025-22234（Phase 4） |
| `720d65f610` | 记录 CVE-2025-22234 文档（Phase 5） |
| `087f5d59dc` | 记录 CVE-2025-22233 文档——不适用（Phase 6） |
| `6352d81cbe` | 记录 CVE-2025-41249 文档——不适用（Phase 7） |

## 文档清单

| 文件 | 说明 |
|------|------|
| `doc/REQUIREMENTS.md` | 本文档——需求与完成情况总览 |
| `doc/NEXUS_DEPLOY.md` | Nexus 私服配置方案与使用说明 |
| `doc/NIMBUS_UPGRADE.md` | nimbus-jose-jwt 依赖升级记录 |
| `doc/CVE/CVE-2025-22228.md` | BCrypt 密码长度限制绕过——已修复 |
| `doc/CVE/CVE-2025-22234.md` | BCrypt 时序攻击回归——已在合并修复中覆盖 |
| `doc/CVE/CVE-2025-22233.md` | DataBinder disallowedFields 绕过——不适用 |
| `doc/CVE/CVE-2025-41249.md` | 注解扫描泛型授权绕过——不适用 |
| `doc/CVE/CVE-2023-52428.md` | nimbus-jose-jwt 算法混淆——已通过升级修复 |
| `doc/CVE/CVE-2025-53864.md` | nimbus-jose-jwt 漏洞——已通过升级修复 |
