## Context

`GlobalMethodSecurityBeanDefinitionParserTests` 是 Spring Security `config` 模块中测试 `<global-method-security>` XML 命名空间解析器的集成测试类。其中 `worksWithoutTargetOrClass()` 方法（对应 SEC-936 issue）通过 `InMemoryXmlApplicationContext` 动态构造包含 `HttpInvokerProxyFactoryBean` 的 Spring 配置，验证在没有实际 target 类的代理场景下安全拦截仍能正常工作。

在定制分支中，`bjca-footstone-bpring-web` 已移除 `org.springframework.remoting.httpinvoker` 包（经 `jar -tf` 确认 jar 中不存在该包），该测试在运行时必然失败。

## Decisions

### 1. 使用 `@Disabled` 而非改写测试

- **原因**：`worksWithoutTargetOrClass()` 的核心语义是验证 `HttpInvokerProxyFactoryBean`（一个不暴露 target class 的 `FactoryBean`）场景下的安全拦截。若替换为 `HessianProxyFactoryBean` 等其它实现，测试的前提条件可能不同（如是否暴露 serviceInterface），偏离 SEC-936 的原始场景。`@Disabled` 保留了原始代码供将来参考。
- **备选方案**：使用 `@EnabledIf` 等条件注解动态检测类是否存在——但对于一个已知永久移除的类，条件判断是不必要的复杂性。

### 2. 注解中说明禁用原因

- **原因**：便于将来代码审查或上游对齐时快速理解为何跳过，无需翻阅提交历史。

## Risks / Trade-offs

- **[遗忘重新启用]** → 风险低。若 `bjca-footstone-bpring-web` 将来恢复该类，测试仍然存在只是被跳过，IDE 和测试报告会显示 skipped 计数，提醒开发者检查
