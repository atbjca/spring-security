## Why

`config` 模块的测试 `GlobalMethodSecurityBeanDefinitionParserTests.worksWithoutTargetOrClass()` 在 XML 配置中使用了 `org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean` 类。该类属于上游 Spring Framework `spring-web` 模块的 `remoting.httpinvoker` 包，但在定制版 `bjca-footstone-bpring-web` 中已被移除，导致测试运行时抛出 `ClassNotFoundException`，进而以 `CannotLoadBeanClassException` 报错，使整个 `config:test` 任务失败。

该失败在修改 saml2 编译竞态之前就已存在（通过 `git stash` 回退验证确认），属于**预先存在的兼容性问题**，与本分支的其它修改无关。

## What Changes

- 在 `GlobalMethodSecurityBeanDefinitionParserTests.worksWithoutTargetOrClass()` 方法上添加 `@Disabled` 注解，标注原因为定制 Spring 中已移除 `HttpInvokerProxyFactoryBean`
- 不修改测试逻辑本身，保留原始代码以便将来参考或上游对齐

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无）

## Impact

- 受影响代码：`config/src/test/java/org/springframework/security/config/method/GlobalMethodSecurityBeanDefinitionParserTests.java`（仅添加注解，不改逻辑）
- 受影响任务：`:config:test`（从 2447 个测试变为 2446 个，跳过 1 个）
- 受影响下游：无。该测试为内部单元测试，不导出、不被其它模块引用
- 风险点：无功能风险。唯一风险是若将来 `bjca-footstone-bpring-web` 恢复了 `HttpInvokerProxyFactoryBean`，需记得重新启用此测试
- 非目标：不尝试将测试改写为使用其它 FactoryBean 实现（如 `HessianProxyFactoryBean`），因为原测试的语义是验证 SEC-936（httpinvoker 场景下的无 target/class 安全拦截），替换类会偏离原始意图
