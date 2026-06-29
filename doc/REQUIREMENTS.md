# Spring Security 6.5.11 维护分支需求文档

## 任务主题

Spring Security 6.5.11 维护分支——CVE 漏洞评估、Nexus 私服发布配置、GAV 去特征化重命名

## 分支信息

| 属性 | 值 |
|------|---|
| **基础版本** | Spring Security 6.5.11（origin/6.5.x） |
| **工作分支** | `6.5.x-bjca-patch` |
| **版本号** | `6.5.11-nes.patch.1-SNAPSHOT` |
| **Group** | `cn.bjca.footstone.bpring.security` |
| **Spring Framework** | `6.2.19-nes.patch.1-SNAPSHOT`（内部 fork） |

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
| CVE-2025-8916 | 6.3 MODERATE | Bouncy Castle | **基线已修复** | 当前 BC 1.80.2 ≥ 1.79 |

## GAV 重构

| 维度 | 变更前 | 变更后 |
|------|--------|--------|
| GroupId | `org.springframework.security` | `cn.bjca.footstone.bpring.security` |
| ArtifactId | `spring-security-*` | `bjca-footstone-bpring-security-*` |
| Version | `6.5.12-SNAPSHOT` | `6.5.11-nes.patch.1-SNAPSHOT` |
| Spring Framework BOM | `org.springframework:spring-framework-bom:6.2.19` | `cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom:6.2.19-nes.patch.1-SNAPSHOT` |

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
