# 通用编码规则

适用范围：全仓库代码、脚本、文档和 AI 生成内容。

## 1. 语言与编码

- MUST：沟通、注释、提交说明和项目文档默认使用中文。
- MUST：所有文本文件保存为 UTF-8 无 BOM。
- MUST：特殊编码需求必须在更近目录 `AGENTS.md` 中说明。
- MUST NOT：混入无法解释的编码转换、乱码文本或 BOM。
- CHECK：新增或修改文档时直接打开检查中文可读性；涉及批量转换时运行对应格式化或构建命令。

## 2. 命名

- MUST：Vue 组件文件使用 PascalCase。
- MUST：CSS 类名使用 kebab-case。
- MUST：后端分页查询 DTO 命名为 `XxxPageQuery`。
- MUST：新增请求 DTO 命名为 `XxxCreateRequest`，更新请求 DTO 命名为 `XxxUpdateRequest`。
- MUST：详情返回命名为 `XxxDetailVO`，分页项命名为 `XxxPageItemVO`。
- MUST：前端 API 函数使用 `fetchXxxPage`、`fetchXxxList`、`fetchXxxDetail`、`createXxx`、`updateXxx`、`removeXxx`。
- MUST NOT：新增含义不明的 `data1`、`temp`、`handleClick2` 等临时命名。
- CHECK：代码评审和 `node scripts/architecture-guard/run.mjs`。

## 3. 类型

- MUST：TypeScript 新增代码使用明确类型。
- MUST：不确定外部数据结构使用 `unknown` 或 `Record<string, unknown>`。
- MUST NOT：新增 `any`。
- MUST NOT：通过类型断言掩盖协议不一致。
- CHECK：博客前台执行 `npm run typecheck`，管理后台执行 `pnpm run type-check`。

## 4. 安全配置

- MUST：账号、密码、token、cookie、私钥只允许通过本地环境或人工输入用于测试。
- MUST：示例配置只保留占位符。
- MUST NOT：将真实账号、密码、token、cookie、私钥写入仓库代码、配置、文档或脚本。
- MUST NOT：为了测试便利硬编码登录态。
- CHECK：提交前搜索 `password=`、`satoken`、`Authorization`、`Bearer `、真实 IP 密钥片段。

## 5. 文件与产物

- MUST：生成产物、压缩包、临时目录、编译产物不进入仓库。
- MUST：SQL 变更放入 `docs/sql/**`。
- MUST：架构和规则变更同步更新 `docs/architecture/**`、`rules/**`、`skills/**`。
- MUST NOT：提交 `target/`、`dist/`、`.tmp_*`、`*.class`、`*.zip`、`*.tgz`。
- CHECK：执行 `git status --short` 并确认无生成产物。

## 6. AI 修改边界

- MUST：只修改与任务直接相关的文件。
- MUST：发现无关脏改时保留，不回退。
- MUST：大改前先读取相关规则和真实代码。
- MUST NOT：用删除功能、降低校验、放宽脚本来掩盖失败。
- MUST NOT：引入重型框架替代现有技术栈。
- CHECK：最终输出修改文件、验证命令和剩余风险。
