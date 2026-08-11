# Nexus 私服发布配置说明

## 概述

本项目基于 Spring Security 5.8.x 维护分支（`5.8.x-bjca-patch`），配置了 Nexus 私服作为依赖下载源和构建产物发布目标。当前开发坐标为 `cn.bjca.footstone.bpring.security` / `5.8.16-nes.patch.2-SNAPSHOT`，上一不可变正式版本为 `5.8.16-nes.patch.1`。发布或上传 SNAPSHOT 前必须先通过 Java 8 和消费者依赖安全门禁。

## 修改文件清单

### 1. `gradle.properties`
- `projectGroup=cn.bjca.footstone.bpring.security`，定义全局 Group ID
- `version=5.8.16-nes.patch.2-SNAPSHOT`

### 2. `settings.gradle`
- 在 `pluginManagement.repositories` 中添加 Nexus 私服仓库（公共仓库 + 快照仓库）和 Spring Release 仓库
- 移除 `dependencyResolutionManagement` 块（改由 `build.gradle` 统一管理仓库，避免配置冲突）

### 3. `build.gradle`
- 设置 `group = projectGroup`（替换原硬编码的 `org.springframework.security`）
- 添加 `allprojects { repositories { ... } }` 配置 Nexus 私服作为所有项目的依赖下载源
- 添加 `allprojects { plugins.withType(MavenPublishPlugin) }` 配置全局 Maven 发布仓库，根据版本号自动选择 snapshot 或 release 仓库
- 禁用 `s101` 插件（Structure101 代码分析插件，配置阶段访问外部网络会导致构建失败）

### 4. `Makefile`
- 新建文件，提供常用构建命令快捷方式
- 集成 sdkman 自动切换 Java 11（buildSrc 需要 Java 11+，主代码通过 Gradle Toolchain 使用 Java 8 编译）
- `make deploy` 在 Nexus 上传前调用 `make verify-published-security`
- `-PbuildSrc.skipTests=true` 跳过依赖本机环境的 buildSrc showcase 测试；发布构件仍执行 Java 8 class-file 和消费者 smoke 门禁

### 5. `scripts/verify-published-security.sh`
- 将候选构件发布到隔离的本地仓库，不直接修改 Nexus
- 使用真实 Java 8 JVM 运行 Maven 和 Gradle consumers
- 检查 Bouncy Castle、OpenSAML 3、Xerces 等依赖基线及禁止依赖
- 检查 Java 8 class-file、OpenSAML 4 缺失、代表性运行时加载和加密 smoke
- 生成 source commit、Java 运行时、依赖树、处置记录、smoke 结果和候选 SHA-256 证据
- 仅依赖 Bash、Maven、Gradle 和标准 Unix 工具，不要求安装 `rg`

## 使用方法

| 命令 | 用途 |
|---|---|
| `make clean` | 清理构建产物 |
| `make build-thin` | 快速构建（跳过测试、文档、代码检查），日常开发验证编译 |
| `make build` | 全量构建（含测试，耗时较长） |
| `make install` | 发布到本地 Maven 仓库（`~/.m2/repository`），跳过测试 |
| `make verify-published-security` | 发布到隔离候选仓库并运行 Java 8/依赖安全门禁，不上传 Nexus |
| `make deploy` | 先运行上述门禁，成功后发布到 Nexus |
| `make stop` | 停止所有 Gradle Daemon 进程，释放内存和文件锁 |
| `make projects` | 查看所有子项目列表 |

## Nexus 属性配置要求

需要在 `~/.gradle/gradle.properties` 中配置以下属性：

```properties
# Nexus 私服地址
nexusPublicUrl=http://<nexus-host>:<port>/repository/maven-public/
nexusReleaseUrl=http://<nexus-host>:<port>/repository/releases/
nexusSnapshotUrl=http://<nexus-host>:<port>/repository/snapshots/

# Nexus 认证凭证
nexusUsername=<用户名>
nexusPassword=<密码>
```

**注意事项：**
- 这些属性配置在用户级别的 `~/.gradle/gradle.properties` 中，不会提交到版本控制
- `nexusPublicUrl` 用于依赖下载（pluginManagement 和 allprojects 仓库）
- `nexusReleaseUrl` 和 `nexusSnapshotUrl` 用于 Maven 发布，根据版本号是否包含 `SNAPSHOT` 自动选择
- 构建需要 Java 11+（通过 sdkman 管理），Makefile 已自动处理切换
- 门禁还需要可执行的 Java 8 JDK；可通过 `JAVA8_HOME` 指定，或使用 sdkman 中的 `8.*` 安装
- `make deploy` 的候选验证失败时会在上传 Nexus 前退出
- SNAPSHOT 版本只进入 Nexus snapshot 仓库；正式发布 patch.2 前必须通过独立 release change 将版本冻结为 `5.8.16-nes.patch.2`
