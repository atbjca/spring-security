## Context

当前项目 `gradle/wrapper/gradle-wrapper.properties` 配置：

```
distributionUrl=file\:///Volumes/LIBIAO_EX/dev/gradle-7.6.3-bin.zip
```

`LIBIAO_EX` 卷不可访问，导致 `./gradlew` 命令失败。

参考项目 `spring-boot-3.5` 的解决方案：使用官方 https URL + 本地 zip 缓存。

## Goals / Non-Goals

**Goals:**
- 修复 gradle wrapper 配置，使 `./gradlew` 可正常工作
- 与 `spring-boot-3.5` 保持一致的构建配置方式
- 支持本地 zip 缓存，实现完全离线构建

**Non-Goals:**
- 不升级 Gradle 版本（保持 7.6.3）
- 不修改 Gradle Wrapper 本身的行为
- 不改变项目依赖管理方式

## Decisions

### Decision 1: 采用 https URL 而非 file URL

**选择**：使用 `https://services.gradle.org/distributions/gradle-7.6.3-bin.zip`

**理由**：
- `file://` 路径强耦合特定机器环境，不可移植
- https URL 让 Gradle Wrapper 自动处理下载，CI/CD 环境友好
- `spring-boot-3.5` 已采用此模式

**替代方案**：
- 继续使用 file URL + 恢复 LIBIAO_EX 卷 → 不可行，LIBIAO_EX 已下线

### Decision 2: 复用 `setup-gradle-local.sh` 脚本

**选择**：直接复制 `spring-boot-3.5/scripts/setup-gradle-local.sh`

**理由**：
- 脚本逻辑已成熟，无需重新开发
- 保持跨项目一致性
- 支持自定义 `LOCAL_GRADLE_DIR`

### Decision 3: Makefile target 命名

**选择**：`setup-gradle`（与 `spring-boot-3.5` 完全一致）

**理由**：
- 语义清晰：设置本地 Gradle 缓存
- 与 `spring-boot-3.5` 保持一致

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| `~/dev` 目录不存在或无 gradle zip | 脚本会报错并提示用户；用户需自行准备 |
| 网络下载慢或不稳定 | 脚本支持 UNPACK=0 仅复制 zip，由用户自行解压 |
| Gradle 版本升级需同步更新 | 需手动更新 `gradle-wrapper.properties` 和 `~/dev` 中的 zip |

## Migration Plan

1. **立即生效**：用户克隆项目后，执行 `make setup-gradle`
2. **脚本扫描** `~/dev/gradle-7.6.3-bin.zip` 并解压到 `~/.gradle/wrapper/dists/`
3. **后续** `./gradlew` 使用缓存，无需网络

**回滚**：
- 如有问题，还原 `gradle/wrapper/gradle-wrapper.properties` 和 `Makefile` 即可
- 已解压的缓存不影响（位于用户 home 目录）
