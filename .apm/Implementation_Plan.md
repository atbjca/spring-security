# Spring Security 5.8.x 维护分支 – APM Implementation Plan
**Memory Strategy:** Dynamic-MD
**Last Modification:** 所有 7 个 Phase 已完成。最后 commit `6352d81cbe`（Phase 7 CVE-2025-41249 文档）。项目完结。
**Project Overview:** 基于 Spring Security 5.8.x（版本 5.8.16）创建 5.8.x-bjca-patch 维护分支，包括：(A) 修复 4 个 CVE 漏洞（CVE-2025-22228、CVE-2025-22234、CVE-2025-22233、CVE-2025-41249）；(B) 参考 spring-boot-2.7 和 spring-framework 项目，配置 Nexus 私服发布、Makefile 和自定义 Group 属性（libiao.test. 前缀）；(C) 升级 nimbus-jose-jwt 至 10.8 及同步升级 oauth2-oidc-sdk 以修复 CVE-2023-52428 等漏洞。所有代码修改需添加完备中文注释，每个任务单独 git commit（CVE commit 前需用户确认）。版本号：5.8.16-bjca-patch-SNAPSHOT。

## Phase 1: 项目初始化

### Task 1.1 – 创建工作分支与目录结构 - Agent_Infra
**Objective:** 搭建项目工作环境，包括分支、版本号、目录结构和需求文档。
**Output:** 可用的 5.8.x-bjca-patch 工作分支、doc/CVE/ 目录、doc/REQUIREMENTS.md 文件。
**Guidance:** 此任务是所有后续 Phase 的前置条件，必须首先完成。

- 从 origin/5.8.x 创建并切换到 `5.8.x-bjca-patch` 分支
- 修改 `gradle.properties` 中 version 为 `5.8.16-bjca-patch-SNAPSHOT`
- 创建 `doc/CVE/` 目录结构
- 创建 `doc/REQUIREMENTS.md`，记录本次任务请求主题："Spring Security 5.8.16 维护分支——CVE 漏洞修复（CVE-2025-22228、CVE-2025-22234、CVE-2025-22233、CVE-2025-41249）、Nexus 私服发布配置、nimbus-jose-jwt 依赖升级"

## Phase 2: 私服/发布配置

### Task 2.1 – 配置 Gradle 仓库与 projectGroup 属性 - Agent_Infra
**Objective:** 配置 Nexus 私服作为依赖下载源和插件仓库，并设置自定义 Group 属性。
**Output:** 修改后的 gradle.properties、settings.gradle、build.gradle（仓库和 Group 配置部分）。
**Guidance:** 参考 /Users/anan/Documents/GitHub/spring-framework 的实现方式（projectGroup 属性方案）。**Depends on: Task 1.1 Output**

1. 在 `gradle.properties` 中添加 `projectGroup=libiao.test.org.springframework.security`
2. 修改 `settings.gradle`，在 pluginManagement.repositories 中添加 Nexus 私服仓库配置（参考 /Users/anan/Documents/GitHub/spring-framework/settings.gradle）
3. 修改 `build.gradle`，在 allprojects/repositories 中添加 Nexus 私服作为依赖下载源（参考 /Users/anan/Documents/GitHub/spring-framework/build.gradle 的仓库配置部分）
4. 修改 `build.gradle`，使用 `projectGroup` 属性配置全局 group（参考 spring-framework 的 `group = projectGroup` 方式）

### Task 2.2 – 配置 Maven 发布仓库 - Agent_Infra
**Objective:** 配置全局 Maven 发布仓库，使所有子项目能发布到 Nexus 私服。
**Output:** 修改后的 build.gradle（发布仓库配置部分）。
**Guidance:** 使用 plugins.withType(MavenPublishPlugin) 模式，自动区分 SNAPSHOT/Release 仓库。**Depends on: Task 2.1 Output**

1. 分析 5.8.x 现有的 Maven 发布插件结构（buildSrc 中的插件和 gradle/ 目录下的脚本）
2. 在 `build.gradle` 中添加全局发布仓库配置，使用 `plugins.withType(MavenPublishPlugin)` 模式，根据版本号自动选择 nexusSnapshotUrl 或 nexusReleaseUrl（参考 /Users/anan/Documents/GitHub/spring-framework/build.gradle 的发布配置）
3. 在所有新增配置处添加中文注释说明

### Task 2.3 – 创建 Makefile - Agent_Infra
**Objective:** 创建 Makefile 提供常用构建命令快捷方式。
**Output:** 项目根目录下的 Makefile 文件。
**Guidance:** 参考 spring-framework 和 spring-boot-2.7 的 Makefile，根据 Spring Security 5.8.x 构建特点适配。**Depends on: Task 2.2 Output**

- 参考 /Users/anan/Documents/GitHub/spring-framework/Makefile 和 /Users/anan/Documents/GitHub/spring-boot-2.7/Makefile 的命令结构
- 创建 Makefile，包含：clean、build-thin（跳过测试和文档）、install（publishToMavenLocal）、deploy（publish 到 Nexus）、stop（停止 Gradle 守护进程）
- 根据 Spring Security 5.8.x 的构建特点适配命令参数

### Task 2.4 – 验证构建、创建文档与提交 - Agent_Infra
**Objective:** 验证构建配置可用，创建文档记录，提交代码。
**Output:** 构建验证通过、doc/NEXUS_DEPLOY.md 文档、git commit。
**Guidance:** 构建验证至少需要 make build-thin 成功。commit 前需要向用户确认。**Depends on: Task 2.3 Output**

1. 执行 `make build-thin` 验证构建成功，如遇到问题则排查修复
2. 创建 `doc/NEXUS_DEPLOY.md`，记录私服配置方案、修改的文件清单、使用方法（make 命令说明）、Nexus 属性配置要求
3. 请求用户确认 commit 内容
4. 执行 git commit，使用中文 commit 消息

## Phase 3: 依赖升级

### Task 3.1 – 研究兼容版本并升级依赖 - Agent_Infra
**Objective:** 将 nimbus-jose-jwt 升级至 10.8，同步升级 oauth2-oidc-sdk 至兼容版本。
**Output:** 更新后的 libs.versions.toml，版本对齐验证通过。
**Guidance:** 5.8.x 使用 gradle/libs.versions.toml 管理版本，buildSrc 中有 VerifyDependenciesVersionsPlugin 验证 nimbus 与 oauth2-oidc-sdk 版本对齐。当前版本：nimbus-jose-jwt 9.24.4, oauth2-oidc-sdk 9.43.3。**Depends on: Task 2.4 Output**

1. Ad-Hoc Delegation – 版本兼容性研究：查找 nimbus-jose-jwt 10.8 对应的 oauth2-oidc-sdk 兼容版本，以及可能的 API 变更
2. 更新 `gradle/libs.versions.toml` 中 nimbus-jose-jwt 版本为 10.8
3. 更新 `gradle/libs.versions.toml` 中 oauth2-oidc-sdk 版本为研究确定的兼容版本
4. 检查并适配 buildSrc 中的 VerifyDependenciesVersionsPlugin，确保版本对齐验证通过

### Task 3.2 – 适配代码与测试、验证构建 - Agent_Infra
**Objective:** 处理依赖升级导致的 API 变更和测试失败，确保构建成功。
**Output:** 适配后的代码（含中文注释）、通过的测试。
**Guidance:** 5.7.x 升级经验表明 json-smart → Gson 迁移会影响 oauth2 模块测试。重点关注 oauth2-jose、oauth2-core、oauth2-client 模块。**Depends on: Task 3.1 Output**

1. 执行构建，识别因依赖升级导致的编译错误和 API 变更
2. 适配受影响的代码（重点关注 oauth2-jose、oauth2-core、oauth2-client 模块），在修改处添加中文注释说明升级原因
3. 运行受影响模块的测试套件，修复因依赖变更导致的测试失败（参考 5.7.x 的 json-smart → Gson 迁移经验）
4. 执行 `make build-thin` 验证全量构建成功

### Task 3.3 – 创建文档与提交 - Agent_Infra
**Objective:** 创建依赖升级文档，记录变更内容，提交代码。
**Output:** doc/NIMBUS_UPGRADE.md 文档、git commit。
**Guidance:** 文档需覆盖升级前后版本对比、修复的 CVE、代码适配内容。**Depends on: Task 3.2 Output**

1. 创建 `doc/NIMBUS_UPGRADE.md`，记录：升级前后版本对比、修复的 CVE 列表（CVE-2023-52428、CVE-2025-53864、CVE-2025-8916）、代码适配内容、测试结果
2. 请求用户确认 commit 内容
3. 执行 git commit，使用中文 commit 消息

## Phase 4: CVE-2025-22228

### Task 4.1 – 研究 CVE-2025-22228 并评估影响 - Agent_CVE_A
**Objective:** 研究 CVE-2025-22228 的漏洞详情、官方修复方案，独立评估对 5.8.x 的影响。
**Output:** CVE 研究报告（漏洞描述、CVSS、官方 commitId、影响评估结论）。
**Guidance:** 此任务为 Phase 4 的起点，其输出决定 Task 4.2 是否执行。必须独立分析 5.8.x 代码，不照搬 5.7.x 结论。**Depends on: Task 1.1 Output by Agent_Infra**

1. Ad-Hoc Delegation – CVE 研究：检索 CVE-2025-22228 的漏洞详情（描述、影响组件、CVSS 评分、受影响版本范围）
2. 查找 Spring Security 官方对该 CVE 的修复 commit（commitId、修复方案描述、修复版本）
3. 独立分析 5.8.x 代码库中 BCrypt.java（crypto/src/main/java/org/springframework/security/crypto/bcrypt/BCrypt.java）的实现，确认漏洞是否存在
4. 输出影响评估结论（受影响/不受影响），并记录研究发现供后续任务使用

### Task 4.2 – 实施 CVE-2025-22228 修复与编写测试 - Agent_CVE_A
**Objective:** Backport 官方修复方案到 5.8.x，添加中文注释，编写测试用例。
**Output:** 修复后的代码（含中文注释）、通过的测试用例。
**Guidance:** 条件性任务——仅在 Task 4.1 确认受影响时执行。需独立分析 5.8.x 代码结构决定修复方式。注意：CVE-2025-22234 是此 CVE 的回归修复，需评估是否采用合并修复策略。**Depends on: Task 4.1 Output**

1. 独立分析 5.8.x 的 BCrypt.java 代码结构，确定修复的最佳插入点
2. 基于官方修复方案，将修复 backport 到 5.8.x 代码库对应位置
3. 在所有修改处添加完备的中文注释，说明修复目的和 CVE 关联
4. 编写 JUnit 测试用例，覆盖漏洞场景和修复验证（正常密码、边界值 72 字节、超长密码）
5. 运行 crypto 模块测试套件确保无回归，记录测试结果

### Task 4.3 – 创建 CVE-2025-22228 文档与提交 - Agent_CVE_A
**Objective:** 创建 CVE 文档，经用户确认后 git commit。
**Output:** doc/CVE/CVE-2025-22228.md 文档、git commit。
**Guidance:** **Depends on: Task 4.1 Output, Task 4.2 Output（如执行）**。不受影响时仅记录文档，无需代码 commit。

1. 在 `doc/CVE/` 下创建 `CVE-2025-22228.md` 文档，包含：漏洞描述、影响分析、官方修复方案及 commitId、本地修复方案（或"不受影响"说明）、测试结果
2. 请求用户确认 commit 内容
3. 执行 git commit，使用中文 commit 消息

## Phase 5: CVE-2025-22234

### Task 5.1 – 研究 CVE-2025-22234 并评估影响 - Agent_CVE_A
**Objective:** 研究 CVE-2025-22234 的漏洞详情、官方修复方案，独立评估对 5.8.x 的影响，评估与 CVE-2025-22228 修复的关联。
**Output:** CVE 研究报告（漏洞描述、CVSS、官方 commitId、影响评估结论、与 Phase 4 修复的关联分析）。
**Guidance:** CVE-2025-22234 是 CVE-2025-22228 的回归修复。如果 Task 4.2 已采用合并修复策略，此 CVE 可能已被覆盖。需明确判断。**Depends on: Task 4.3 Output**

1. Ad-Hoc Delegation – CVE 研究：检索 CVE-2025-22234 的漏洞详情（描述、影响组件、CVSS 评分、受影响版本范围）
2. 查找 Spring Security 官方对该 CVE 的修复 commit（commitId、修复方案描述、修复版本）
3. 独立分析 5.8.x 代码库中受影响组件，确认漏洞是否存在；如果 Task 4.2 已执行合并修复，评估是否已覆盖此 CVE
4. 输出影响评估结论，并记录与 CVE-2025-22228 的关联关系

### Task 5.2 – 实施 CVE-2025-22234 修复与编写测试 - Agent_CVE_A
**Objective:** 实施修复（如未被 Phase 4 覆盖），添加中文注释，编写测试用例。
**Output:** 修复后的代码（含中文注释）、通过的测试用例（或"已在 Phase 4 合并修复"的确认）。
**Guidance:** 条件性任务——仅在 Task 5.1 确认需要额外修复时执行。如已在 Task 4.2 合并修复则跳过代码修改，仅补充测试。**Depends on: Task 5.1 Output**

1. 根据 Task 5.1 的评估结论，确定是否需要额外修复（如已在 Task 4.2 合并修复则跳过代码修改）
2. 如需修复：基于官方修复方案，将修复 backport 到 5.8.x 对应位置
3. 在所有修改处添加完备的中文注释
4. 编写/补充测试用例验证修复效果，运行测试确保无回归

### Task 5.3 – 创建 CVE-2025-22234 文档与提交 - Agent_CVE_A
**Objective:** 创建 CVE 文档，经用户确认后 git commit。
**Output:** doc/CVE/CVE-2025-22234.md 文档、git commit。
**Guidance:** 文档需说明与 CVE-2025-22228 的关联关系和处置策略。**Depends on: Task 5.1 Output, Task 5.2 Output（如执行）**

1. 在 `doc/CVE/` 下创建 `CVE-2025-22234.md` 文档，包含：漏洞描述、影响分析、与 CVE-2025-22228 的关联说明、官方修复方案及 commitId、本地处置方案、测试结果
2. 请求用户确认 commit 内容
3. 执行 git commit，使用中文 commit 消息

## Phase 6: CVE-2025-22233

### Task 6.1 – 研究 CVE-2025-22233 并评估影响 - Agent_CVE_B
**Objective:** 研究 CVE-2025-22233 的漏洞详情、官方修复方案，独立评估对 5.8.x 的影响。
**Output:** CVE 研究报告（漏洞描述、CVSS、官方 commitId、影响评估结论）。
**Guidance:** 5.7.x 中此 CVE 被判定为不适用（Spring Framework 漏洞），但 5.8.x 需独立分析确认。**Depends on: Task 1.1 Output by Agent_Infra**

1. Ad-Hoc Delegation – CVE 研究：检索 CVE-2025-22233 的漏洞详情（描述、影响组件、CVSS 评分、受影响版本范围）
2. 查找官方修复 commit（commitId、修复方案描述、修复版本）
3. 独立分析 5.8.x 代码库中受影响组件，确认漏洞是否存在于 Spring Security 层面
4. 输出影响评估结论（受影响/不受影响/不适用），并记录研究发现

### Task 6.2 – 实施 CVE-2025-22233 修复与编写测试 - Agent_CVE_B
**Objective:** Backport 官方修复方案到 5.8.x，添加中文注释，编写测试用例。
**Output:** 修复后的代码（含中文注释）、通过的测试用例。
**Guidance:** 条件性任务——仅在 Task 6.1 确认受影响时执行。**Depends on: Task 6.1 Output**

1. 基于 Task 6.1 的官方修复方案，将修复 backport 到 5.8.x 代码库对应位置
2. 在所有修改处添加完备的中文注释
3. 编写 JUnit 测试用例，覆盖漏洞场景和修复验证
4. 运行受影响模块测试套件确保无回归，记录测试结果

### Task 6.3 – 创建 CVE-2025-22233 文档与提交 - Agent_CVE_B
**Objective:** 创建 CVE 文档，经用户确认后 git commit。
**Output:** doc/CVE/CVE-2025-22233.md 文档、git commit。
**Guidance:** **Depends on: Task 6.1 Output, Task 6.2 Output（如执行）**。不受影响/不适用时仅记录文档。

1. 在 `doc/CVE/` 下创建 `CVE-2025-22233.md` 文档，包含：漏洞描述、影响分析、官方修复方案及 commitId、本地处置方案（或"不受影响/不适用"说明）、测试结果
2. 请求用户确认 commit 内容
3. 执行 git commit，使用中文 commit 消息

## Phase 7: CVE-2025-41249

### Task 7.1 – 研究 CVE-2025-41249 并评估影响 - Agent_CVE_B
**Objective:** 从零研究 CVE-2025-41249 的漏洞详情、官方修复方案，评估对 5.8.x 的影响。
**Output:** CVE 研究报告（漏洞描述、CVSS、官方 commitId、影响评估结论）。
**Guidance:** 全新 CVE，无 5.7.x 经验可参考，需完整研究。**Depends on: Task 6.3 Output**

1. Ad-Hoc Delegation – CVE 研究：检索 CVE-2025-41249 的漏洞详情（描述、影响组件、CVSS 评分、受影响版本范围）
2. 查找 Spring Security 官方对该 CVE 的修复 commit（commitId、修复方案描述、修复版本）
3. 独立分析 5.8.x 代码库中受影响的组件，确认漏洞是否存在
4. 输出影响评估结论（受影响/不受影响），并记录研究发现供后续任务使用

### Task 7.2 – 实施 CVE-2025-41249 修复与编写测试 - Agent_CVE_B
**Objective:** Backport 官方修复方案到 5.8.x，添加中文注释，编写测试用例。
**Output:** 修复后的代码（含中文注释）、通过的测试用例。
**Guidance:** 条件性任务——仅在 Task 7.1 确认受影响时执行。全新 CVE，修复范围和复杂度待研究确定。**Depends on: Task 7.1 Output**

1. 基于 Task 7.1 的官方修复方案，将修复 backport 到 5.8.x 代码库对应位置
2. 在所有修改处添加完备的中文注释，说明修复目的和 CVE 关联
3. 编写 JUnit 测试用例，覆盖漏洞场景和修复验证
4. 运行受影响模块测试套件确保无回归，记录测试结果

### Task 7.3 – 创建 CVE-2025-41249 文档与提交 - Agent_CVE_B
**Objective:** 创建 CVE 文档，经用户确认后 git commit。
**Output:** doc/CVE/CVE-2025-41249.md 文档、git commit。
**Guidance:** **Depends on: Task 7.1 Output, Task 7.2 Output（如执行）**。不受影响时仅记录文档。

1. 在 `doc/CVE/` 下创建 `CVE-2025-41249.md` 文档，包含：漏洞描述、影响分析、官方修复方案及 commitId、本地修复方案（或"不受影响"说明）、测试结果
2. 请求用户确认 commit 内容
3. 执行 git commit，使用中文 commit 消息
