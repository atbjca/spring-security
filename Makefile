.PHONY: clean build build-thin install deploy stop projects help setup-gradle

# 初始化 sdkman 并切换到 Java 11（buildSrc 需要 Java 11+）
# 主项目代码仍通过 Gradle Toolchain 使用 Java 8 编译
SHELL := /bin/bash
JAVA_INIT := source "$(HOME)/.sdkman/bin/sdkman-init.sh" && sdk use java 11.0.29-amzn > /dev/null &&

# Gradle 本地缓存配置：扫描 ~/dev/gradle-*-zip 并解压到 Wrapper 缓存
LOCAL_GRADLE_DIR ?= $(HOME)/dev
SETUP_GRADLE := ./scripts/setup-gradle-local.sh

# 显示帮助信息
help:
	@echo ""
	@echo "可用命令:"
	@echo "  make setup-gradle - 安装并解压 LOCAL_GRADLE_DIR 下全部 gradle-*-zip 到 wrapper 缓存；缺包则回退联网下载"
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
# 注：跳过 integrationTest（Spring Security 命名空间解析测试环境问题）
# 注：跳过 docs:api（remoting 模块引用了不存在的 Spring API）
build: clean
	$(JAVA_INIT) ./gradlew build -x integrationTest -x :bjca-footstone-bpring-security-docs:api -PbuildSrc.skipTests=true

# 快速构建：跳过测试、集成测试、代码格式检查（checkstyle/checkFormat）和文档模块
# 适用于日常开发验证编译是否通过
# 注：-x checkstyleNohttp 跳过 nohttp 检查（输入文件在初次构建后才会存在）
# 注：-x integrationTest 跳过集成测试（-x test 不会自动跳过自定义的 integrationTest 任务）
build-thin: clean
	$(JAVA_INIT) ./gradlew build -x test -x integrationTest -x checkstyleMain -x checkstyleTest -x checkFormatMain -x checkFormatTest -x :bjca-footstone-bpring-security-docs:antora -x :bjca-footstone-bpring-security-docs:docs -x javadoc -x checkstyleNohttp -PbuildSrc.skipTests=true

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

# 安装并解压 LOCAL_GRADLE_DIR 下全部 Gradle zip（默认 ~/dev）到 Wrapper 缓存
# 首次运行后，./gradlew 可在离线模式下使用（基于本地缓存）
# -----------------------------------------------------------------------------
# setup-gradle：构建前预热 Gradle 发行包（几乎所有 target 的前置依赖）
#
# 为什么这么做：
#   Gradle Wrapper 首次运行会按 gradle-wrapper.properties 里的 distributionUrl
#   联网从 services.gradle.org 下载发行包。在内网/离线/弱网环境下这一步很慢或
#   直接失败。本 target 调用 scripts/setup-gradle-local.sh，把本地已备好的
#   gradle-*-{bin,all}.zip 按 Wrapper 的缓存命名规则（MD5(url)->base36）直接
#   复制解压到 ~/.gradle/wrapper/dists/，模拟“首次下载已完成”，从而免联网。
#   因为用的是官方 URL 算 hash，gradle-wrapper.properties 无需改成 file://。
#
# 从哪里找包：
#   默认扫描 LOCAL_GRADLE_DIR（缺省 ~/dev）下的 gradle-*-{bin,all}.zip。
#   可覆盖：LOCAL_GRADLE_DIR=/path/to/zips make <target>
#
# 会产生什么效果：
#   - 找到本地包：免网络注入 Wrapper 缓存，构建直接用本地发行包（幂等，已就绪则跳过）。
#   - 找不到本地包：不再中断构建，仅打印提示并以退出码 0 继续，交回 Gradle Wrapper
#     按官方 distributionUrl 联网下载。（旧行为是 exit 1 直接让 make 失败。）
#
# 注意：
#   “缺包回退联网下载”依赖能访问 services.gradle.org。若既无本地包又完全离线，
#   下载会在 Gradle 自身阶段失败——此时请补齐本地包或设置 LOCAL_GRADLE_DIR。
# -----------------------------------------------------------------------------
setup-gradle:
	LOCAL_GRADLE_DIR="$(LOCAL_GRADLE_DIR)" UNPACK=1 "$(SETUP_GRADLE)"
