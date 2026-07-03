## Context

`config` 模块的测试源码里，SAML2 相关测试（`org/springframework/security/config/annotation/web/configurers/saml2/**` 与 `org/springframework/security/config/http/Saml2*`）依赖 OpenSAML4，需要与主 test 编译隔离。当前方案（见 `config/spring-security-config.gradle`）为：

```groovy
compileTestJava {
    exclude "org/springframework/security/config/annotation/web/configurers/saml2/**",
            "org/springframework/security/config/http/Saml2*"
}

task compileSaml2TestJava(type: JavaCompile) {
    source = sourceSets.test.java.srcDirs
    include "org/springframework/security/config/annotation/web/configurers/saml2/**",
            "org/springframework/security/config/http/Saml2*"
    classpath = sourceSets.test.compileClasspath
    destinationDirectory = new File("${buildDir}/classes/java/test")   // ← 与 compileTestJava 同目录
    options.sourcepath = sourceSets.test.java.getSourceDirectories()
}
```

`compileTestJava` 排除 SAML2 类、`compileSaml2TestJava` 补上这些类，两者拼出完整的 test 输出。问题在于二者写入**同一目录**却无先后约束。

## Goals / Non-Goals

**Goals:**
- 消除 `compileTestJava` 与 `compileSaml2TestJava` 之间的输出目录竞态，使全量并行构建下 `config` 的 test 产物确定完整
- 让 `testJar` 稳定包含全部 SAML2 测试类（下游 `test` 模块能编译通过）
- 消除 Gradle 报出的 5 条 implicit dependency 告警及 "Execution optimizations disabled" 兜底
- 保持 SAML2 编译隔离（独立 JDK11 toolchain 编译、`saml2Tests` 独立测试任务）语义不变

**Non-Goals:**
- 不迁移到完整独立的 `saml2Test` sourceSet（改动面大，留作后续演进）
- 不改变 SAML2 测试被排除/隔离的边界（include/exclude 模式保持一致）
- 不触碰其它模块的 testJar 约定

## Decisions

### 1. 采用「独立输出目录 + `sourceSets.test.output.dir(builtBy:)` 注册」，而非逐个补 `dependsOn`

- **原因**：竞态的根因是两个 `JavaCompile` 写同一目录。给 SAML2 编译独立目录能从根上消除写重叠；再用 `output.dir(dir, builtBy: compileSaml2TestJava)` 把该目录并入 `sourceSets.test.output`，则所有消费 `test.output` 的任务自动、隐式地依赖 SAML2 编译，一处声明覆盖全部消费者。
- **落地形态**：

  ```groovy
  task compileSaml2TestJava(type: JavaCompile) {
      // ...toolchain / source / include / classpath 不变...
      destinationDirectory = new File("${buildDir}/classes/java/saml2Test")   // 独立目录
      options.sourcepath = sourceSets.test.java.getSourceDirectories()
  }

  sourceSets.test.output.dir(
      compileSaml2TestJava.destinationDirectory,
      builtBy: compileSaml2TestJava
  )
  ```

- **效果连锁**：
  - `testJar { from sourceSets.test.output }` → 自动打包 saml2Test 目录里的类
  - `test` / `checkstyleTest` / `checkstyleIntegrationTest` / `compileIntegrationTestJava`（均消费 `test.output`）→ 自动获得对 `compileSaml2TestJava` 的隐式依赖
- **备选方案**：
  - **方向 B（同目录 + 逐个 `dependsOn`）**：需为 `testJar`、`test`、`checkstyleTest`、`checkstyleIntegrationTest`、`compileIntegrationTestJava` 逐条补依赖。脆弱——新增任何消费 test 输出的任务都要记得补，漏一条竞态复发。
  - **完整 `saml2Test` sourceSet（upstream 后续做法）**：语义最正、扩展性最好，但要新建 sourceSet、配置 compile/runtime classpath、把 testJar 改为聚合两个 sourceSet 输出，改动面显著大于当前修复目标。

### 2. `saml2Tests` 测试任务继续依赖 `compileSaml2TestJava`

- **原因**：`saml2Tests` 需要 SAML2 类的编译输出。改独立目录后，`saml2Tests` 的 `testClassesDirs` / classpath 需能覆盖到 saml2Test 目录。由于该目录已并入 `sourceSets.test.output`，`saml2Tests` 若基于 `sourceSets.test.output` 构造 classpath 即自动可见；需在实现时核对 `saml2Tests` 的 `testClassesDirs` 配置确实包含新目录。
- **验证点**：`saml2Tests` 仍能发现并运行 `org.springframework.security.config.annotation.web.configurers.saml2.*` 测试。

### 3. 通过「全量构建 + 产物内容校验」验收，而非只看单模块 testJar

- **原因**：本 bug 只在全量并行构建下暴露，单独 `:config:testJar` 一直是好的。必须以复现路径（`./gradlew build`）验收，并校验 `test` 模块能编译通过。

### 4. 去掉 `options.sourcepath`，改用 `compileTestJava` 的 class 输出解析共享测试依赖

- **问题**：`compileSaml2TestJava` 设置了 `options.sourcepath = sourceSets.test.java.getSourceDirectories()`。当 saml2 测试文件（`Saml2LoginConfigurerTests`、`Saml2LogoutConfigurerTests`）import 了非 saml2 的共享测试工具类（`SecurityContextChangedListenerArgumentMatchers`、`SecurityContextChangedListenerConfig`）时，javac 通过 sourcepath 找到源码并**重新编译**到 `saml2Test/` 目录。这些类已经被 `compileTestJava` 编译到 `test/` 目录，`sourceSets.test.output.dir(...)` 合并两个目录后 `testJar` 报 duplicate entry。
- **修复**：删除 `options.sourcepath`，将 `compileTestJava` 的输出加入 classpath，使 javac 从 `.class` 文件解析依赖而非重新编译源码：

  ```groovy
  task compileSaml2TestJava(type: JavaCompile) {
      // ...toolchain / source / include 不变...
      dependsOn compileTestJava
      classpath = sourceSets.test.compileClasspath + files(compileTestJava.destinationDirectory)
      destinationDirectory = new File("${buildDir}/classes/java/saml2Test")
      // 不再设置 options.sourcepath
  }
  ```

- **为何安全**：`.class` 文件在 classpath 上同样满足 javac 的类型解析需求；`dependsOn compileTestJava` 保证编译顺序；saml2 测试对非 saml2 工具类的依赖本就是事实存在的，显式声明比隐式 sourcepath 更可预测。

## Risks / Trade-offs

- **[遗漏 `builtBy` 导致依赖丢失]** → 注册时必须带 `builtBy: compileSaml2TestJava`；验收时用 `--dry-run` 确认 `testJar`/`test` 前置了 `compileSaml2TestJava`
- **[`saml2Tests` 找不到类]** → 改目录后核对 `saml2Tests.testClassesDirs`/classpath 覆盖 saml2Test 目录；运行 `:config:saml2Tests` 确认用例被发现
- **[增量构建残留]** → 旧的共享目录下可能残留 SAML2 `.class`；首次验证前做一次 `clean` 避免旧产物掩盖问题
- **[与 upstream 分叉]** → 本方案是最小侵入的过渡形态，与 upstream 完整 sourceSet 方案不同；在文档中标注，便于日后对齐上游时替换
- **[sourcepath 删除后编译失败]** → 理论上 `.class` 文件足以满足 javac 的类型解析；若遇到仅 sourcepath 能解析的极端场景（如注解处理），需回退并改用 `duplicatesStrategy` 兜底
