# Spring Security 5.8.16 维护分支需求文档

## 任务主题

Spring Security 5.8.16 维护分支——CVE 漏洞修复（CVE-2025-22228、CVE-2025-22234、CVE-2025-22233、CVE-2025-41249、CVE-2024-22257、CVE-2024-22258、CVE-2024-38827、CVE-2024-38821）、Nexus 私服发布配置、nimbus-jose-jwt 依赖升级

## 分支信息

| 属性 | 值 |
|------|---|
| **基础版本** | Spring Security 5.8.16（origin/5.8.x） |
| **工作分支** | `5.8.x-bjca-patch` |
| **当前开发版本** | `5.8.16-nes.patch.2-SNAPSHOT` |
| **上一正式版本** | `5.8.16-nes.patch.1`（`v5.8.16-nes.patch.1`） |
| **Group** | `cn.bjca.footstone.bpring.security` |

## CVE 漏洞修复清单

| CVE 编号 | CVSS | 影响组件 | 处置结果 | 说明 |
|----------|------|---------|---------|------|
| CVE-2025-22228 | 7.4 HIGH | Spring Security (BCrypt) | **已修复** | BCrypt 密码 72 字节长度限制绕过，在 `BCrypt.hashpw()` 中添加条件检查 |
| CVE-2025-22234 | 5.3 MEDIUM | Spring Security (BCrypt) | **已覆盖** | CVE-2025-22228 的回归修复，采用合并修复策略一步到位，从未暴露于此漏洞 |
| CVE-2025-22233 | 3.1 LOW | Spring Framework (DataBinder) | **不适用** | 漏洞位于 Spring Framework spring-context 模块，非 Spring Security 代码范围 |
| CVE-2025-41249 | 7.5 HIGH | Spring Framework (AnnotationsScanner) | **不适用** | 漏洞位于 Spring Framework spring-core 模块，配套 CVE-2025-41248 仅影响 6.x |
| CVE-2024-22257 | 8.2 HIGH | Spring Security (AuthenticatedVoter) | **已在基线修复** | 空 Authentication 绕过，5.8.11 已修，基线 5.8.16 已包含修复 |
| CVE-2024-22258 | 6.1 MEDIUM | Spring Authorization Server (PKCE) | **不适用** | 漏洞位于 Spring Authorization Server，非 Spring Security 核心代码范围 |
| CVE-2024-38827 | 4.8 MEDIUM | Spring Security (Locale 大小写) | **已在基线修复** | Locale 相关 toLowerCase/toUpperCase 绕过，5.8.16 已修，基线已包含修复 |
| CVE-2024-38821 | 9.3 CRITICAL | Spring Security (WebFlux 防火墙) | **已在基线修复** | WebFlux 静态资源 URL 规范化绕过，5.8.15 已修，基线 5.8.16 已包含修复 |

## 依赖升级

| 依赖 | 升级前 | 升级后 | 修复的 CVE |
|------|--------|--------|-----------|
| nimbus-jose-jwt | 9.24.4 | 10.8 | CVE-2023-52428、CVE-2025-53864 |
| oauth2-oidc-sdk | 9.43.3 | 11.33 | （随 nimbus-jose-jwt 同步升级） |
| Bouncy Castle | 1.70 | 1.84 | CVE-2025-8916 |

## 2026 Spring Security 安全回补

当前 `patch.2-SNAPSHOT` 还包含七个适用于 Spring Security 5.8 的 2026 安全回补：

- CVE-2026-22732、CVE-2026-22746
- CVE-2026-40988、CVE-2026-41003、CVE-2026-41694
- CVE-2026-41706、CVE-2026-47838

各漏洞的官方范围、上游提交、本地提交、受影响模块和 Java 8 回归证据统一记录在
`doc/CVE/CVE-2026-security-backports.md`。CVE-2026-22748 经官方版本范围核对，不影响 5.8 基线。

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
| `1fe117b5ca` | 建立消费者可见的安全依赖与 Java 8 发布边界 |
| `558f26596d` | 将维护分支推进到 `5.8.16-nes.patch.2-SNAPSHOT` |
| `9c5e51ee66` | 完成并归档 patch.2 SNAPSHOT 准备变更 |

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
| `doc/CVE/CVE-2024-22257.md` | AuthenticatedVoter 空认证绕过——已在基线修复 |
| `doc/CVE/CVE-2024-22258.md` | Spring Authorization Server PKCE 降级攻击——不适用 |
| `doc/CVE/CVE-2024-38827.md` | Locale 大小写转换授权绕过——已在基线修复 |
| `doc/CVE/CVE-2024-38821.md` | WebFlux 静态资源授权绕过——已在基线修复 |
| `doc/CVE/CVE-2025-8916.md` | Bouncy Castle 证书名称约束 DoS——已通过 1.84 修复 |
| `doc/CVE/CVE-2026-security-backports.md` | 七个 2026 Spring Security 回补及 Java 8 验证证据 |

---

## GAV 重构——SCA 规避坐标重命名（已完成）

> 以下记录本次 GAV 重构的全部修改，共涉及 **36 个文件**，**341 行新增 / 363 行删除**。

### 1. 背景与目标

由于官方 Spring Security 5.8.x 已停止常规维护，企业级 SCA 扫描工具会根据 GAV (GroupId, ArtifactId, Version) 特征报出已知 CVE。为了在长期维护补丁版本的同时规避工具误报，需要对项目坐标进行"去特征化"重命名。

### 2. 核心约束

- **严禁修改 Package/Class**：不修改 Java 源代码中的 `package` 声明、类名、变量名或 `import` 语句。
- **严禁字节码转换**：不使用 shade-plugin relocation 等字节码重写工具，下游业务代码切换 POM 即可无缝迁移。
- **功能完整性**：保证 Filter 链、注解驱动及核心认证授权机制在重命名后正常工作。

### 3. 坐标变更总览

| 维度 | 变更前 | 变更后 |
|------|--------|--------|
| **GroupId** | `libiao.test.org.springframework.security` | `cn.bjca.footstone.bpring.security` |
| **ArtifactId 前缀** | `spring-security-` | `bjca-footstone-bpring-security-` |
| **Version** | `5.8.16-bjca-patch-SNAPSHOT` | `5.8.16-nes.patch.2-SNAPSHOT`（上一正式版为 `5.8.16-nes.patch.1`） |
| **Spring Framework BOM** | `org.springframework:spring-framework-bom:5.3.39` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom:5.3.39-nes.patch.1` |

### 4. 文件修改明细

#### 4.1 构建基础设施（4 个文件）

| 文件 | 修改内容 |
|------|---------|
| `gradle.properties` | `projectGroup` 为 `cn.bjca.footstone.bpring.security`；当前开发 `version` 为 `5.8.16-nes.patch.2-SNAPSHOT` |
| `gradle/libs.versions.toml` | `org-springframework` 版本为 `5.3.39-nes.patch.1`；`spring-framework-bom` 坐标为 `cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom` |
| `settings.gradle` | 在 `buildFiles.each` 循环中增加动态重命名逻辑——所有 `spring-security-*` 项目名自动替换为 `bjca-footstone-bpring-security-*`，物理目录结构保持不变 |
| `build.gradle` | 新增禁用 `remoting` 模块的配置（内部 Spring Framework fork 不含 `httpinvoker` 包）；`nohttp` 插件引用的项目名跟随重命名 |

#### 4.2 Makefile（1 个文件）

| 文件 | 修改内容 |
|------|---------|
| `Makefile` | `build-thin` 目标增加 `-x integrationTest -x checkstyleNohttp`；docs 模块引用改为 `:bjca-footstone-bpring-security-docs` |

#### 4.3 依赖管理（1 个文件）

| 文件 | 修改内容 |
|------|---------|
| `dependencies/spring-security-dependencies.gradle` | `spring-framework-bom` 坐标替换为 `cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom` |

#### 4.4 Java 源代码（1 个文件，仅常量/返回值，未改 package）

| 文件 | 修改内容 |
|------|---------|
| `core/.../SpringSecurityCoreVersion.java` | `getVersion()` 硬编码返回 `"5.8.16"`（运行时版本伪装，维持 Spring Boot 兼容）；`getSpringVersion()` 中 properties key 改为 `cn.bjca.footstone.bpring:bjca-footstone-bpring-core` |

#### 4.5 子模块 .gradle 文件（28 个文件）

所有子模块的 `.gradle` 文件执行了两类替换：

**A. 内部项目引用**（`project(':spring-security-xxx')` → `project(':bjca-footstone-bpring-security-xxx')`）

**B. Spring Framework 依赖坐标**（`org.springframework:spring-xxx` → `cn.bjca.footstone.bpring:bjca-footstone-bpring-xxx`）

涉及的 18 个 Spring Framework 模块：

| 原始坐标 | 新坐标 |
|----------|--------|
| `org.springframework:spring-aop` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-aop` |
| `org.springframework:spring-beans` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-beans` |
| `org.springframework:spring-context` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-context` |
| `org.springframework:spring-context-support` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-context-support` |
| `org.springframework:spring-core` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-core` |
| `org.springframework:spring-expression` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-expression` |
| `org.springframework:spring-jcl` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-jcl` |
| `org.springframework:spring-jdbc` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-jdbc` |
| `org.springframework:spring-messaging` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-messaging` |
| `org.springframework:spring-orm` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-orm` |
| `org.springframework:spring-r2dbc` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-r2dbc` |
| `org.springframework:spring-test` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-test` |
| `org.springframework:spring-tx` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-tx` |
| `org.springframework:spring-web` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-web` |
| `org.springframework:spring-webflux` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-webflux` |
| `org.springframework:spring-webmvc` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-webmvc` |
| `org.springframework:spring-websocket` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-websocket` |
| `org.springframework:spring-framework-bom` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom` |

受影响的子模块 gradle 文件完整列表：

| # | 文件 |
|---|------|
| 1 | `acl/spring-security-acl.gradle` |
| 2 | `aspects/spring-security-aspects.gradle` |
| 3 | `cas/spring-security-cas.gradle` |
| 4 | `config/spring-security-config.gradle` |
| 5 | `core/spring-security-core.gradle` |
| 6 | `crypto/spring-security-crypto.gradle` |
| 7 | `data/spring-security-data.gradle` |
| 8 | `docs/spring-security-docs.gradle` |
| 9 | `itest/context/spring-security-itest-context.gradle` |
| 10 | `itest/ldap/embedded-ldap-apacheds-default/spring-security-itest-ldap-embedded-apacheds-default.gradle` |
| 11 | `itest/ldap/embedded-ldap-mode-apacheds/spring-security-itest-ldap-embedded-mode-apacheds.gradle` |
| 12 | `itest/ldap/embedded-ldap-mode-unboundid/spring-security-itest-ldap-embedded-mode-unboundid.gradle` |
| 13 | `itest/ldap/embedded-ldap-none/spring-security-itest-ldap-embedded-none.gradle` |
| 14 | `itest/ldap/embedded-ldap-unboundid-default/spring-security-itest-ldap-embedded-unboundid-default.gradle` |
| 15 | `itest/web/spring-security-itest-web.gradle` |
| 16 | `ldap/spring-security-ldap.gradle` |
| 17 | `messaging/spring-security-messaging.gradle` |
| 18 | `oauth2/oauth2-client/spring-security-oauth2-client.gradle` |
| 19 | `oauth2/oauth2-core/spring-security-oauth2-core.gradle` |
| 20 | `oauth2/oauth2-jose/spring-security-oauth2-jose.gradle` |
| 21 | `oauth2/oauth2-resource-server/spring-security-oauth2-resource-server.gradle` |
| 22 | `openid/spring-security-openid.gradle` |
| 23 | `remoting/spring-security-remoting.gradle` |
| 24 | `rsocket/spring-security-rsocket.gradle` |
| 25 | `saml2/saml2-service-provider/spring-security-saml2-service-provider.gradle` |
| 26 | `taglibs/spring-security-taglibs.gradle` |
| 27 | `test/spring-security-test.gradle` |
| 28 | `web/spring-security-web.gradle` |

#### 4.6 新增文件（1 个文件）

| 文件 | 说明 |
|------|------|
| `doc/GAV_MAPPING.md` | GAV 坐标映射表，记录所有模块新旧坐标对照 |

### 5. 特殊处理

- **remoting 模块禁用**：内部 Spring Framework fork 不包含 `org.springframework.remoting.httpinvoker` 包（该功能在 Spring 6.x 已移除），因此在 `build.gradle` 中将 `bjca-footstone-bpring-security-remoting` 的所有 tasks 设为 `disabled`。
- **运行时版本伪装**：`SpringSecurityCoreVersion.getVersion()` 硬编码返回 `"5.8.16"`，不再从 MANIFEST 读取，确保 Spring Boot 等组件的版本兼容检查不受影响。
- **Spring Framework 版本说明**：`libs.versions.toml` 中 `org-springframework` 设为已通过 Nexus 验证的 RELEASE `5.3.39-nes.patch.1`。
