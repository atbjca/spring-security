## 1. 配置变更

- [x] 1.1 修改 `gradle/wrapper/gradle-wrapper.properties`
  - 将 `distributionUrl` 改为 `https\://services.gradle.org/distributions/gradle-7.6.3-bin.zip`
  - 添加 `networkTimeout=10000`
  - 添加 `validateDistributionUrl=true`

## 2. 脚本迁移

- [x] 2.1 从 `spring-boot-3.5` 复制 `scripts/setup-gradle-local.sh` 到当前项目
- [x] 2.2 确保脚本有执行权限（`chmod +x`）

## 3. Makefile 更新

- [x] 3.1 在 `.PHONY` 添加 `setup-gradle`
- [x] 3.2 添加 `LOCAL_GRADLE_DIR ?= $(HOME)/dev` 变量定义
- [x] 3.3 添加 `SETUP_GRADLE := ./scripts/setup-gradle-local.sh` 变量定义
- [x] 3.4 添加 `setup-gradle` target 调用脚本

## 4. 验证

- [x] 4.1 执行 `make setup-gradle` 验证本地缓存可用
- [x] 4.2 执行 `./gradlew --version` 验证 gradle 可用
