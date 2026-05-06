# 管理后台主题系统前后端一体化升级方案

## 1. 文档目标

本文档用于定义 `sourcelin-ui-admin` 管理后台主题系统的轻量化一体化升级方案。

本次方案的核心判断是：

- 系统默认配置属于平台能力，应放后台统一管理
- 用户个性偏好属于前端体验数据，当前阶段优先放浏览器本地
- 暂不引入账号级用户偏好表
- 保留未来升级为“跨设备同步偏好”的扩展位

本方案的目标不是把主题系统做重，而是先把当前“本地存一部分、后台补丁式存一部分”的不一致状态收口为一套清晰模型。

---

## 2. 现状与问题

### 2.1 前端现状

当前管理后台主题能力主要分布在以下位置：

- 配置默认值：`sourcelin-ui/sourcelin-ui-admin/src/settings.ts`
- 主题状态与持久化：`sourcelin-ui/sourcelin-ui-admin/src/store/modules/settings.ts`
- 主题变量同步：`sourcelin-ui/sourcelin-ui-admin/src/utils/theme.ts`
- 设置面板：`sourcelin-ui/sourcelin-ui-admin/src/layouts/components/LayoutSettings.vue`

当前前端持久化分裂为两类：

1. 本地 `localStorage`
   - `theme`
   - `themeColor`
   - `layout`
   - `showTagsView`
   - `showAppLogo`
   - `pageSwitchingAnimation`
   - `sidebarColorScheme`

2. 后端 `sys_config`
   - 仅 `sys.index.sideTheme` 一项

结果是：

- 配置源头不统一
- reset 行为只重置本地，不重置后端
- 只有侧边栏配色被特殊处理，模型天然不对称
- “用户个性偏好”与“系统默认配置”语义混杂

### 2.2 后端现状

当前主题相关后端能力复用系统参数配置模块：

- Controller：`sourcelin-modules/sourcelin-system/src/main/java/com/sourcelin/system/controller/SysConfigController.java`
- Service：`sourcelin-modules/sourcelin-system/src/main/java/com/sourcelin/system/service/impl/SysConfigServiceImpl.java`
- Mapper：`sourcelin-modules/sourcelin-system/src/main/resources/mapper/system/SysConfigMapper.xml`
- 表结构：`docs/sql/sourcelin-cloud.sql` 中 `sys_config`

现有接口是：

- `GET /system/config/configKey/{configKey}`
- `PUT /system/config/key/{configKey}`

当前最大问题不是接口不可用，而是接口语义错位：

1. `sys_config` 是系统全局参数，不是用户个人偏好存储。
2. `PUT /config/key/{configKey}` 要求系统配置编辑权限，不适合普通后台用户切换自己的主题。
3. 现在只有 `sys.index.sideTheme` 接入后端，导致整个主题系统只有一项配置“特殊化”。

### 2.3 根本问题

当前主题系统没有拆清两类配置：

1. 系统默认配置
2. 当前浏览器内的用户个性偏好

在当前项目阶段，这两类已经足够。

不需要现在就引入第三类“账号级偏好存储”，否则系统复杂度会上升得过快。

---

## 3. 目标模型

### 3.1 两层主模型

本次推荐模型收口为两层：

#### A. 系统默认配置

由平台管理员维护，定义全站默认行为，例如：

- 默认主题偏好：`system`
- 默认布局：`left`
- 默认侧边栏样式：`classic`
- 默认主题色：`#4B55FA`
- 是否允许用户自定义主题色
- 可选主题色预设

#### B. 浏览器本地偏好

由当前用户在当前浏览器内维护，仅影响本地体验，例如：

- 亮/暗/跟随系统
- 布局模式
- 侧边栏风格
- 主题色
- 是否显示页签
- 是否显示 Logo
- 页面切换动画
- 组件尺寸

### 3.2 运行时解析值

前端运行时仍保留解析层，但不作为持久化层：

- `resolvedTheme = light | dark`
- Element Plus 动态主题变量
- 菜单和工具栏衍生变量

这一层继续由前端 runtime 负责，不写入后端、不写入数据库。

### 3.3 配置优先级

推荐固定优先级：

`浏览器本地偏好 > 系统默认配置 > 前端兜底默认值`

若未来某个路由存在特殊主题要求，可继续在最前面增加：

`路由临时覆盖 > 浏览器本地偏好 > 系统默认配置 > 前端兜底默认值`

---

## 4. 数据存储设计

### 4.1 后台存储：只存系统默认配置

系统默认配置继续复用 `sys_config`，但只存“默认值”和“策略”，不再承载用户个性偏好。

推荐新增以下配置键：

| 配置键 | 用途 | 示例值 |
|--------|------|--------|
| `sys.admin.ui.defaults` | 管理后台默认 UI 配置 | JSON |
| `sys.admin.ui.policy` | 管理后台主题策略 | JSON |
| `sys.admin.ui.presets` | 管理后台预置主题色列表 | JSON |

示例：

`sys.admin.ui.defaults`

```json
{
  "themePreference": "system",
  "layout": "left",
  "sidebarAppearance": "classic",
  "themeColor": "#4B55FA",
  "showTagsView": true,
  "showAppLogo": true,
  "pageSwitchingAnimation": "fade-slide",
  "componentSize": "default"
}
```

`sys.admin.ui.policy`

```json
{
  "allowCustomThemeColor": true,
  "allowUserLayoutSwitch": true,
  "allowUserSidebarAppearanceSwitch": true
}
```

`sys.admin.ui.presets`

```json
[
  "#4B55FA",
  "#0F766E",
  "#C2410C",
  "#1D4ED8",
  "#BE185D"
]
```

### 4.2 浏览器本地存储：只存个性偏好

用户个性偏好统一收口到浏览器本地存储。

建议继续使用现有 `STORAGE_KEYS` 前缀，并补充一个聚合对象键，或保持现有离散键但由统一 store 管理。

推荐的偏好结构：

```json
{
  "themePreference": "system",
  "layout": "left",
  "sidebarAppearance": "classic",
  "themeColor": "#4B55FA",
  "showTagsView": true,
  "showAppLogo": true,
  "pageSwitchingAnimation": "fade-slide",
  "componentSize": "default"
}
```

说明：

- 本地偏好是“浏览器级体验状态”，不是业务主数据。
- 当前阶段不做数据库落表。
- 本地偏好丢失的成本低，可接受。

### 4.3 不再保留的存储方式

本方案明确不再推荐：

- 继续使用 `sys.index.sideTheme` 作为用户切换写入目标
- 在后台数据库中新增 `sys_user_preference` 作为本阶段主方案

`sys.index.sideTheme` 只能保留为历史兼容默认值，不应再作为最终设计基线。

---

## 5. 接口设计

### 5.1 设计原则

本阶段接口设计原则：

1. 后台只提供“系统默认配置”能力
2. 用户个性偏好不走后端接口
3. 设置面板的用户操作默认只更新浏览器本地
4. 后台接口只服务于默认值管理和策略开关

### 5.2 系统默认配置接口

#### 5.2.1 查询系统默认 UI 配置

`GET /system/config/ui-defaults`

返回：

```json
{
  "defaults": {
    "themePreference": "system",
    "layout": "left",
    "sidebarAppearance": "classic",
    "themeColor": "#4B55FA",
    "showTagsView": true,
    "showAppLogo": true,
    "pageSwitchingAnimation": "fade-slide",
    "componentSize": "default"
  },
  "policy": {
    "allowCustomThemeColor": true,
    "allowUserLayoutSwitch": true,
    "allowUserSidebarAppearanceSwitch": true
  },
  "presets": [
    "#4B55FA",
    "#0F766E",
    "#C2410C",
    "#1D4ED8",
    "#BE185D"
  ]
}
```

#### 5.2.2 更新系统默认 UI 配置

`PUT /system/config/ui-defaults`

请求体：

```json
{
  "defaults": {
    "themePreference": "system",
    "layout": "left",
    "sidebarAppearance": "classic",
    "themeColor": "#4B55FA",
    "showTagsView": true,
    "showAppLogo": true,
    "pageSwitchingAnimation": "fade-slide",
    "componentSize": "default"
  },
  "policy": {
    "allowCustomThemeColor": true,
    "allowUserLayoutSwitch": true,
    "allowUserSidebarAppearanceSwitch": true
  },
  "presets": [
    "#4B55FA",
    "#0F766E",
    "#C2410C"
  ]
}
```

权限建议：

- `system:config:edit`

说明：

- 管理员通过该接口更新管理后台系统默认值
- 服务端将对象序列化写入 `sys_config`

### 5.3 兼容期接口策略

兼容期内可保留：

- `GET /system/config/configKey/sys.index.sideTheme`

但仅用于旧版本回退和迁移期兼容，不再作为主题系统最终契约的一部分。

本方案明确废弃以下写法：

- `PUT /system/config/key/sys.index.sideTheme`

原因：

- 这是用户偏好写入系统参数的旧设计
- 继续沿用只会让模型继续污染

---

## 6. 后端实现方案

### 6.1 推荐新增模块

建议在 `sourcelin-system` 中新增一个聚合型默认配置接口，而不是新增用户偏好模块。

推荐新增：

#### Controller

- `AdminUiConfigController`

#### Service

- `IAdminUiConfigService`
- `AdminUiConfigServiceImpl`

#### DTO

- `AdminUiDefaultsDTO`
- `AdminUiPolicyDTO`
- `AdminUiConfigAggregateDTO`

### 6.2 服务职责

#### `AdminUiConfigService`

负责：

- 从 `sys_config` 读取后台 UI 默认配置
- 解析 JSON
- 校验结构合法性
- 返回聚合配置对象
- 将后台默认配置写回 `sys_config`

#### Controller 层

只负责：

- 参数绑定
- 权限控制
- 调用 service

不要继续在 controller 中暴露零散 `configKey` 级接口来承载主题系统。

### 6.3 缓存策略

系统默认配置仍可复用现有 `sys_config` 的 Redis 缓存能力。

本阶段无需新增用户偏好缓存，因为用户偏好不入后端。

---

## 7. 前端实现方案

### 7.1 API 层调整

建议新增：

- `src/api/system/admin-ui-config.ts`

保留：

- `src/api/system/config.ts`

但不再由设置面板直接调用：

- `updateByKey("sys.index.sideTheme")`

### 7.2 store 层调整

建议重构 `useSettingsStore`，拆分为两类核心状态：

#### A. 系统默认配置

例如：

- `systemDefaults`
- `systemPolicy`
- `themePresets`

#### B. 浏览器本地偏好

例如：

- `localPreference`

#### C. 运行时有效配置

例如：

- `effectiveSettings`
- `resolvedTheme`

说明：

- `effectiveSettings` 通过 `computed` 合并得出
- 浏览器本地偏好仍由 `useStorage` 维护
- 后台默认配置只在启动时读取并缓存在 store 中

### 7.3 启动链路

建议后台应用启动时按以下顺序执行：

1. 读取系统默认配置接口
2. 读取浏览器本地偏好
3. 合并生成 `effectiveSettings`
4. 应用主题变量
5. 渲染布局

失败兜底策略：

- 系统默认配置接口失败：回退前端兜底默认值
- 本地偏好缺失：仅使用系统默认配置

### 7.4 设置面板行为调整

`LayoutSettings.vue` 应调整为：

1. 所有用户操作只更新浏览器本地偏好
2. 不再写任何后台用户偏好接口
3. `reset` 只清理浏览器本地偏好并回退到系统默认配置
4. “复制配置”功能改为导出当前有效配置 JSON，不再生成伪代码片段

### 7.5 本地存储建议

推荐将本地存储明确标注为：

- 当前浏览器偏好
- 非账号级同步数据

可选做法：

1. 继续保留现有离散 key
2. 收敛为单个聚合 key，如 `vea:ui:preference`

建议优先保持现有 key，不在本轮额外做大迁移，只统一 store 管理和读取优先级。

---

## 8. 迁移方案

### 8.1 Phase 0：冻结旧写法

目标：

- 不再新增新的 `sys.index.*` 主题写入逻辑
- 将 `sys.index.sideTheme` 标记为历史兼容项

动作：

- 停止扩展 `ConfigAPI.updateByKey` 在主题系统中的使用

### 8.2 Phase 1：补系统默认配置聚合接口

目标：

- 后台补齐 `GET/PUT /system/config/ui-defaults`

动作：

- 新增 `AdminUiConfigController`
- 新增 DTO 与 service
- 将默认值、策略、预设统一聚合输出

交付结果：

- 后台默认配置读取链路统一

### 8.3 Phase 2：前端改为“默认值 + 本地偏好”双层模型

目标：

- 前端启动时统一读取后台默认值
- 用户设置只写本地

动作：

- `settingsStore` 增加 `systemDefaults/systemPolicy/themePresets`
- `effectiveSettings` 按优先级合并
- 删除设置面板对 `sys.index.sideTheme` 的写回

交付结果：

- 主题模型闭环完成
- 后台只负责默认值，前端只负责个性偏好

### 8.4 Phase 3：清理兼容残留

目标：

- 将 `sys.index.sideTheme` 降级为历史兼容读取项

动作：

- 更新文档与实现
- 清理设置面板中的旧逻辑说明

## 8.5 实施结果（2026-04-17）

本轮已完成以下落地：

- 已新增 `GET /system/config/ui-defaults`
- 已新增 `PUT /system/config/ui-defaults`
- 已新增后端聚合服务与单测，默认配置读写统一落到：
  - `sys.admin.ui.defaults`
  - `sys.admin.ui.policy`
  - `sys.admin.ui.presets`
- 已补充 SQL 种子脚本：
  - `docs/sql/2026-04-17-admin-ui-defaults-seed.sql`
- 已同步更新总 SQL 文档：
  - `docs/sql/sourcelin-cloud.sql`
- 已新增前端默认配置 API 与类型契约：
  - `src/api/system/admin-ui-config.ts`
  - `src/types/api/admin-ui-config.ts`
- 已将 `settingsStore` 改造成：
  - 系统默认配置
  - 浏览器本地偏好
  - 运行时有效配置
- 已移除设置面板对 `sys.index.sideTheme` 的写回
- 已将“复制配置”收口为导出当前有效配置 JSON
- 已将组件尺寸并入同一套偏好模型，不再分散在 `appStore`
- 已将 `sys.index.sideTheme` 降级为历史兼容项，不再作为主题系统写入目标

---

## 9. 回滚方案

若升级过程中出现问题，回滚策略如下：

1. 保留现有 `ConfigAPI.getByKey` 能力不删
2. 新接口只新增，不破坏旧 `sys_config` 结构
3. 前端本地偏好结构保持兼容，不一次性大改 key 体系

回滚优先级：

1. 回退前端新 store 合并逻辑
2. 回退设置面板的默认值读取逻辑
3. 保留后台聚合接口，不影响旧接口继续运行

---

## 10. 验收标准

### 10.1 功能验收

- 用户切换主题、布局、侧边栏风格、主题色、页签、Logo、动画、组件尺寸后，刷新页面保持一致
- 清除浏览器本地偏好后，页面正确回退到系统默认配置
- 管理员更新系统默认 UI 配置后，新浏览器或已 reset 的浏览器能看到新默认值

### 10.2 架构验收

- 用户个性偏好不再写入 `sys_config`
- 前端不再调用 `updateByKey("sys.index.sideTheme")`
- `resolvedTheme` 只作为运行时计算结果
- 后台只负责默认值，浏览器只负责个性偏好

### 10.3 测试验收

- 后端接口测试：默认配置读取、更新、JSON 校验
- 前端类型检查：`pnpm run type-check`
- 前端构建：`pnpm run build`
- 前端交互测试：切换、reset、刷新、浏览器本地清理后的回退行为

---

## 11. 可选扩展：未来升级为账号级偏好同步

当前阶段不推荐直接建设账号级偏好表。

但若后续出现以下明确需求：

- 同账号经常跨浏览器、跨机器登录
- 用户强烈要求主题偏好跨设备同步
- 偏好配置数量持续增加，且对工作流影响明显

则可以在下一阶段扩展为：

- `sys_config` 继续管理系统默认配置
- 新增 `sys_user_preference` 管理账号级偏好
- 前端优先级改为：
  - `浏览器临时偏好 > 账号级偏好 > 系统默认配置 > 前端兜底默认值`

该能力应作为后续平台化升级项，而不是本轮默认实现。

---

## 12. 最终结论

对当前 `sourcelin-ui-admin` 来说，最合理的方案不是把整套用户偏好都放后台数据库，而是：

- 后台管理“系统默认配置”
- 浏览器本地管理“用户个性偏好”
- 前端运行时负责“主题解析与变量应用”

这样既能形成完整闭环，又不会把当前主题系统做得过重。
