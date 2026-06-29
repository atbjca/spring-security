# 快速入门指南

## 环境要求

| 组件 | 版本 |
|------|------|
| JDK | 17+（推荐 `sdk use java 17.0.17-amzn`） |
| Gradle | 8.14.5（wrapper 已配置本地路径） |
| Nexus 凭证 | 配置于 `~/.gradle/gradle.properties` |

## 构建命令

```bash
# 查看可用命令
make help

# 快速编译（跳过测试）
make build-thin

# 运行单元测试
make test

# 发布到本地 ~/.m2
make install
```

## 下游依赖示例

```xml
<dependency>
    <groupId>cn.bjca.footstone.bpring.security</groupId>
    <artifactId>bjca-footstone-bpring-security-web</artifactId>
    <version>6.5.11-nes.patch.1-SNAPSHOT</version>
</dependency>
```

在 `dependencyManagement` 中引入 BOM：

```xml
<dependency>
    <groupId>cn.bjca.footstone.bpring.security</groupId>
    <artifactId>bjca-footstone-bpring-security-bom</artifactId>
    <version>6.5.11-nes.patch.1-SNAPSHOT</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

## 常见问题

- **Gradle 下载慢**：wrapper 已指向 `file:///Users/anan/dev/gradle-8.14.5-bin.zip`
- **Nexus 认证失败**：检查 `~/.gradle/gradle.properties` 中的 `nexusUsername` / `nexusPassword`
- **测试 flaky**：`DPoPProofJwtDecoderFactoryTests` 偶发失败，单独重跑即可
