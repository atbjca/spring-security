## ADDED Requirements

### Requirement: 本地 Gradle zip 缓存支持

系统 SHALL 支持通过本地 Gradle zip 文件预填充 Gradle Wrapper 缓存，使后续 `./gradlew` 命令可在离线模式下运行。

#### Scenario: setup-gradle 成功执行
- **WHEN** 用户执行 `make setup-gradle`
- **THEN** 系统扫描 `~/dev/gradle-7.6.3-bin.zip` 并复制到 `~/.gradle/wrapper/dists/gradle-7.6.3-bin/`
- **AND** 系统解压 zip 文件到目标目录
- **AND** 系统创建 `.ok` 标记文件

#### Scenario: 已缓存时跳过解压
- **WHEN** 用户执行 `make setup-gradle` 且缓存已存在
- **THEN** 系统检测到 `.ok` 标记文件存在
- **AND** 系统跳过复制和解压步骤
- **AND** 系统输出 "已就绪: gradle-7.6.3-bin"

#### Scenario: gradle-wrapper 使用 https URL
- **WHEN** `gradle-wrapper.properties` 配置 `distributionUrl=https://services.gradle.org/distributions/gradle-7.6.3-bin.zip`
- **THEN** Gradle Wrapper 优先使用本地缓存（如存在）
- **OR** 通过网络下载（如缓存不存在）

#### Scenario: 网络超时配置
- **WHEN** Gradle Wrapper 需要下载 Gradle
- **THEN** 网络超时时间 SHALL 为 10000 毫秒
- **AND** 启用 URL 验证
