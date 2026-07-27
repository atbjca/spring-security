# 用户手册

## 1. 项目概述

本项目基于 Spring Security 6.5.11 维护分支，进行内部补丁维护，主要目标：

1. **CVE 安全评估**：记录已知漏洞的适用性与处置结果
2. **GAV 去特征化**：重命名 Maven 坐标以规避 SCA 工具误报
3. **Nexus 私服发布**：统一依赖下载与构件发布渠道

## 2. 版本与坐标

| 项 | 值 |
|---|---|
| 发布版本 | `6.5.11-nes.patch.1` |
| GroupId | `cn.bjca.footstone.bpring.security` |
| 运行时展示版本 | `6.5.11`（`gradle.properties` 的 `springSecurityVersion`，构建时注入 `getVersion()`） |
| Spring Framework | `6.2.19-nes.patch.1`（已发布的内部 RELEASE） |

代表性坐标为
`cn.bjca.footstone.bpring.security:bjca-footstone-bpring-security-core:6.5.11-nes.patch.1`；
下游应优先导入
`cn.bjca.footstone.bpring.security:bjca-footstone-bpring-security-bom:6.5.11-nes.patch.1`。
完整坐标转换见 `doc/GAV_MAPPING.md`。Maven 坐标的重命名不改变
`org.springframework.security` Java package。

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

RELEASE 流程严格按依赖拓扑执行：先验证 Spring Framework RELEASE，再复用未被
源码/测试变更失效的开发 build/test 证据，无 `clean` 地执行
`publishToMavenLocal -x test`、生成 POM/BOM 的内部 SNAPSHOT 扫描及
RELEASE-only 消费验证。只有这些门禁全部通过后才允许执行一次 Nexus 部署。
本版本没有显式发布排除项。

Nexus 主机为 `192.168.131.36:8088`，RELEASE 仓库名为 `releases`，路径为
`/repository/releases/`。项目通过 `nexusReleaseUrl` 获取完整地址，凭证只保存在
用户级 Gradle 配置中。

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
