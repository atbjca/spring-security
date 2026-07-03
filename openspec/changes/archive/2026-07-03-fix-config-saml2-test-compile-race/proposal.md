## Why

`config` 模块为隔离 SAML2/OpenSAML4 测试编译，引入了自定义任务 `compileSaml2TestJava`，它与标准 `compileTestJava` **写入同一个输出目录** `build/classes/java/test`，但两者之间**没有声明任务依赖**。在全量并行构建（`./gradlew build`）时，`testJar`、`test`、`checkstyleTest`、`checkstyleIntegrationTest`、`compileIntegrationTestJava` 与 `compileSaml2TestJava` 的执行次序不确定，导致 `config` 模块的 test 产物在竞态中被读到半成品状态：`config-*-test.jar` 未能正确生成或缺类。

下游 `test` 模块通过 `configuration: 'tests'` 消费该产物，因此编译时报 `org.springframework.security.config.users.AuthenticationTestConfiguration` 不存在，整个构建失败。这是一个**非确定性失败**——依赖 Gradle worker 调度顺序，时好时坏。Gradle 本身已在日志中明确指认了缺失的隐式依赖（`testJar uses this output of task compileSaml2TestJava without declaring an explicit or implicit dependency`）。

## What Changes

- 将 `compileSaml2TestJava` 的输出目录从共享的 `build/classes/java/test` 改为独立目录 `build/classes/java/saml2Test`
- 通过 `sourceSets.test.output.dir(<saml2Test 输出>, builtBy: compileSaml2TestJava)` 将该独立目录注册为 test source set 输出的一部分，使：
  - `testJar`（`from sourceSets.test.output`）自动打包 SAML2 测试类
  - 所有消费 `test.output` 的任务（`testJar` / `test` / `checkstyleTest` / `checkstyleIntegrationTest` / `compileIntegrationTestJava`）通过 `builtBy` 自动获得对 `compileSaml2TestJava` 的隐式依赖
- 移除或不再依赖对共享目录的写入重叠，消除 Gradle 报出的 5 条 "implicit dependency" 告警与 "Execution optimizations disabled" 兜底
- 保持 SAML2 测试仍从 config 的 testJar 中导出（下游若有消费者不受影响），且 `saml2Tests` 独立测试任务行为不变

## Capabilities

### New Capabilities
- `config-test-artifact`: 保证 `config` 模块的 test 产物（`-test.jar` / test source set 输出）在全量并行构建下完整、确定地包含全部测试类，包括经独立编译隔离的 SAML2 测试类

### Modified Capabilities

## Impact

- 受影响代码：`config/spring-security-config.gradle`（`compileSaml2TestJava` 任务定义、test source set 输出注册）
- 受影响任务：`:config:compileSaml2TestJava`、`:config:testJar`、`:config:test`、`:config:checkstyleTest`、`:config:checkstyleIntegrationTest`、`:config:compileIntegrationTestJava`
- 受影响下游：`test` 模块（`configuration: 'tests'` 消费者），以及任何依赖 `config` testJar 的模块
- 风险点：独立目录注册若遗漏 `builtBy`，会退回到缺依赖状态；需验证 SAML2 测试类确实进入 testJar 且 `saml2Tests` 任务仍能定位到类
- 非目标：不改用完整独立 `saml2Test` sourceSet（upstream 后续做法），不改动 SAML2 测试的编译隔离意图本身
