## Why

当前 `gradle/wrapper/gradle-wrapper.properties` 配置指向 `file:///Volumes/LIBIAO_EX/dev/gradle-7.6.3-bin.zip`，但 `LIBIAO_EX` 卷在当前环境不可访问，导致 `./gradlew` 命令无法正常工作。

参考 `spring-boot-3.5` 项目采用的 `setup-gradle` 方案：使用官方 https URL 下载 Gradle，同时支持通过本地 zip 缓存实现离线构建。

## What Changes

- **修改** `gradle/wrapper/gradle-wrapper.properties`：
  - 将 `distributionUrl` 从 `file:///Volumes/LIBIAO_EX/dev/gradle-7.6.3-bin.zip` 改为 `https://services.gradle.org/distributions/gradle-7.6.3-bin.zip`
  - 添加 `networkTimeout=10000` 和 `validateDistributionUrl=true`

- **新增** `scripts/setup-gradle-local.sh`：
  - 从 `spring-boot-3.5` 项目复制
  - 扫描 `~/dev/gradle-*-bin.zip` 或 `*-all.zip`
  - 计算 MD5 hash（Gradle Wrapper 路径格式）
  - 复制 zip 到 `~/.gradle/wrapper/dists/<name>/<hash>/`
  - 解压并标记 `.ok` 文件
  - 后续 `./gradlew` 可完全离线运行（基于缓存）

- **修改** `Makefile`：
  - 在 `.PHONY` 添加 `setup-gradle`
  - 添加 `setup-gradle` target：
    - `LOCAL_GRADLE_DIR ?= $(HOME)/dev`（默认 `~/dev`）
    - 调用 `scripts/setup-gradle-local.sh`

## Capabilities

### New Capabilities

- `gradle-local-cache`：通过本地 Gradle zip 文件预填充 Wrapper 缓存，支持离线构建

### Modified Capabilities

（无）

## Impact

- **修改文件**：
  - `gradle/wrapper/gradle-wrapper.properties`
  - `Makefile`
- **新增文件**：
  - `scripts/setup-gradle-local.sh`
- **无 API 变更**：纯构建配置变更
- **向后兼容**：本地已有 `~/dev/gradle-7.6.3-bin.zip`，setup-gradle 可直接使用
