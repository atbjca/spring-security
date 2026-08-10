## Context

当前分支以 Spring Security `5.8.16` 为基础，仍支持 Java 8、Servlet 4 和 OpenSAML 3.4.6。2026 年上游修复分散在 `core`、`web`、`config` 和 `saml2-service-provider`，且部分参考提交来自使用较新 Java/OpenSAML API 的维护分支，不能直接以“整版本升级”或不经审查的 cherry-pick 处理。

本 change 以 Spring Security 5.8.27 的安全行为为目标，而不是宣称内部版本在所有功能上等同 5.8.27。验收依据是七项 CVE 的触发条件不再成立、合法流程保持兼容、上游提交和本地实现之间存在可审计映射。

## Goals / Non-Goals

**Goals:**

- 在当前 Java 8/OpenSAML 3 基线上关闭七项适用于 5.8 的 2026 CVE
- 对每项漏洞同时覆盖易受攻击行为、修复后的拒绝行为和正常业务行为
- 保持现有公开 API、二进制兼容性、Servlet 4 兼容性和 OpenSAML 3 登录/登出互操作性
- 记录官方公告、参考提交、本地回移提交、测试和最终处置状态
- 将 `CVE-2026-22748` 作为有证据的不适用项，而不是按宽泛 CPE 结果误回移

**Non-Goals:**

- 不整体合并 Spring Security 5.8.17 至 5.8.27 的所有非安全变更
- 不迁移到 Java 17、Jakarta Servlet 6、OpenSAML 4/5 或 Spring Security 6/7 API
- 不在本 change 中处理 Jackson、发布依赖图、SBOM/VEX 平台或 Spring Framework 源码漏洞
- 不以修改内部 GAV 或压制扫描结果代替源码修复

## Decisions

### 1. 使用“行为等价回移”，不按提交机械复制

每项 CVE 都以官方公告的受影响条件和修复后行为为契约，参考提交只用于定位设计意图。若参考代码依赖新主版本 API，则在 5.8 现有扩展点中实现最小等价逻辑，并保留原有签名。

| CVE | 5.8 回移行为 | 参考提交 |
|---|---|---|
| `CVE-2026-22732` | `OnCommittedResponseWrapper` 对 `addHeader`、`addIntHeader`、`setHeader`、`setIntHeader` 写入的 `Content-Length` 都跟踪响应完成状态，保证延迟写入的安全响应头不会因提前提交而丢失 | `1dae9aa45943` |
| `CVE-2026-22746` | 用户被禁用、过期或锁定时仍执行等价的密码附加检查以消除明显时序差异，同时向调用方保留原始账户状态异常；兼容开关默认采用安全值 | `a317a3d86639` |
| `CVE-2026-40988` | 所有 REDIRECT 登录和登出解压入口在 XML 解析前限制解压后 SAML 数据为 1 MiB，超过上限时失败关闭且不保留部分结果 | `9d4d9065b485` |
| `CVE-2026-41003` | SAML POST 表单的 action、参数名和值统一通过 HTML 上下文编码；在 5.8 中回移或实现 Java 8 兼容的表单跳转策略，禁止手工拼接未编码的 `RelyingPartyRegistration` 数据 | `356131b1ea6e` |
| `CVE-2026-41694` | 登录 Response/Assertion 和登出 Request/Response 必须先完成适用的签名验证，签名无效时不得进入解密或后续语义验证，避免将 SP 变成解密 oracle | `e50c2a6a74d5`、`cf0687120024` |
| `CVE-2026-41706` | Servlet 与 Reactive Cookie request cache 只保存并恢复同源相对 URI；绝对 URI、scheme-relative URI 和其他可逃逸到外部站点的表示不得成为登录后跳转目标 | `a14c9d66b159` |
| `CVE-2026-47838` | 默认 X.509 principal 提取使用结构化 `X500Principal`/`LdapName` RDN 解析，选择最具体的目标 CN 或 emailAddress；保留旧 extractor 供显式兼容配置，但默认路径不再使用正则解析 DN | `e3ad551ab337` |

### 2. 安全默认值优先，同时保留必要的兼容入口

`DaoAuthenticationProvider` 的附加检查默认始终执行；X.509 默认切换到结构化解析器；Cookie request cache 默认产生相对 URI。已有公开类和 setter 不删除，新增 API 只采用 Java 8 可表达的类型。对可能依赖旧行为的调用方，通过显式配置保留兼容路径，并在发布说明中标注安全影响。

### 3. SAML 修复集中覆盖所有协议入口

仓库存在 OpenSAML 3/4 分源集和多处复制的 `Saml2Utils`。实现时先盘点最终发布 JAR 中的全部 inflate、POST form、登录解密和登出解密入口，再将相同限制应用到每条可达路径。OpenSAML 4 测试可作为辅助，但发布基线必须以 OpenSAML 3 生产 class 目标为 Java 8，并在真实 Java 8 JVM 上通过消费测试为准；仓库构建本身可使用配置的较新 JDK。

### 4. 回归测试采用确定性安全断言

时序漏洞的常规单元测试验证“账户状态检查失败后仍调用密码检查”以及“原始异常不变”，避免把严格毫秒阈值作为唯一 CI 门禁；耗时分布测试可放在显式启用的慢速测试中。其余漏洞使用恶意输入夹具直接证明危险分支未执行，例如解压上限、解密器零调用、外部 URL 不被恢复、嵌入 DN 字符串不被当成独立 CN。

### 5. 维护逐项证据清单

每项 CVE 的文档至少记录：官方受影响范围、当前内部基线、触发条件、参考提交、实际修改文件、本地提交、测试命令和结果、最终状态。`CVE-2026-22748` 必须记录其仅影响 6.3+ 的官方证据，并与七项已回移漏洞分开。

## Risks / Trade-offs

- [较新分支实现依赖 5.8 不存在的 API] → 以安全行为和测试为准做最小适配，并对公开签名做二进制兼容检查
- [1 MiB SAML 上限拒绝超大但合法报文] → 使用官方修复上限、覆盖边界值测试，并在发布说明中公开限制
- [SAML 签名验证顺序调整造成互操作变化] → 覆盖 Response 签名、Assertion 签名、POST/REDIRECT 登录与登出组合；不为无有效签名的加密载荷保留兼容例外
- [账户时序测试在共享 CI 上抖动] → 以调用路径为强制门禁，统计测试仅作为补充证据
- [X.509 默认 principal 对重复 CN 的选择发生变化] → 按 RFC 结构选择最具体 RDN，保留旧 extractor 的显式配置方式并提供迁移说明
- [仅修补其中一个复制入口] → 用源码搜索清单和恶意输入参数化测试覆盖全部可达实现

## Migration Plan

1. 先回移 `core`/`web` 中相互独立的响应头、认证时序和 Cookie request cache 修复。
2. 回移 SAML 解压、表单编码和“先验签后解密”修复，并在 OpenSAML 3 主路径执行专项测试。
3. 引入结构化 X.509 principal extractor，切换默认配置并验证 Java/XML/Reactive 配置入口。
4. 使用配置的构建 JDK 执行模块测试，检查生产 class 的 Java 8 target 和二进制兼容性，并在真实 Java 8 JVM 上运行完整安全夹具，生成逐项证据。
5. 若任一回移导致不可接受回归，在发布前回滚该 change 的全部实现；不得发布“七项基线已完成”的部分状态。

## Open Questions

无阻塞问题。实现阶段需要确认各参考提交在 5.8 维护分支上的最终修正版；若提交与官方公告行为冲突，以官方修复版本的测试和公告为准。
