## ADDED Requirements

### Requirement: config 模块 test 产物 MUST 在并行构建下完整包含 SAML2 测试类

`config` 模块的 test source set 输出与 `-test.jar` MUST 在全量并行构建下确定性地包含全部测试类，包括经 `compileSaml2TestJava` 独立编译的 SAML2 测试类，不得依赖任务执行次序。

#### Scenario: 全量并行构建生成 config test 产物

- **WHEN** 执行 `./gradlew build`（并行、按需配置）
- **THEN** `config` 的 `-test.jar` 包含 `org/springframework/security/config/users/AuthenticationTestConfiguration.class`
- **AND** `-test.jar` 包含 `org/springframework/security/config/annotation/web/configurers/saml2/**` 下的 SAML2 测试类
- **AND** 结果不随 Gradle worker 调度次序变化

#### Scenario: 下游 test 模块消费 config test 产物

- **WHEN** `test` 模块通过 `configuration: 'tests'` 消费 `config` 的 test 产物并编译 `compileTestJava`
- **THEN** `org.springframework.security.config.users.AuthenticationTestConfiguration` 可被解析
- **AND** `:test:compileTestJava` 编译成功

### Requirement: SAML2 测试编译 MUST NOT 与标准测试编译共享输出目录

`compileSaml2TestJava` 与 `compileTestJava` MUST NOT 写入同一输出目录，且所有消费 test source set 输出的任务 MUST 获得对 SAML2 编译任务的构建依赖。

#### Scenario: 检查任务依赖图

- **WHEN** 对消费 `config` test 输出的任务（`testJar`、`test`、`checkstyleTest`、`checkstyleIntegrationTest`、`compileIntegrationTestJava`）求依赖
- **THEN** 每个任务都隐式或显式依赖 `compileSaml2TestJava`
- **AND** 构建日志不再出现 "uses this output of task ':...:compileSaml2TestJava' without declaring an explicit or implicit dependency" 告警

#### Scenario: SAML2 独立测试任务仍可运行

- **WHEN** 执行 `:config:saml2Tests`
- **THEN** `org.springframework.security.config.annotation.web.configurers.saml2.*` 下的测试被发现并执行
