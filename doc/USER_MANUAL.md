# 用户手册

## 1. 项目概述

本项目基于 Spring Security 6.5.11 维护分支，进行内部补丁维护，主要目标：

1. **CVE 安全评估**：记录已知漏洞的适用性与处置结果
2. **GAV 去特征化**：重命名 Maven 坐标以规避 SCA 工具误报
3. **Nexus 私服发布**：统一依赖下载与构件发布渠道

## 2. 版本与坐标

| 项 | 值 |
|---|---|
| 发布版本 | `6.5.11-nes.patch.1-SNAPSHOT` |
| GroupId | `cn.bjca.footstone.bpring.security` |
| 运行时伪装版本 | `6.5.11`（`SpringSecurityCoreVersion.getVersion()`） |
| Spring Framework | `6.2.19-nes.patch.1-SNAPSHOT`（内部 fork） |

## 3. Makefile 命令参考

| 命令 | 说明 |
|------|------|
| `make clean` | 清理构建产物 |
| `make build-thin` | 快速构建，跳过测试和代码检查 |
| `make test` | 运行单元测试 |
| `make test-all` | 单元测试 + 集成测试 |
| `make build` | 全量构建 |
| `make install` | 发布到 `~/.m2/repository` |
| `make deploy` | 发布到 Nexus 私服 |
| `make stop` | 停止 Gradle Daemon |

## 4. 迁移指南

从官方 Spring Security 迁移到本内部版本：

1. 在 `pom.xml` / `build.gradle` 中替换 GAV 坐标（参见 `doc/GAV_MAPPING.md`）
2. 同步替换 Spring Framework BOM 为内部 fork 坐标
3. **无需修改** Java 源代码中的 `import` 语句
4. 确认 `dependencyManagement` 中 BOM 版本一致

## 5. 相关文档

- [快速入门](QUICK_START.md)
- [Nexus 发布配置](NEXUS_DEPLOY.md)
- [GAV 映射表](GAV_MAPPING.md)
- [需求与 CVE 清单](REQUIREMENTS.md)
