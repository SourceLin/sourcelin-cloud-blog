# 管理后台配置域拆分落地方案（一次切换版）

## 1. 目标与原则

### 1.1 目标
- 将当前 `system/config` 组合页拆分为两个独立能力页：
1. 参数中心（系统通用参数 CRUD）
2. 管理后台 UI 默认配置（主题/布局/策略聚合配置）
- 实现“单一编辑入口”：`sys.admin.ui.*` 只能通过 UI 默认配置页编辑，参数中心不可见、不可改。
- 完成权限域拆分，避免继续复用 `system:config:*` 管理 UI 默认配置。

### 1.2 原则
- 不做兼容路径，不保留临时入口，不做双写。
- 一次发布后直接进入目标信息架构与权限架构。
- 前后端、菜单、权限、SQL 同步发布，避免半状态运行。
- 本次属于断链式重命名（`/system/admin-ui-config` -> `/system/admin-ui`），必须原子发布；未满足发布前置条件时禁止上线。

## 2. 现状问题（必须解决）

- `system/config` 页面同时承载两个不同领域：参数表与 UI 默认配置，认知负担高。
- `sys.admin.ui.defaults/policy/presets` 存在双入口编辑风险：
1. 参数中心可编辑 `sys_config`
2. UI 默认配置页也可编辑
- 权限语义不清：UI 默认配置接口当前复用了 `system:config:edit`。
- 前端页面中 `system:config:create/update/delete` 与后端 `add/edit/remove` 命名不一致，存在权限点漂移风险。
- 刷新缓存权限语义错误：后端 `refreshCache` 当前复用 `system:config:remove`，需拆分为独立权限点。
- 接口命名切换存在断链风险：当前线上实际为 `AdminUiConfigController` + `/system/admin-ui-config`，若前后端不同步发布会导致 `settingsStore` 初始化失败。

## 3. 目标态设计

### 3.1 页面与菜单
- 菜单“参数设置”重命名为“参数中心”，组件仍为 `system/config/index`，仅保留参数 CRUD 能力。
- 新增菜单“后台 UI 默认配置”，组件为 `system/admin-ui/index`，仅保留 UI 聚合配置能力。
- 两页完全独立，不再在同一页面混排。

### 3.1.1 最终路由与组件映射（目标态）

| 菜单名 | parentId | orderNum | path | component | perms（菜单可见） | status | 说明 |
|---|---:|---:|---|---|---|---|---|
| 参数中心 | `1` | `7` | `config` | `system/config/index` | `system:config:list` | `0` | 仅参数中心 CRUD，不含 UI 默认配置 |
| 后台 UI 默认配置 | `1` | `8`（后续菜单顺延） | `adminUi` | `system/admin-ui/index` | `system:admin-ui:query` | `0` | 仅管理后台默认主题/布局/策略 |

### 3.2 权限域
- 参数中心继续使用：
1. `system:config:list/query/add/edit/remove/export/refresh`
- 新增 UI 默认配置权限：
1. `system:admin-ui:query`
2. `system:admin-ui:edit`

### 3.2.1 按钮-权限-后端接口映射（目标态）

| 页面 | 按钮/动作 | 前端权限点 | 后端接口 | 后端权限点 |
|---|---|---|---|---|
| 参数中心 | 查询列表 | `system:config:list` | `GET /system/config/list` | `system:config:list` |
| 参数中心 | 新增 | `system:config:add` | `POST /system/config` | `system:config:add` |
| 参数中心 | 编辑 | `system:config:edit` | `PUT /system/config` | `system:config:edit` |
| 参数中心 | 删除 | `system:config:remove` | `DELETE /system/config/{ids}` | `system:config:remove` |
| 参数中心 | 刷新缓存 | `system:config:refresh` | `DELETE /system/config/refreshCache` | `system:config:refresh` |
| 后台 UI 默认配置 | 查询 | `system:admin-ui:query` | `GET /system/admin-ui` | `system:admin-ui:query` |
| 后台 UI 默认配置 | 保存 | `system:admin-ui:edit` | `PUT /system/admin-ui` | `system:admin-ui:edit` |

### 3.3 数据编辑边界
- 保留 UI 聚合配置数据仍落地于 `sys_config`：
1. `sys.admin.ui.defaults`
2. `sys.admin.ui.policy`
3. `sys.admin.ui.presets`
- 但参数中心对 `sys.admin.ui.%` 做强约束：
1. 列表不可见
2. 新增/修改禁止写入该前缀
3. `updateByKey` 禁止更新该前缀
4. 删除禁止作用于该前缀

## 4. 详细改造清单

### 4.1 前端（sourcelin-ui-admin）

#### 4.1.1 页面拆分
1. 新建页面：`src/views/system/admin-ui/index.vue`
2. 将现有 `src/views/system/config/index.vue` 中 UI 默认配置区块完整迁移到新页面：
- UI 配置卡片模板
- `AdminUiAPI` 调用
- `loadAdminUi` / `handleSaveAdminUi`
- `adminUiForm`、`normalizeAdminUi`
- 相关样式 `ui-config-section`、`preset-option` 等
3. 原 `src/views/system/config/index.vue` 仅保留参数中心列表、搜索、新增/编辑/删除、刷新缓存。

#### 4.1.2 权限点统一
1. 参数中心页面按钮权限改为后端标准命名：
- `system:config:add`
- `system:config:edit`
- `system:config:remove`
- `system:config:refresh`
2. UI 默认配置页权限：
- 查询入口与页面访问：`system:admin-ui:query`
- 保存按钮：`system:admin-ui:edit`

#### 4.1.3 API 层
- 新建并使用 `src/api/system/admin-ui.ts`（URL 使用 `/system/admin-ui`）。
- 参数中心继续使用 `src/api/system/config.ts`。

### 4.2 后端（sourcelin-system）

#### 4.2.1 AdminUiController 权限拆分
文件：`src/main/java/com/sourcelin/system/controller/AdminUiController.java`

- `GET /admin-ui` 权限改为：`system:admin-ui:query`
- `PUT /admin-ui` 权限改为：`system:admin-ui:edit`
- 不再保留 `AdminUiConfigController` 与 `/admin-ui-config` 旧路径。

#### 4.2.2 SysConfigController 增加保留前缀防护
文件：`src/main/java/com/sourcelin/system/controller/SysConfigController.java`

新增保留前缀常量：
- `sys.admin.ui.`

在以下入口增加校验：
1. `add`：禁止新增 `sys.admin.ui.*`
2. `edit`：禁止修改为 `sys.admin.ui.*`，且禁止编辑已有 `sys.admin.ui.*`
3. `remove`：禁止删除 `sys.admin.ui.*`
4. `updateByKey`：禁止更新 `sys.admin.ui.*`
5. `refreshCache`：权限点改为 `system:config:refresh`（禁止继续复用 `remove`）

校验失败统一抛 `BusinessException`，错误码使用现有业务错误码体系。

#### 4.2.3 参数中心查询排除 UI 配置键
文件：`src/main/resources/mapper/system/SysConfigMapper.xml`

在 `selectConfigList` 的 `where` 中增加固定条件：
- `AND config_key NOT LIKE 'sys.admin.ui.%'`

效果：参数中心分页总数与结果一致，不会出现“先查后过滤”导致的分页失真。

#### 4.2.4 服务层保留前缀防护（强制）
文件：
- `ISysConfigService.java`
- `SysConfigServiceImpl.java`

新增方法：
- `boolean isReservedConfigKey(String configKey)`

并在以下服务入口强制执行：
1. `insertConfig`
2. `updateConfig`
3. `deleteConfigByIds`
4. `updateConfigByKey`

要求：即使未来出现新 Controller、批任务、导入任务调用服务层，也不能绕过 `sys.admin.ui.*` 保护。

#### 4.2.5 导出链路与查询一致性
文件：`SysConfigController.java` + `SysConfigMapper.xml`

- `export` 调用与 `list` 统一使用 `selectConfigList`。
- 因 Mapper 已加入 `config_key NOT LIKE 'sys.admin.ui.%'`，导出天然不包含保留前缀。
- 禁止在 Controller 再做二次内存过滤，防止分页/导出口径不一致。

### 4.3 菜单与权限 SQL（一次性执行）

文件建议新增：`docs/sql/2026-04-28-admin-ui-domain-split.sql`

执行内容：
1. 将 menu_id=106 的菜单名由“参数设置”改为“参数中心”。
2. 新增“后台 UI 默认配置”菜单（`path=adminUi`，`component=system/admin-ui/index`）。
3. 新增功能权限菜单：
- `system:admin-ui:query`
- `system:admin-ui:edit`
4. 新增参数中心刷新缓存权限：`system:config:refresh`，并授权管理员角色。
5. 为管理员角色分配新菜单与新权限。
6. 若历史存在错误权限点（如 `system:config:update/create/delete`）且已入库，统一清理并改为标准点。
7. 输出菜单字段核对清单：`menu_name/path/component/perms/parent_id/order_num/status`。

## 5. 实施顺序（强制）

1. 提交并执行 SQL（菜单、权限、角色授权）。
2. 发布后端（接口重命名 + 权限改造 + 参数中心保留前缀防护 + Mapper 查询排除）。
3. 发布前端（拆页 + API 路径切换 + 权限点调整）。
4. 运维执行：
- 清理网关/服务缓存（若有）
- 管理员重新登录，刷新动态路由与权限快照

### 5.1 原子发布闸门与中止条件（强制）
1. 发布前检查：
- 后端包已包含 `AdminUiController` 且旧 `AdminUiConfigController` 已移除。
- 前端包已将 `settingsStore` 与 UI 默认配置页面调用切换到 `/system/admin-ui`。
- SQL 已执行并校验新权限点存在。
2. 发布中止条件（任一命中即停止）：
- `GET /system/admin-ui` 返回 404/401/403（管理员账号）。
- 管理后台登录后 `settingsStore.initialize()` 报错。
- 动态路由中“后台 UI 默认配置”菜单无法打开页面。
3. 发布完成判定：
- 菜单、权限、页面、接口四项全部通过冒烟后才允许放量。

## 6. 验收标准

#### 6.1 菜单与路由
- 系统管理下出现两个独立菜单：
1. 参数中心
2. 后台 UI 默认配置
- 两个菜单分别进入不同页面，不再同页并存。

#### 6.2 参数中心行为
- 列表中看不到 `sys.admin.ui.*`。
- 尝试通过参数中心新增/编辑/删除/按键更新 `sys.admin.ui.*` 均被拒绝。

#### 6.3 UI 默认配置行为
- UI 默认配置页可正常读取与保存。
- 保存后刷新页面，`settingsStore` 初始化读取结果正确生效。

#### 6.4 权限行为
- 仅拥有 `system:config:*` 的用户无法修改 UI 默认配置。
- 仅拥有 `system:admin-ui:*` 的用户无法操作参数中心增删改。

#### 6.5 数据正确性断言
1. `sys.admin.ui.defaults`、`sys.admin.ui.policy`、`sys.admin.ui.presets` 三个 key 必须存在，且值为合法 JSON。
2. 写后读一致：保存后立即读取，响应体与数据库值一致。
3. 失败不污染：模拟保存失败时，数据库旧值保持不变，缓存无脏写。
4. 参数中心与导出结果均不包含 `sys.admin.ui.*`。

## 7. 测试清单

#### 7.1 后端测试
- `AdminUiController`：
1. 无权限访问返回鉴权失败
2. 有 `query` 可读、有 `edit` 可写
- `SysConfigController`：
1. 对 `sys.admin.ui.*` 的 add/edit/remove/updateByKey 触发业务异常
2. `refreshCache` 权限点为 `system:config:refresh`
3. `list/export` 不返回保留前缀数据
4. 服务层入口 `insert/update/delete/updateByKey` 直调也会拦截保留前缀

#### 7.2 前端测试
- `system/config` 页面只保留参数中心元素，无 UI 默认配置卡片。
- `system/admin-ui` 页面完整展示原 UI 配置能力。
- `v-hasPerm` 在新旧权限下行为符合预期。
- 登录初始化时 `settingsStore` 成功读取 `/system/admin-ui`。

## 8. 回滚方案（仅版本回滚，不保留兼容逻辑）

- 若发布失败，按版本回滚：
1. 回滚前端到拆分前版本
2. 回滚后端到拆分前版本
3. 回滚 SQL（删除新菜单/权限，恢复原菜单名、原排序、原角色授权）
4. 恢复旧接口与旧权限点（仅用于故障恢复窗口）

说明：回滚仅用于故障恢复，不作为长期双轨运行策略。

### 8.1 SQL 回滚最小清单
1. 删除“后台 UI 默认配置”菜单及其功能权限节点。
2. 删除角色-菜单关联中新增记录。
3. 恢复 menu_id=106 菜单名称与排序。
4. 回滚 `system:config:refresh` 新增权限（若本次新增）。
5. 恢复历史权限点映射（若本次做了清理替换）。

## 9. 交付物清单

1. 前端代码改造 PR（页面拆分 + 权限修正）
2. 后端代码改造 PR（权限域拆分 + 保留前缀防护 + 查询排除）
3. SQL 脚本：`2026-04-28-admin-ui-domain-split.sql`
4. 测试报告（接口鉴权 + 页面行为 + 冒烟截图）
