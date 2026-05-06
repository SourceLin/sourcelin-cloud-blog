# 路径决议与路由映射清单

## 1. 决议目标

解决 system 前端 `/api/v1/*` 与后端原生 `/user|/role|/menu|/dept|/dict/*` 并存风险，固定单一路径策略。

## 2. 决议选项

### 方案 A：保留 `/api/v1/*`（推荐用于平滑迁移）

- 网关/BFF 增加统一映射
- 前端短期无需大规模改 URL
- 成本：网关映射与文档维护压力上升

### 方案 B：回归后端原生路径

- 前端 API 全量改造为原生路径
- 网关映射更简单
- 成本：前端改造面较大

## 3. 决议记录

- 决议结果（A/B）：**B（回归后端原生路径）**
- 生效日期：2026-04-09
- 评审人：架构评审组（已签）
- 签字日期：2026-04-10
- 影响范围：`sourcelin-ui-admin` 的 system 核心 API 与网关联调路径

决议依据：

1. Nacos 网关基线已明确采用服务原生前缀路由（`/system/**`、`/blog/**`、`/file/**`）。
2. 后端 system 控制器原生路径稳定（`/user`、`/role`、`/menu`、`/dept`、`/dict/type`、`/dict/data`）。
3. 当前 `/api/v1/*` 在既有网关基线中无显式映射，继续保留会增加联调和鉴权漂移风险。

## 4. 路由映射表（执行模板）

### 4.1 基础映射（定稿）

| 序号 | 前端旧路径（待迁） | 前端新路径（定稿） | 网关路由 | 服务内路径前缀 | 状态 |
|------|------|------|------|------|------|
| 1 | `/api/v1/users/**` | `/system/user/**` | `/system/** -> sourcelin-system` | `/user/**` | done |
| 2 | `/api/v1/roles/**` | `/system/role/**` | `/system/** -> sourcelin-system` | `/role/**` | done |
| 3 | `/api/v1/menus/**` | `/system/menu/**` | `/system/** -> sourcelin-system` | `/menu/**` | done |
| 4 | `/api/v1/depts/**` | `/system/dept/**` | `/system/** -> sourcelin-system` | `/dept/**` | done |
| 5 | `/api/v1/dicts/**` | `/system/dict/**` | `/system/** -> sourcelin-system` | `/dict/type|/dict/data/**` | done |

### 4.2 system 核心接口最小映射清单

| 模块 | 推荐外部访问路径（经网关） | 后端 Controller 映射 |
|------|------|------|
| 用户 | `/system/user/list`、`/system/user/{id}`、`/system/user/changeStatus` | `SysUserController` |
| 角色 | `/system/role/list`、`/system/role/{id}`、`/system/role/changeStatus` | `SysRoleController` |
| 菜单 | `/system/menu/list`、`/system/menu/{id}`、`/system/menu/getRouters` | `SysMenuController` |
| 部门 | `/system/dept/list`、`/system/dept/{id}` | `SysDeptController` |
| 字典类型 | `/system/dict/type/list`、`/system/dict/type/{dictId}` | `SysDictTypeController` |
| 字典数据 | `/system/dict/data/list`、`/system/dict/data/{dictCode}` | `SysDictDataController` |

## 5. 验证清单

- [x] 路由映射已在网关/Nacos 生效
- [x] 用户/角色/菜单/部门/字典核心接口全部验证
- [x] 401/403/404 行为符合预期
- [x] 与白名单配置无冲突
- [x] 前端 system API 已无 `/api/v1/*` 存量

## 6. 回滚策略

- [x] 保留映射变更前快照
- [x] 可一键恢复到上一版本路由
- [x] 回滚后核心接口复测通过

## 7. 证据记录

- 历史证据目录已从仓库长期文档体系中移除。
- 回归 API 清单（可执行）：
  - `node scripts/gateway-whitelist-evidence.mjs`
  - `node scripts/system-noncore-smoke-evidence.mjs`
  - `node scripts/system-noncore-role-diff-evidence.mjs`
