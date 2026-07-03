## 1. 禁用不兼容的测试用例

- [x] 1.1 在 `GlobalMethodSecurityBeanDefinitionParserTests.java` 中添加 `import org.junit.jupiter.api.Disabled`
- [x] 1.2 在 `worksWithoutTargetOrClass()` 方法上添加 `@Disabled("HttpInvokerProxyFactoryBean 已在定制的 bjca-footstone-bpring-web 中移除")` 注解及中文说明注释

## 2. 验收

- [x] 2.1 单独运行该测试确认显示 skipped 而非 failed — BUILD SUCCESSFUL
- [x] 2.2 运行 `:config:test` 确认全部通过 — BUILD SUCCESSFUL（无 FAILED）
- [x] 2.3 运行完整 `make build` 确认 BUILD SUCCESSFUL — 429 tasks, 0 failures
