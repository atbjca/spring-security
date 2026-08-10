# Spring Data Commons 2026 安全公告评估

## 基线与处置

- **升级前：** `org.springframework.data:spring-data-commons:3.4.13`
- **升级后：** `cn.bjca.footstone.bpring.data:bjca-footstone-bpring-data-commons:3.5.13-nes.patch.1`
- **Spring Framework：** 内部 `6.2.19-nes.patch.1`
- **处置结果：** 以下四个 Spring Data Commons CVE 均由 3.5.13 基线覆盖

内部 fork 保持 `org.springframework.data.*` Java 包与 `spring.data.commons` 模块名，依赖已替换为内部 Spring Framework GAV，因此下游源码无需迁移，也不会继续传递官方 Spring Core 6.2.15。

## CVE-2026-41721

- **严重性：** 8.2 HIGH
- **问题：** `MapDataBinder` 允许 SpEL 集合自动增长到攻击者指定的巨大索引，可能导致内存耗尽。
- **触发条件：** 不可信请求参数进入 `@ProjectedPayload` 等 Map 数据绑定路径。
- **修复：** Spring Data Commons 3.5.12 起限制集合自动增长；内部 3.5.13 已包含。

## CVE-2026-41716

- **严重性：** 7.5 HIGH
- **问题：** `TypeDiscoverer` 对攻击者控制的不同属性名进行无界负结果缓存，可能导致堆耗尽。
- **触发条件：** 大量互不相同的外部属性路径进入属性解析。
- **修复：** 3.5.12 起一次性初始化类型属性；内部 3.5.13 已包含并带有后续正确性修复。

## CVE-2026-41711

- **严重性：** 5.9 MEDIUM
- **问题：** `PropertyPath` 的 camel-case 拆分递归未统一应用最大深度限制，可能触发栈溢出。
- **触发条件：** 攻击者控制超长属性路径或排序字段。
- **修复：** 3.5.12 起对所有递归分支使用统一深度预算；内部 3.5.13 已包含。

## CVE-2026-41695

- **严重性：** HIGH
- **问题：** `PersistentPropertyPathFactory` 使用外部属性路径作为无界强引用缓存键，且缓存解析失败结果，可能导致内存耗尽。
- **触发条件：** 下游 Spring Data REST、存储映射器或应用代码将大量不同外部路径传给 MappingContext。
- **修复：** 内部 3.5.13 的 `PersistentPropertyPathFactory` 使用容量为 512 的 `ConcurrentLruCache`。

## 依赖验证

- Spring Security Data compile classpath 解析到内部 Spring Data Commons 3.5.13。
- 官方 `spring-data-commons:3.4.13` 不再出现。
- 官方 `spring-core:6.2.15` 不再出现，Spring Core 由内部 6.2.19 提供。
- Spring Data JPA 测试依赖排除了官方 Spring Data Commons 和官方 Spring Framework 实现。

## 测试

验证结果：

- `:bjca-footstone-bpring-security-data:test` 通过。
- `:bjca-footstone-bpring-security-config:test --tests 'org.springframework.security.config.annotation.method.configuration.aot.EnableMethodSecurityAotTests'` 通过。
- Gradle 输出为 `BUILD SUCCESSFUL`（84 actionable tasks）。
