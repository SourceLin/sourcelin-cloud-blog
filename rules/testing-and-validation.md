# 测试与验证规则

适用范围：所有代码、规则、技能、文档和脚本改动。

## 1. 通用原则

- MUST：每次代码改动执行与改动范围匹配的最小验证命令。
- MUST：验证失败先修复，再继续下一阶段。
- MUST：最终输出实际执行的命令和结果。
- MUST NOT：删除测试、降低断言、放宽扫描规则来通过验证。
- MUST NOT：把未执行的命令描述为已通过。
- CHECK：最终回复必须包含验证命令列表。

## 2. 后端验证

- MUST：使用当前环境可用的 `mvn` 执行 Maven 验证。
- MUST：如本机需要自定义 `settings.xml`，只能通过本地终端环境或临时命令参数提供，不写入项目文档和规则。
- MUST：后端模块改动至少执行对应模块 `compile` 或 `test`。
- MUST：公共模块改动执行受影响模块的 `test -pl <module> -am`。
- CHECK：示例命令 `mvn test -pl sourcelin-modules/sourcelin-blog -am`。

## 3. API 契约验证

- MUST：接口、响应体、分页、前端 API 消费或脚本改动后执行 `node scripts/api-contract-scan.mjs`。
- MUST：涉及运行态接口契约时执行 `node scripts/blog-api-smoke.mjs`。
- MUST：登录态 smoke 测试的账号、密码、token、cookie 只允许来自本地输入或环境，不写入仓库。
- MUST NOT：smoke 脚本使用旧字段 fallback。
- CHECK：`api-contract-scan` 阻断项为 0；smoke 失败接口数为 0。

## 4. 博客前台验证

- MUST：TypeScript 或 Vue 改动执行 `npm run typecheck`。
- MUST：样式、组件、页面或设计系统边界改动执行 `npm run style:guard`。
- MUST：路由、模块边界、页面拆分或架构规则改动执行 `npm run test:architecture`。
- MUST：仓库级前台边界验证执行 `node scripts/architecture-guard/frontend-platform-guard.mjs`。
- CHECK：所有命令退出码为 0。

## 5. 管理后台验证

- MUST：TypeScript、Vue、API 或页面改动执行 `pnpm run type-check`。
- MUST：样式、格式、组件或页面改动执行 `pnpm run lint`。
- MUST：后台模块化边界改动执行 `node scripts/architecture-guard/frontend-admin-guard.mjs`。
- CHECK：所有命令退出码为 0。

## 6. 规则与技能验证

- MUST：`rules/`、`skills/`、`AGENTS.md` 或 `docs/architecture/**` 改动后执行 `node scripts/architecture-guard/rules-system-guard.mjs`。
- MUST：架构边界改动后执行 `node scripts/architecture-guard/run.mjs`。
- MUST NOT：保留旧规则或旧技能引用。
- CHECK：规则体系守卫输出 `rules-system-guard: OK`。
