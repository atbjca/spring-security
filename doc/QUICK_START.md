# 快速入门指南

## 环境要求

| 组件 | 版本 |
|------|------|
| JDK | 17+（推荐 `sdk use java 17.0.17-amzn`） |
| Gradle | 8.14.5（wrapper 已配置本地路径） |
| Nexus 凭证 | 配置于 `~/.gradle/gradle.properties` |

当前 RELEASE 为 `6.5.11-nes.patch.1`。构建前必须先确认内部上游
`cn.bjca.footstone.bpring:bjca-footstone-bpring-framework-bom:6.2.19-nes.patch.1`
已在 Nexus RELEASE 可解析；不得用对应 SNAPSHOT 替代。

## 构建命令

```bash
# 查看可用命令
make help

# 快速编译（跳过测试）
make build-thin

# 运行单元测试
make test

# 增量发布到本地 ~/.m2（RELEASE 校验不执行 clean 或 test）
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 ./gradlew publishToMavenLocal -x test -PbuildSrc.skipTests=true --max-workers=3

# 本地安装后扫描生成的 POM；任何内部 SNAPSHOT 都会阻塞 deploy
rg -n 'cn\.bjca\.footstone|-SNAPSHOT' ~/.m2/repository/cn/bjca/footstone/bpring/security
```

正式发布门禁复用已批准且未被源码变更失效的开发 build/test 证据，然后执行
增量本地发布、生成 POM 扫描和 RELEASE-only 消费验证，最后才可授权 Nexus
部署。Nexus 主机为
`192.168.131.36:8088`，仓库名为 `releases`，路径为 `/repository/releases/`；
项目通过 `nexusReleaseUrl` 使用该地址。当前没有显式发布排除项。

## 下游依赖示例

```xml
<dependency>
    <groupId>cn.bjca.footstone.bpring.security</groupId>
    <artifactId>bjca-footstone-bpring-security-web</artifactId>
    <version>6.5.11-nes.patch.1</version>
</dependency>
```

在 `dependencyManagement` 中引入 BOM：

```xml
<dependency>
    <groupId>cn.bjca.footstone.bpring.security</groupId>
    <artifactId>bjca-footstone-bpring-security-bom</artifactId>
    <version>6.5.11-nes.patch.1</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

Java 源码中的 `org.springframework.security` package 与 `import` 不变。

## 常见问题

- **Gradle 下载慢**：wrapper 已指向 `file:///Users/anan/dev/gradle-8.14.5-bin.zip`
- **Nexus 认证失败**：检查 `~/.gradle/gradle.properties` 中的 `nexusUsername` / `nexusPassword`
- **测试 flaky**：`DPoPProofJwtDecoderFactoryTests` 偶发失败，单独重跑即可
