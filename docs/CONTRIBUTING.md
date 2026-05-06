# 贡献指南

感谢您对 Sourcelin Blog 项目的兴趣！我们欢迎任何形式的贡献，包括但不限于提交 Bug 报告、提出新功能建议、完善文档、修复代码等。

## 行为准则

请阅读并遵守我们的 [行为准则](./CODE_OF_CONDUCT.md)，以确保社区保持友好和包容。

## 如何贡献

### 报告 Bug

如果您发现了 Bug，请按照以下步骤报告：

1. 首先搜索 [Issues](https://gitee.com/sourcelin/sourcelin-cloud-blog/issues) 确认是否已有人报告
2. 如果没有，请创建新的 Issue，包含：
   - 清晰的 Bug 描述
   - 复现步骤（最好有截图或动图）
   - 期望行为和实际行为
   - 环境信息（操作系统、浏览器、Java 版本等）

### 提出新功能

1. 搜索现有 Issues 确认是否已有人提出
2. 创建 Feature Request，详细说明：
   - 功能描述
   - 使用场景
   - 可能的实现方案
   - 对项目的影响

### 提交代码

#### 开发环境准备

```bash
# 1. Fork 本仓库
# 2. 克隆到本地
git clone https://gitee.com/your-username/sourcelin-cloud-blog.git
cd sourcelin-cloud-blog

# 3. 添加上游仓库
git remote add upstream https://gitee.com/sourcelin/sourcelin-cloud-blog.git

# 4. 创建开发分支
git checkout -b feature/your-feature-name
```

#### 代码规范

**Java 代码规范**

- 遵循 [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c)
- 使用 Lombok 减少样板代码
- 类和方法添加必要的注释
- 方法长度控制在 80 行以内

**前端代码规范**

- 使用 ESLint + Prettier 进行代码检查
- 组件命名使用大驼峰 (PascalCase)
- CSS 类名使用小写连字符 (kebab-case)
- Vue 文件结构：template → script → style

#### 提交规范

```bash
# 1. 同步最新代码
git fetch upstream
git rebase upstream/master

# 2. 进行开发...
# 3. 提交代码
git add .
git commit -m 'feat: Add new feature'

# 4. 推送分支
git push origin feature/your-feature-name
```

**提交信息格式**

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type 类型**

| 类型 | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug 修复 |
| docs | 文档修改 |
| style | 代码格式（不影响功能）|
| refactor | 重构（既不是新功能也不是修复）|
| perf | 性能优化 |
| test | 测试相关 |
| build | 构建或依赖相关 |
| ci | CI 配置相关 |
| chore | 其他不修改源码的更改 |

**示例**

```
feat(article): Add markdown preview support

- Add markdown-it editor
- Support code highlighting
- Support LaTeX formula

Closes #123
```

#### 创建 Pull Request

1. 访问 [Pull Requests](https://gitee.com/sourcelin/sourcelin-cloud-blog/pulls)
2. 点击「新建 Pull Request」
3. 选择源分支和目标分支
4. 填写 PR 描述：
   - 描述解决的问题或新增的功能
   - 列出测试结果
   - 截图（如果有 UI 更改）
5. 提交等待审核

### 完善文档

文档改进同样重要！您可以：

- 修正错别字或语法错误
- 补充遗漏的内容
- 翻译文档到其他语言
- 添加使用示例

### 其他贡献

- ⭐ Star 项目支持我们
- 📢 推荐给朋友
- 💰 赞助项目
- 🏫 在学校或社区分享项目

## 开发指南

### 项目结构

```
sourcelin-cloud-blog/
├── sourcelin-gateway/          # 网关服务
├── sourcelin-auth/             # 认证服务
├── sourcelin-modules/          # 业务模块
│   ├── sourcelin-system/       # 系统模块
│   ├── sourcelin-blog/        # 博客模块
│   ├── sourcelin-file/         # 文件模块
│   └── sourcelin-job/          # 定时任务
├── sourcelin-common/           # 通用模块
├── sourcelin-api/              # API 接口
├── sourcelin-ui/               # 前端项目
│   ├── sourcelin-ui-admin/     # 管理后台
│   └── sourcelin-ui-platform/  # 博客前台
└── sql/                        # 数据库脚本
```

### 运行测试

```bash
# 后端测试
mvn test

# 前端测试
cd sourcelin-ui/sourcelin-ui-admin
npm run test
```

### 构建项目

```bash
# 构建后端
mvn clean package -DskipTests

# 构建前端
cd sourcelin-ui/sourcelin-ui-admin
npm run build:prod
```

## 联系方式

- 📧 邮箱：support@sourcelin.com
- 💬 QQ群：即将推出
- 🌐 官网：https://sourcelin.com

## 赞助支持

如果您觉得本项目对您有帮助，可以通过以下方式支持我们：

- ⭐ Star 本项目
- 💝 捐赠咖啡
- 🤝 成为赞助商

---

感谢您的贡献！🎉
