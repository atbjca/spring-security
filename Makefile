.PHONY: clean build build-thin test test-all install deploy stop projects help

# 初始化 sdkman 并切换到 Java 17（Spring Security 6.5.x 要求 JDK 17+）
SHELL := /bin/bash
JAVA_INIT := source "$(HOME)/.sdkman/bin/sdkman-init.sh" && sdk use java 17.0.17-amzn > /dev/null &&

help:
	@echo ""
	@echo "可用命令:"
	@echo "  make clean      - 清理构建产物"
	@echo "  make test       - 运行单元测试（./gradlew test）"
	@echo "  make test-all   - 运行单元测试 + 集成测试"
	@echo "  make build-thin - 快速构建（跳过测试、文档、代码检查）"
	@echo "  make build      - 全量构建（含测试与代码检查）"
	@echo "  make install    - 发布到本地 Maven 仓库（~/.m2），跳过测试"
	@echo "  make deploy     - 发布到 Nexus 私服，跳过测试"
	@echo "  make stop       - 停止所有 Gradle Daemon"
	@echo "  make projects   - 查看所有子项目"
	@echo ""

clean:
	$(JAVA_INIT) ./gradlew clean

test:
	$(JAVA_INIT) ./gradlew test

test-all:
	$(JAVA_INIT) ./gradlew test integrationTest

build-thin: clean
	$(JAVA_INIT) ./gradlew build -x test -x integrationTest -x checkstyleMain -x checkstyleTest -x checkFormatMain -x checkFormatTest -x :bjca-footstone-bpring-security-docs:antora -x :bjca-footstone-bpring-security-docs:docs -x javadoc -x checkstyleNohttp

build: clean
	$(JAVA_INIT) ./gradlew build

install:
	$(JAVA_INIT) ./gradlew clean publishToMavenLocal -x test

deploy:
	$(JAVA_INIT) ./gradlew clean publishAllPublicationsToNexusRepository -x test

stop:
	$(JAVA_INIT) ./gradlew --stop

projects:
	$(JAVA_INIT) ./gradlew projects
