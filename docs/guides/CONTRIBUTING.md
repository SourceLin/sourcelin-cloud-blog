# 贡献指南

感谢您关注 Sourcelin Blog。这个仓库欢迎代码、文档、截图、部署经验、二次开发案例和问题反馈等多种形式的贡献。

## 开始之前

提交贡献前，建议先阅读以下内容：

- [README.md](../../README.md)
- [AGENTS.md](../../AGENTS.md)
- [rules/README.md](../../rules/README.md)
- [docs/DOCS_INDEX.md](../DOCS_INDEX.md)

如果你的改动涉及具体领域，请继续阅读对应规则：

- 后端：`rules/backend.md`
- 博客前台：`rules/frontend-platform.md`
- 管理后台：`rules/frontend-admin.md`
- 接口、分页、响应体：`rules/api-contract.md`

## 行为准则

参与社区前，请先阅读并遵守 [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md)。

## 贡献方式

你可以通过以下方式参与：

- 提交 Bug 反馈
- 提出功能建议
- 完善 README、部署文档、截图和示例
- 修复前后端问题
- 优化 API 契约、规则和技能文档
- 提交部署案例、二开案例和实战经验

## 问题反馈

如发现问题，请优先通过 Gitee Issue 提交：

- Issues 地址：[https://gitee.com/my_lyq/sourcelin-cloud-blog/issues](https://gitee.com/my_lyq/sourcelin-cloud-blog/issues)

提 Issue 前请先搜索是否已有类似问题，避免重复。

推荐按以下信息描述：

1. 问题现象
2. 重现步骤
3. 期望结果
4. 实际结果
5. 环境信息
6. 截图、日志或报错信息

## 功能建议

如果要提交功能建议，建议在 Issue 中说明：

1. 你想解决什么问题
2. 你的使用场景
3. 你预期的行为
4. 是否愿意自行提交 PR

## 开发流程

### 1. Fork 并克隆仓库

```bash
git clone https://gitee.com/your-username/sourcelin-cloud-blog.git
cd sourcelin-cloud-blog
git remote add upstream https://gitee.com/my_lyq/sourcelin-cloud-blog.git
```

### 2. 同步最新代码

```bash
git fetch upstream
```

默认情况下，请基于 `develop` 开发并向 `develop` 提交 Pull Request。`master` 用于公开稳定发布。

```bash
git checkout develop
git pull upstream develop
git checkout -b feature/your-feature-name
```

如果仓库当前未开放 `develop` 协作，以 Maintainer 当次说明为准。

### 3. 按规则开发

开发过程中请遵守：

- 对外接口统一 `ApiResponse<T>`
- 分页统一 `PageResult<T>`
- 不新增旧字段兼容写法
- 只修改与任务直接相关的文件
- 不提交生成产物、临时文件和敏感信息

## 提交规范

提交信息默认使用中文，建议使用语义化前缀：

```text
feat: 新增文章评论回复提醒
fix: 修复前台文章详情页分类链接错误
docs: 重写 README 的界面预览部分
refactor: 拆分后台系统配置模块
test: 补充博客模块分页查询单测
```

常用前缀：

| 类型 | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug 修复 |
| docs | 文档修改 |
| refactor | 重构 |
| test | 测试相关 |
| build | 构建或依赖调整 |
| chore | 其他杂项修改 |

## 提交前自检

请根据改动范围执行最小必要验证。

### 后端改动

```bash
mvn compile -pl sourcelin-modules/sourcelin-blog -am
```

或：

```bash
mvn test -pl sourcelin-modules/sourcelin-blog -am
```

### 博客前台改动

```bash
cd sourcelin-ui/sourcelin-ui-platform
npm run typecheck
npm run style:guard
npm run test:architecture
```

### 管理后台改动

```bash
cd sourcelin-ui/sourcelin-ui-admin
pnpm run type-check
pnpm run lint
```

### 文档、规则、技能改动

请至少检查：

- 链接和引用路径真实存在
- 没有把开源分支禁止内容加入正式入口
- 文档描述与当前仓库实际结构一致

## Pull Request 要求

PR 地址：

- [https://gitee.com/my_lyq/sourcelin-cloud-blog/pulls](https://gitee.com/my_lyq/sourcelin-cloud-blog/pulls)

提交 PR 时请说明：

1. 修改目的
2. 主要改动点
3. 影响范围
4. 验证命令和结果
5. 截图或录屏（涉及 UI 时）
6. 关联的 Issue

默认目标分支为 `develop`。

## 哪些贡献最有价值

当前阶段最欢迎：

- 部署文档修正
- 新手避坑补充
- 前后台页面截图补全
- Bug 修复和回归验证
- AI 规则与技能体系的可复用优化
- 毕设 / 二开 / 真实部署案例提交

## 联系方式

- 仓库主页：[https://gitee.com/my_lyq/sourcelin-cloud-blog](https://gitee.com/my_lyq/sourcelin-cloud-blog)
- 演示地址：[http://sourcelin.cn/](http://sourcelin.cn/)
- 联系邮箱：lyq_0201@126.com

感谢你的贡献。
