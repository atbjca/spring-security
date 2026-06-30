.PHONY: setup-gradle clean build build-thin test test-all install deploy stop projects help

# =============================================================================
# Spring Security 6.5.x BJCA 维护分支 — 构建快捷命令
# =============================================================================
# 初始化 sdkman 并切换到 Java 17（Spring Security 6.5.x 要求 JDK 17+）
SHELL := /bin/bash
JAVA_INIT := source "$(HOME)/.sdkman/bin/sdkman-init.sh" && sdk use java 17.0.17-amzn > /dev/null &&

help:
	@echo ""
	@echo "可用命令:"
	@echo "  make setup-gradle - 安装本地 Gradle zip 到 wrapper 缓存（默认 ~/dev）"
	@echo "  make clean      - 清理构建产物"
	@echo "  make test       - 运行单元测试（./gradlew test，不启动外部服务）"
	@echo "  make test-all   - 运行单元测试 + 集成测试（含嵌入式 LDAP 等）"
	@echo "  make build-thin - 快速构建（跳过测试、文档、代码检查）"
	@echo "  make build      - 全量构建（含测试与 checkstyle 等检查）"
	@echo "  make install    - 发布到本地 Maven 仓库（~/.m2），跳过测试"
	@echo "  make deploy     - 发布到 Nexus 私服，跳过测试"
	@echo "  make stop       - 停止所有 Gradle Daemon，释放内存与文件锁"
	@echo "  make projects   - 查看所有子项目（含 GAV 重命名后的 project.name）"
	@echo ""

# 安装并解压 LOCAL_GRADLE_DIR 下全部 Gradle zip（默认 ~/dev）
# 预缓存后 ./gradlew 自动使用本地分发包，跳过网络下载
setup-gradle:
	LOCAL_GRADLE_DIR="$(LOCAL_GRADLE_DIR)" UNPACK=1 ./scripts/setup-gradle-local.sh

# 清理所有子模块的 build 目录
clean: setup-gradle
	$(JAVA_INIT) ./gradlew clean

# 单元测试：各模块 src/test/ 下的 JUnit 测试，内存 Mock，不依赖外部中间件
# 注：build.gradle 已为 Test 任务统一设置 user.language=en，避免 XSD 校验中文消息导致断言失败
test: setup-gradle
	$(JAVA_INIT) ./gradlew test

# 集成测试：ldap、itest-web 等模块的 integrationTest 任务，依赖嵌入式 LDAP 等环境
test-all: setup-gradle
	$(JAVA_INIT) ./gradlew test integrationTest

# 快速构建：跳过测试、集成测试、代码格式检查及文档模块，用于日常编译验证
# 注：-x checkstyleNohttp 跳过 nohttp（首次构建前输入文件可能不存在）
# 注：docs 模块在 GAV 重命名后为 bjca-footstone-bpring-security-docs
build-thin: clean
	$(JAVA_INIT) ./gradlew build -x test -x integrationTest -x checkstyleMain -x checkstyleTest -x checkFormatMain -x checkFormatTest -x :bjca-footstone-bpring-security-docs:antora -x :bjca-footstone-bpring-security-docs:docs -x javadoc -x checkstyleNohttp

# 全量构建：含单元测试、checkstyle、checkFormat 等全部验证任务
build: clean setup-gradle
	$(JAVA_INIT) ./gradlew build

# 发布到本地 Maven 仓库，供同机其他项目依赖调试（跳过测试以加快速度）
install: setup-gradle
	$(JAVA_INIT) ./gradlew clean publishToMavenLocal -x test

# 发布到 Nexus 私服；使用 publishAllPublicationsToNexusRepository 精确指定 Nexus 仓库
deploy: setup-gradle
	$(JAVA_INIT) ./gradlew clean publishAllPublicationsToNexusRepository -x test

stop:
	$(JAVA_INIT) ./gradlew --stop

projects: setup-gradle
	$(JAVA_INIT) ./gradlew projects
