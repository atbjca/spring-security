# Nexus 私服发布配置说明

## 概述

本项目基于 Spring Security 6.5.x 维护分支（`6.5.x-bjca-patch`），配置了 Nexus 私服作为依赖下载源和构建产物发布目标。所有子模块的依赖下载和 Maven 发布均通过 Nexus 私服完成，同时使用自定义 Group ID（`cn.bjca.footstone.bpring.security`）以区分官方发布。

## 修改文件清单

### 1. `gradle.properties`
- 添加 `projectGroup=cn.bjca.footstone.bpring.security`，定义全局 Group ID
- 修改 `version=6.5.11-nes.patch.1-SNAPSHOT`

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
- 集成 sdkman 自动切换 Java 17（Spring Security 6.5.x 要求 JDK 17+）

## 使用方法

| 命令 | 用途 |
|---|---|
| `make clean` | 清理构建产物 |
| `make build-thin` | 快速构建（跳过测试、文档、代码检查），日常开发验证编译 |
| `make build` | 全量构建（含测试，耗时较长） |
| `make install` | 发布到本地 Maven 仓库（`~/.m2/repository`），跳过测试 |
| `make deploy` | 发布到 Nexus 私服，跳过测试 |
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
- 构建需要 Java 17+（通过 sdkman 管理），Makefile 已自动处理切换
