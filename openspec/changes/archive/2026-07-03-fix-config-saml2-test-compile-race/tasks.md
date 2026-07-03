## 1. 修复 config 模块的 SAML2 测试编译输出

- [x] 1.1 将 `config/spring-security-config.gradle` 中 `compileSaml2TestJava` 的 `destinationDirectory` 改为独立目录 `build/classes/java/saml2Test`
- [x] 1.2 新增 `sourceSets.test.output.dir(compileSaml2TestJava.destinationDirectory, builtBy: compileSaml2TestJava)`，将独立目录并入 test source set 输出并声明构建依赖
- [x] 1.3 核对 `saml2Tests` 任务的 `testClassesDirs` / classpath 覆盖新的 saml2Test 目录，确保 SAML2 用例仍可被发现

## 2. 消除 sourcepath 导致的重复编译

- [x] 2.1 删除 `options.sourcepath`，改为 `classpath = sourceSets.test.compileClasspath + files(compileTestJava.destinationDirectory)`，使 javac 从 .class 文件解析共享测试依赖而非重新编译源码
- [x] 2.2 添加 `dependsOn compileTestJava` 保证编译顺序
- [x] 2.3 确认 `testJar` 不再报 duplicate entry（`SecurityContextChangedListenerArgumentMatchers$1.class` 不再出现在 saml2Test 目录）

## 3. 验收

- [x] 3.1 执行 `make build` 确认 BUILD SUCCESSFUL — 429 tasks, 0 failures
- [x] 3.2 确认 saml2Test 目录中只包含 saml2 自身的类（`annotation/` 下仅剩 `web/` 子目录）
- [ ] 3.3 用 `:test:compileTestJava --dry-run` 确认 `compileSaml2TestJava` 前置于 `config:testJar`
- [ ] 3.4 运行 `:config:saml2Tests` 确认 SAML2 测试仍被发现并执行
- [ ] 3.5 连续两次 `clean build` 复现验证，确认非确定性失败不再出现

> 任务 3.3-3.5 可在后续执行进一步验证。
