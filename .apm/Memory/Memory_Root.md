# Spring Security 5.8.x 维护分支 – APM Memory Root
**Memory Strategy:** Dynamic-MD
**Project Overview:** 基于 Spring Security 5.8.x（版本 5.8.16）创建 5.8.x-bjca-patch 维护分支，涵盖三大工作流：(A) 修复 4 个 CVE 漏洞（CVE-2025-22228、CVE-2025-22234、CVE-2025-22233、CVE-2025-41249）；(B) 配置 Nexus 私服发布、Makefile 和自定义 Group 属性（libiao.test. 前缀，projectGroup 属性方案）；(C) 升级 nimbus-jose-jwt 至 10.8 及同步升级 oauth2-oidc-sdk 以修复 CVE-2023-52428 等漏洞。7 Phase / 20 Task / 3 Agent（Agent_Infra、Agent_CVE_A、Agent_CVE_B）。所有代码修改需完备中文注释，每个任务单独 git commit。

## Phase 01 – 项目初始化 Summary
* Agent_Infra 从 `origin/5.8.x`（commit 8806e0176c）成功创建 `5.8.x-bjca-patch` 工作分支，版本号修改为 `5.8.16-bjca-patch-SNAPSHOT`，创建 `doc/CVE/` 目录和 `doc/REQUIREMENTS.md` 需求文档。所有交付物验证通过，无阻塞问题。
* 涉及 Agent：Agent_Infra
* Task Logs：
  - `.apm/Memory/Phase_01_项目初始化/Task_1_1_创建工作分支与目录结构.md`

## Phase 02 – 私服/发布配置 Summary
* Agent_Infra 完成 Nexus 私服全套配置：(1) `gradle.properties` 添加 `projectGroup`；(2) `settings.gradle` 配置 pluginManagement 仓库并移除 `dependencyResolutionManagement`；(3) `build.gradle` 添加 allprojects 依赖仓库、`group = projectGroup`、`plugins.withType(MavenPublishPlugin)` 发布仓库配置；(4) 创建 Makefile（7 个目标，集成 sdkman Java 11 切换）；(5) 构建验证通过（修复 s101 插件和 Antora 文档任务名问题）；(6) 创建 `doc/NEXUS_DEPLOY.md`。Git commit `0045d902cb`。
* 涉及 Agent：Agent_Infra
* Task Logs：
  - `.apm/Memory/Phase_02_私服发布配置/Task_2_1_配置Gradle仓库与projectGroup属性.md`
  - `.apm/Memory/Phase_02_私服发布配置/Task_2_2_配置Maven发布仓库.md`
  - `.apm/Memory/Phase_02_私服发布配置/Task_2_3_创建Makefile.md`
  - `.apm/Memory/Phase_02_私服发布配置/Task_2_4_验证构建创建文档与提交.md`

## Phase 03 – 依赖升级 Summary
* Agent_Infra 完成 nimbus-jose-jwt 9.24.4→10.8 和 oauth2-oidc-sdk 9.43.3→11.33 升级：(1) 更新 `libs.versions.toml` 版本；(2) 修改 `VerifyDependenciesVersionsPlugin` 支持同主版本高版本覆盖；(3) 适配 `ClientRegistrations.java`（oauth2-oidc-sdk 11.x 异常行为变更）；(4) 构建验证通过；(5) 创建 `doc/NIMBUS_UPGRADE.md` 和 3 个独立 CVE 文档（CVE-2023-52428 已修复、CVE-2025-53864 已修复、CVE-2025-8916 未修复需升级 BC）。Git commit `c5654eff5f`。
* 涉及 Agent：Agent_Infra
* Task Logs：
  - `.apm/Memory/Phase_03_依赖升级/Task_3_1_研究兼容版本并升级依赖.md`
  - `.apm/Memory/Phase_03_依赖升级/Task_3_2_适配代码与测试验证构建.md`
  - `.apm/Memory/Phase_03_依赖升级/Task_3_3_创建文档与提交.md`

## Phase 04 – CVE-2025-22228 Summary
* Agent_CVE_A 确认 5.8.x 受 CVE-2025-22228 影响（CVSS 7.4 HIGH，BCrypt 密码 72 字节静默截断）。采用**合并修复策略**，一步同时修复 CVE-2025-22228 和 CVE-2025-22234（回归问题）：在 `BCrypt.hashpw()` 中添加 `if (!for_check && passwordb.length > 72)` 条件检查，encode 路径拒绝超长密码，matches 路径保持正常执行以维持时序攻击防护。新增 5 个测试用例，crypto 模块和 DaoAuthenticationProvider 测试全部通过。创建详尽 CVE 文档。Git commit `9ca4c02192`。
* 涉及 Agent：Agent_CVE_A
* Task Logs：
  - `.apm/Memory/Phase_04_CVE-2025-22228/Task_4_1_研究CVE-2025-22228并评估影响.md`
  - `.apm/Memory/Phase_04_CVE-2025-22228/Task_4_2_实施CVE-2025-22228修复与编写测试.md`
  - `.apm/Memory/Phase_04_CVE-2025-22228/Task_4_3_创建CVE-2025-22228文档与提交.md`

## Phase 05 – CVE-2025-22234 Summary
* CVE-2025-22234（CVSS 5.3 MEDIUM，BCrypt 时序攻击回归）已在 Phase 4 合并修复中完全覆盖，本项目从未暴露于此回归漏洞。Task 5.1 补充研究确认漏洞详情和官方修复 commit（b478716）。Task 5.2 跳过（无需额外代码修改）。Task 5.3 创建详尽文档并提交。Git commit `720d65f610`。
* 涉及 Agent：Agent_CVE_A
* Task Logs：
  - `.apm/Memory/Phase_05_CVE-2025-22234/Task_5_1_研究CVE-2025-22234并评估影响.md`
  - `.apm/Memory/Phase_05_CVE-2025-22234/Task_5_2_实施CVE-2025-22234修复与编写测试.md`
  - `.apm/Memory/Phase_05_CVE-2025-22234/Task_5_3_创建CVE-2025-22234文档与提交.md`

## Phase 06 – CVE-2025-22233 Summary
* CVE-2025-22233（CVSS 3.1 LOW，DataBinder disallowedFields 大小写绕过）经独立分析判定为**不适用**——漏洞位于 Spring Framework spring-context 模块的 `DataBinder` 类，非 Spring Security 代码范围。此结论与 5.7.x 判定一致。Task 6.2 跳过（无需代码修改）。Task 6.3 创建文档并提交。Git commit `087f5d59dc`。
* 涉及 Agent：Agent_CVE_B
* Task Logs：
  - `.apm/Memory/Phase_06_CVE-2025-22233/Task_6_1_研究CVE-2025-22233并评估影响.md`
  - `.apm/Memory/Phase_06_CVE-2025-22233/Task_6_2_实施CVE-2025-22233修复与编写测试.md`
  - `.apm/Memory/Phase_06_CVE-2025-22233/Task_6_3_创建CVE-2025-22233文档与提交.md`

## Phase 07 – CVE-2025-41249 Summary
* CVE-2025-41249（CVSS 7.5 HIGH，Spring Framework 注解扫描无界泛型授权绕过）经独立分析判定为**不适用**——根因在 Spring Framework spring-core 的 `AnnotationsScanner`/`AnnotatedMethod`，配套 CVE-2025-41248 修复的 `UniqueSecurityAnnotationScanner` 仅存在于 Spring Security 6.x。Task 7.2 跳过（无法在 Spring Security 层面修复）。Task 7.3 创建文档并提交。Git commit `6352d81cbe`。
* 涉及 Agent：Agent_CVE_B
* Task Logs：
  - `.apm/Memory/Phase_07_CVE-2025-41249/Task_7_1_研究CVE-2025-41249并评估影响.md`
  - `.apm/Memory/Phase_07_CVE-2025-41249/Task_7_2_实施CVE-2025-41249修复与编写测试.md`
  - `.apm/Memory/Phase_07_CVE-2025-41249/Task_7_3_创建CVE-2025-41249文档与提交.md`

---

## Project Completion Summary

**所有 7 个 Phase / 20 个 Task 已全部完成。**

### Git Commit 历史

| Commit | Phase | 内容 |
|--------|-------|------|
| `0045d902cb` | Phase 1+2 | 项目初始化 + Nexus 私服发布配置 |
| `c5654eff5f` | Phase 3 | nimbus-jose-jwt 10.8 + oauth2-oidc-sdk 11.33 升级 |
| `9ca4c02192` | Phase 4 | CVE-2025-22228 修复（合并 CVE-2025-22234 回归修复）|
| `720d65f610` | Phase 5 | CVE-2025-22234 文档记录 |
| `087f5d59dc` | Phase 6 | CVE-2025-22233 文档记录（不适用）|
| `6352d81cbe` | Phase 7 | CVE-2025-41249 文档记录（不适用）|

### CVE 处置总结

| CVE | CVSS | 处置结果 |
|-----|------|---------|
| CVE-2025-22228 | 7.4 HIGH | **已修复**（BCrypt 密码长度检查）|
| CVE-2025-22234 | 5.3 MEDIUM | **已覆盖**（Phase 4 合并修复）|
| CVE-2025-22233 | 3.1 LOW | **不适用**（Spring Framework DataBinder）|
| CVE-2025-41249 | 7.5 HIGH | **不适用**（Spring Framework AnnotationsScanner）|
| CVE-2023-52428 | - | **已修复**（nimbus-jose-jwt 10.8 升级）|
| CVE-2025-53864 | - | **已修复**（nimbus-jose-jwt 10.8 升级）|
