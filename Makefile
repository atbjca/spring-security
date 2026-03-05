.PHONY: clean build build-thin install deploy stop projects help

# 初始化 sdkman 并切换到 Java 11（buildSrc 需要 Java 11+）
# 主项目代码仍通过 Gradle Toolchain 使用 Java 8 编译
SHELL := /bin/bash
JAVA_INIT := source "$(HOME)/.sdkman/bin/sdkman-init.sh" && sdk use java 11.0.29-amzn > /dev/null &&

# 显示帮助信息
help:
	@echo ""
	@echo "可用命令:"
	@echo "  make clean      - 清理构建产物"
	@echo "  make build-thin - 快速构建（跳过测试、文档、代码检查）"
	@echo "  make install    - 发布到本地 Maven 仓库（~/.m2/repository），跳过测试"
	@echo "  make deploy     - 发布到 Nexus 私服，跳过测试"
	@echo "  make stop       - 停止所有 Gradle Daemon"
	@echo "  make projects   - 查看所有子项目"
	@echo "  make build      - 全量构建（含测试）"
	@echo ""

# 清理构建产物
clean:
	$(JAVA_INIT) ./gradlew clean

# 全量构建（含测试，耗时较长）
build: clean
	$(JAVA_INIT) ./gradlew build

# 快速构建：跳过测试、集成测试、代码格式检查（checkstyle/checkFormat）和文档模块
# 适用于日常开发验证编译是否通过
# 注：-x checkstyleNohttp 跳过 nohttp 检查（输入文件在初次构建后才会存在）
# 注：-x integrationTest 跳过集成测试（-x test 不会自动跳过自定义的 integrationTest 任务）
build-thin: clean
	$(JAVA_INIT) ./gradlew build -x test -x integrationTest -x checkstyleMain -x checkstyleTest -x checkFormatMain -x checkFormatTest -x :bjca-footstone-bpring-security-docs:antora -x :bjca-footstone-bpring-security-docs:docs -x javadoc -x checkstyleNohttp

# 发布到本地 Maven 仓库（~/.m2/repository）
# 跳过测试，供本地其他项目依赖调试使用
install:
	$(JAVA_INIT) ./gradlew clean publishToMavenLocal -x test

# 发布到 Nexus 私服（根据版本号自动选择 snapshot 或 release 仓库）
# 使用 publishAllPublicationsToNexusRepository 精确指定 Nexus 仓库，避免发布到 OSSRH 等其他仓库
deploy:
	$(JAVA_INIT) ./gradlew clean publishAllPublicationsToNexusRepository -x test

# 停止所有 Gradle Daemon 进程，释放内存和文件锁
stop:
	$(JAVA_INIT) ./gradlew --stop

# 查看所有子项目列表
projects:
	$(JAVA_INIT) ./gradlew projects
