.PHONY: clean build build-thin test test-all stop projects help

# 初始化 sdkman 并切换到 Java 17（Spring Security 6.5.x 要求 JDK 17+）
SHELL := /bin/bash
JAVA_INIT := source "$(HOME)/.sdkman/bin/sdkman-init.sh" && sdk use java 17.0.17-amzn > /dev/null &&

help:
	@echo ""
	@echo "可用命令:"
	@echo "  make clean      - 清理构建产物"
	@echo "  make test       - 运行单元测试（./gradlew test，内存中 Mock，较快）"
	@echo "  make test-all   - 运行单元测试 + 集成测试（需真实 LDAP/容器，较慢）"
	@echo "  make build-thin - 快速构建（跳过测试、文档、代码检查）"
	@echo "  make build      - 全量构建（含测试与代码检查，耗时最长）"
	@echo "  make stop       - 停止所有 Gradle Daemon"
	@echo "  make projects   - 查看所有子项目"
	@echo ""

clean:
	$(JAVA_INIT) ./gradlew clean

# 单元测试：各模块 src/test 下的 JUnit 测试，不启动外部服务
test:
	$(JAVA_INIT) ./gradlew test

# 集成测试：ldap、itest-web 等模块的 integrationTest，依赖嵌入式 LDAP 等环境
test-all:
	$(JAVA_INIT) ./gradlew test integrationTest

build-thin: clean
	$(JAVA_INIT) ./gradlew build -x test -x integrationTest -x checkstyleMain -x checkstyleTest -x checkFormatMain -x checkFormatTest -x :spring-security-docs:antora -x :spring-security-docs:docs -x javadoc -x checkstyleNohttp

build: clean
	$(JAVA_INIT) ./gradlew build

stop:
	$(JAVA_INIT) ./gradlew --stop

projects:
	$(JAVA_INIT) ./gradlew projects
