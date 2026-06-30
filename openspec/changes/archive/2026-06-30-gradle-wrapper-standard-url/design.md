# 设计：Gradle Wrapper URL 标准化

## 概述

将 Gradle Wrapper 配置从非标准的 `file://` 本地路径改为标准 `https://` 远程路径，并通过 `setup-gradle` 脚本预缓存本地 zip 文件，实现与 spring-boot-3.5 完全一致的行为。

## 架构设计

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           Gradle Wrapper 调用流程                            │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  用户执行 make [命令]                                                        │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────┐                                                        │
│  │  make setup-gradle │ ──▶ 扫描 ~/dev/gradle-*-zip                      │
│  └────────┬────────┘        │                                             │
│           │                  ▼                                             │
│           │         复制 zip 到 ~/.gradle/wrapper/dists/                   │
│           │         解压 + 创建 .ok 标记                                    │
│           │                  │                                             │
│           ▼                  ▼                                             │
│  ┌─────────────────┐   ┌─────────────────────────┐                          │
│  │ ./gradlew ...   │   │  ~/.gradle/wrapper/dists │                          │
│  └────────┬────────┘   │  └── gradle-8.14.5-bin  │                          │
│           │             │      └── {hash}/        │                          │
│           │             │          ├── gradle-8.14.5/   (已解压)          │
│           │             │          └── gradle-8.14.5-bin.zip.ok             │
│           │             └─────────────────────────┘                          │
│           ▼                                                                  │
│  Gradle Wrapper 检查缓存 ──▶ 已就绪 ──▶ 直接使用，无网络请求                │
│                              未就绪 ──▶ 从 https://services.gradle.org 下载  │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

## 文件变更

### 1. `gradle/wrapper/gradle-wrapper.properties`

**变更前**：
```properties
distributionUrl=file\:///Users/anan/dev/gradle-8.14.5-bin.zip
validateDistributionUrl=false
```

**变更后**：
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.14.5-bin.zip
validateDistributionUrl=true
```

### 2. `scripts/setup-gradle-local.sh`（新建）

从 spring-boot-3.5 复制，保留 Python 实现（计算 Gradle Wrapper 缓存路径 hash）。

**核心逻辑**：
1. 扫描 `LOCAL_GRADLE_DIR`（默认 `~/dev`）下的 `gradle-*-bin.zip` 和 `gradle-*-all.zip`
2. 对每个 zip，计算其对应的 Gradle Wrapper 缓存目录 hash
3. 复制 zip 到 `~/.gradle/wrapper/dists/{dist-name}/{hash}/`
4. 解压并创建 `.zip.ok` 标记

**Hash 计算规则**：
- URL: `https://services.gradle.org/distributions/gradle-{version}-{type}.zip`
- Hash: MD5(URL) → BigInteger → Base36
- 这是 Gradle Wrapper 的标准 PathAssembler 逻辑

### 3. `Makefile`

**新增 target**：
```makefile
setup-gradle: ## 安装并解压 LOCAL_GRADLE_DIR 下全部 Gradle zip（默认 ~/dev）
	LOCAL_GRADLE_DIR="$(LOCAL_GRADLE_DIR)" UNPACK=1 ./scripts/setup-gradle-local.sh
```

**修改现有 target 依赖**：
- `clean`、`test`、`test-all`、`build`、`build-thin`、`install`、`deploy`、`projects` 均添加 `setup-gradle` 为依赖

**更新 help 信息**：
```makefile
	@echo "  make setup-gradle - 安装本地 Gradle zip 到 wrapper 缓存（默认 ~/dev）"
```

## 行为对比

| 场景 | 变更前（file://） | 变更后（https + setup-gradle） |
|------|-------------------|--------------------------------|
| 首次构建 | 直接使用本地 zip | 需先执行 `make setup-gradle`，或等待网络下载 |
| 有 ~/dev/gradle-8.14.5-bin.zip | ✅ 即刻可用 | ✅ 执行 `make setup-gradle` 后可用 |
| 无本地 zip | ❌ 失败 | ✅ 自动从网络下载 |
| 其他机器/CI | ❌ 失败（路径不存在） | ✅ 网络下载（标准行为） |
| validateDistributionUrl | false（非标准） | true（标准行为） |

## 验证方案

1. `make setup-gradle` — 验证本地 zip 被正确安装到缓存
2. `./gradlew --version` — 验证 Gradle 正常运行
3. `ls ~/.gradle/wrapper/dists/gradle-8.14.5-bin/` — 验证缓存目录存在
