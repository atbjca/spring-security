# Spring Security 6.5.11 维护分支需求文档

## 任务主题

Spring Security 6.5.11 维护分支——CVE 漏洞评估、Nexus 私服发布配置、GAV 去特征化重命名

## 分支信息

| 属性 | 值 |
|------|---|
| **基础版本** | Spring Security 6.5.11（origin/6.5.x） |
| **工作分支** | `6.5.x-bjca-patch` |
| **版本号** | `6.5.11-nes.patch.2-SNAPSHOT`（下一维护版本，尚未发布） |
| **springSecurityVersion** | `6.5.11`（构建时注入 `SpringSecurityCoreVersion.getVersion()`） |
| **Group** | `cn.bjca.footstone.bpring.security` |
| **Spring Framework** | `6.2.19-nes.patch.1`（已发布的内部 RELEASE） |

## CVE 漏洞处置清单

| CVE 编号 | CVSS | 影响组件 | 处置结果 | 说明 |
|----------|------|---------|---------|------|
| CVE-2025-22228 | 7.4 HIGH | Spring Security (BCrypt) | **基线已修复** | 6.5.x 已含 72 字节密码长度校验 |
| CVE-2025-22234 | 5.3 MEDIUM | Spring Security (BCrypt) | **基线已覆盖** | 与 CVE-2025-22228 合并修复 |
| CVE-2025-22233 | 3.1 LOW | Spring Framework (DataBinder) | **不适用** | 漏洞位于 Spring Framework |
| CVE-2025-41249 | 7.5 HIGH | Spring Framework (AnnotationsScanner) | **不适用** | 漏洞位于 Spring Framework spring-core |
| CVE-2025-41248 | — | Spring Security 6.x | **待 Framework fork 确认** | 配套 CVE-2025-41249，需 Framework ≥6.2.11 |
| CVE-2024-22257 | 8.2 HIGH | AuthenticatedVoter | **基线已修复** | 6.4.x+ 已修 |
| CVE-2024-22258 | 6.1 MEDIUM | Spring Authorization Server | **不适用** | 非 Spring Security 核心范围 |
| CVE-2024-38827 | 4.8 MEDIUM | Locale 大小写绕过 | **基线已修复** | 6.3.x+ 已修 |
| CVE-2024-38821 | 9.3 CRITICAL | WebFlux 防火墙 | **基线已修复** | 6.3.x+ 已修 |
| CVE-2023-52428 | 7.5 HIGH | nimbus-jose-jwt | **基线已覆盖** | 当前 9.37.4 ≥ 9.37.2 |
| CVE-2025-53864 | 5.8 MEDIUM | nimbus-jose-jwt | **基线已覆盖** | 当前 9.37.4 ≥ 9.37.4 |
| CVE-2025-8916 | 6.3 MODERATE | Bouncy Castle | **基线已修复** | 当前 BC 1.84 ≥ 1.79 |
| CVE-2026-59889 | HIGH | Jackson Databind | **基线已修复** | Jackson BOM 已从 2.18.8 升级至 2.18.9；仅在特定外部类型/多态反序列化配置下受影响 |
| CVE-2026-54515 | HIGH | Jackson Databind | **基线已修复** | Jackson BOM 2.18.9 含修复；Spring Security 未发现直接使用受影响 JsonView 配置的代码路径 |
| Jackson JsonView external-property advisory（CVE 未分配） | — | Jackson Databind | **基线已覆盖** | 通过 BOM 2.18.9 获取上游修复；不将未分配编号的公告误记为 CVE |
| CVE-2026-41721 | 8.2 HIGH | Spring Data Commons (MapDataBinder) | **基线已修复** | 已切换 NES Spring Data Commons 3.5.13，官方 3.5.12 起修复 |
| CVE-2026-41716 | 7.5 HIGH | Spring Data Commons (TypeDiscoverer) | **基线已修复** | NES Spring Data Commons 3.5.13 包含有界属性初始化修复 |
| CVE-2026-41711 | 5.9 MEDIUM | Spring Data Commons (PropertyPath) | **基线已修复** | NES Spring Data Commons 3.5.13 包含统一解析深度限制 |
| CVE-2026-41695 | HIGH | Spring Data Commons (PersistentPropertyPathFactory) | **基线已修复** | NES Spring Data Commons 3.5.13 使用有界 ConcurrentLruCache |
| CVE-2026-41848 | LOW | Spring Framework (AntPathMatcher) | **基线已修复** | 清除 Spring Data 传递的官方 spring-core 6.2.15，统一使用 NES 6.2.19 |
| CVE-2026-41720 | HIGH | Spring LDAP | **基线已修复** | Spring LDAP 3.2.16 升至 3.3.8；保留 Spring Security 空密码拒绝 |
| CVE-2026-5588 | MODERATE | Bouncy Castle BCPKIX | **基线已修复** | Bouncy Castle 1.84 修复复合签名空序列验证问题 |
| CVE-2026-0636 | MODERATE | Bouncy Castle Provider | **基线已修复** | Bouncy Castle 1.84 修复 LDAPStoreHelper LDAP 注入 |

## GAV 重构

| 维度 | 变更前 | 变更后 |
|------|--------|--------|
| GroupId | `org.springframework.security` | `cn.bjca.footstone.bpring.security` |
| ArtifactId | `spring-security-*` | `bjca-footstone-bpring-security-*` |
| Version | `6.5.12-SNAPSHOT` | `6.5.11-nes.patch.2-SNAPSHOT` |
| Spring Framework BOM | `org.springframework:spring-framework-bom:6.2.19` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom:6.2.19-nes.patch.1` |

详见 `doc/GAV_MAPPING.md`。

## 文档清单

| 文件 | 说明 |
|------|------|
| `doc/REQUIREMENTS.md` | 本文档 |
| `doc/NEXUS_DEPLOY.md` | Nexus 私服配置 |
| `doc/GAV_MAPPING.md` | GAV 坐标映射表 |
| `doc/QUICK_START.md` | 快速入门 |
| `doc/USER_MANUAL.md` | 用户手册 |
| `doc/CVE/*.md` | 各 CVE 评估记录 |
