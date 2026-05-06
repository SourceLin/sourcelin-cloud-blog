# 测试与验证规则

适用范围：所有代码、规则、技能和文档改动。

## 1. 通用原则

- MUST：每次代码改动执行与改动范围匹配的最小验证命令。
- MUST：验证失败先修复，再继续下一阶段。
- MUST：最终输出实际执行的命令和结果。
- MUST NOT：删除测试、降低断言或放宽规则来通过验证。
- MUST NOT：把未执行的命令描述为已通过。
- CHECK：最终回复必须包含验证命令列表。

## 2. 后端验证

- MUST：使用当前环境可用的 `mvn` 执行 Maven 验证。
- MUST：如本机需要自定义 `settings.xml`，只能通过本地终端环境或临时命令参数提供，不写入项目文档和规则。
- MUST：后端模块改动至少执行对应模块 `compile` 或 `test`。
- MUST：公共模块改动执行受影响模块的 `test -pl <module> -am`。
- CHECK：示例命令 `mvn test -pl sourcelin-modules/sourcelin-blog -am`。

## 3. API 契约验证

- MUST：接口、响应体、分页或前端 API 消费改动后，审查 Controller 返回类型、前端 request 解包和分页字段。
- MUST：涉及运行态接口契约时，使用本地登录态或临时输入执行真实 HTTP 验证；账号、密码、token、cookie 不写入仓库。
- MUST：分页响应只允许 `items/total/page/pageSize/totalPages`。
- MUST NOT：保留旧字段 fallback，例如 `items ?? list`、`items ?? rows`、`items ?? records`。
- CHECK：后端执行受影响模块编译或单测；前端执行对应类型检查。

## 4. 博客前台验证

- MUST：TypeScript 或 Vue 改动执行 `npm run typecheck`。
- MUST：样式、组件、页面或设计系统边界改动执行 `npm run style:guard`。
- MUST：路由、模块边界、页面拆分或架构规则改动执行 `npm run test:architecture`。
- CHECK：所有命令退出码为 0。

## 5. 管理后台验证

- MUST：TypeScript、Vue、API 或页面改动执行 `pnpm run type-check`。
- MUST：样式、格式、组件或页面改动执行 `pnpm run lint`。
- CHECK：所有命令退出码为 0。

## 6. 规则与技能验证

- MUST：`rules/`、`skills/` 或 `AGENTS.md` 改动后，检查引用路径均在开源分支真实存在。
- MUST：数据库初始化脚本改动后，导入空库验证表结构和必要初始化数据。
- MUST NOT：保留旧规则、旧技能、已排除脚本、市场分析或商业模式文档引用。
- CHECK：执行 `git ls-files scripts` 必须无输出；执行 `git ls-files docs` 时只允许 `docs/sql/**`。
