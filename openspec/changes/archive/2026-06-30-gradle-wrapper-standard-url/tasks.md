# 任务：Gradle Wrapper URL 标准化

## 1. 脚本创建

- [x] 1.1 创建 `scripts/setup-gradle-local.sh`，从 spring-boot-3.5 复制并翻译注释为中文

## 2. 配置修改

- [x] 2.1 修改 `gradle/wrapper/gradle-wrapper.properties`：将 `distributionUrl` 改为 https URL，`validateDistributionUrl` 改为 `true`

## 3. Makefile 更新

- [x] 3.1 在 `.PHONY` 中添加 `setup-gradle`
- [x] 3.2 新增 `setup-gradle` target
- [x] 3.3 在 `clean`、`test`、`test-all`、`build`、`build-thin`、`install`、`deploy`、`projects` 依赖中添加 `setup-gradle`
- [x] 3.4 在 help 文本中添加 `setup-gradle` 说明

## 4. 验证

- [x] 4.1 执行 `make setup-gradle` 确认脚本正常工作
- [x] 4.2 执行 `./gradlew --version` 确认 Gradle 能正常使用
- [x] 4.3 检查 `~/.gradle/wrapper/dists/gradle-8.14.5-bin/` 缓存目录
