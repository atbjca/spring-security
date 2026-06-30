# 提案：Gradle Wrapper URL 标准化

## 状态
- **提议日期**：2026-06-30
- **提议人**：BJCA 维护团队
- **当前状态**：已提议

## 背景与问题

当前 `gradle/wrapper/gradle-wrapper.properties` 使用硬编码的 `file://` 协议本地路径：

```properties
distributionUrl=file:///Users/anan/dev/gradle-8.14.5-bin.zip
validateDistributionUrl=false
```

**问题**：
1. 路径硬编码为 `/Users/anan/dev/`，绑定到特定开发者的 home 目录
2. 其他开发者或 CI 环境无法直接使用
3. `validateDistributionUrl=false` 是非标准 workaround
4. 偏离了 Gradle Wrapper 的设计意图

## 参考方案

参考 `spring-boot-3.5` 项目的做法：
- `gradle-wrapper.properties` 使用标准 https URL
- 提供 `setup-gradle` 脚本，将本地 zip 预安装到 `~/.gradle/wrapper/dists/`
- Gradle Wrapper 检测到缓存已就绪时自动跳过网络下载

## 提案

采用与 spring-boot-3.5 完全一致的方式：

1. **`gradle-wrapper.properties`**：改回标准 https URL
2. **新增 `scripts/setup-gradle-local.sh`**：从 `~/dev` 扫描 gradle-*-zip 并预安装到 wrapper 缓存
3. **更新 `Makefile`**：所有构建命令依赖 `setup-gradle`，保持向后兼容

## 非目标（Non-goals）

- 不修改 Gradle 版本（维持 8.14.5）
- 不修改任何 Gradle 构建逻辑或依赖配置
- 不提供 Windows 环境支持（仅 macOS/Linux）

## 影响范围

| 模块 | 影响 |
|------|------|
| `gradle/wrapper/gradle-wrapper.properties` | 修改 URL 和 validateDistributionUrl |
| `Makefile` | 增加 setup-gradle target 及依赖 |
| `scripts/setup-gradle-local.sh` | 新增文件 |

## 风险评估

- **低风险**：配置标准化，无破坏性变更
- **回滚方案**：恢复 `gradle-wrapper.properties` 的 file:// URL 即可
