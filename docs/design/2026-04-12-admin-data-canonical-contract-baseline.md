# `sourcelin-ui-admin` `ApiResponse.data` Canonical 协议基线

## 1. 文档目标

本文档只回答一个问题：  
**在保持统一响应外壳 `ApiResponse` 完全不变的前提下，管理后台应该如何定义 `ApiResponse.data` 内部的唯一 canonical 协议，并据此彻底拆除 Vue 3 管理端现存兼容层。**

本文档是 [API 契约与模块治理规则](../architecture/api-contract.md) 在管理后台 `ApiResponse.data` 层面的补充说明。

## 2. 边界与不变量

以下内容明确不动：

- [ApiResponse.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/domain/response/ApiResponse.java)
- [ApiResponseBodyAdvice.java](../../sourcelin-common/sourcelin-common-webmvc/src/main/java/com/sourcelin/common/core/web/advice/ApiResponseBodyAdvice.java)
- 登录协议保持现状
- 继续使用 RSA 公钥加密登录密码
- 微服务内部 Feign 协议 `R<T>` 保持现状

以下旧响应对象明确纳入清理范围：

- `AjaxResult.java`
- `TableDataInfo.java`
- [BaseController.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/controller/BaseController.java) 中与二者绑定的旧 helper

因此，本轮协议治理的唯一作用域是：

> `ApiResponse.data` 内部的业务对象、分页对象、列表对象、树对象、详情对象、提交对象与查询参数。

## 3. 现状判断

后端“统一响应外壳”其实已经完成，当前真正缺少的是“`data` 内部单一业务契约”。

从后端基线看，平台已经有两套可复用结果模型：

- [PageResult.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/domain/response/PageResult.java)
- [ListResult.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/domain/response/ListResult.java)

其中：

- `PageResult<T>` 已固定为 `items / total / page / pageSize / totalPages`
- `ListResult<T>` 已固定为 `items`

同时，system/blog 管理端控制器大多数已经按这一基线输出：

- 分页查询：`PageResult<T>`
- 纯列表：`ListResult<T>` 或 `List<T>`
- 详情：业务 VO / DTO / Entity
- 内部调用：`R<T>`

也就是说，**后端 canonical 雏形已经存在，真正的混乱主要发生在前端继续保留 legacy fallback。**

补充判断：

- `AjaxResult` 当前已不再承担真实管理端协议职责
- `TableDataInfo` 当前已不再承担真实管理端分页职责
- 它们继续保留，只会给新代码留下错误入口

## 4. Canonical 协议总则

### 4.1 总则一：外壳统一，内部单型

对浏览器管理端 `/system`、`/blog` 接口：

- HTTP JSON 外壳统一为 `ApiResponse<T>`
- 同一接口类型只允许一种 `data` 形态
- 前端不得再兼容第二种分页结构、第二种列表结构、第二种字段命名

### 4.2 总则二：查询类接口只允许三种返回类别

管理后台查询接口在 `data` 内只允许：

1. `PageResult<T>`
2. `ListResult<T>` 或明确语义的 `List<T>`
3. 单对象 `T`

不再允许：

- 同一接口一会儿返回 `items`，一会儿返回 `list`
- 同一列表页一会儿分页，一会儿平铺
- 同一详情页既返回对象又返回包装后的别名对象

补充强约束：

- 只要语义是“分页结果”，就必须返回 `PageResult<T>`
- 不允许为任何分页接口继续定义第二套分页响应类
- 不允许在分页接口上返回 `List<T>`、`ListResult<T>`、`Map<String, Object>`、`rows/total` 风格对象
- `PageResult<T>` 字段集合固定，禁止按模块自行扩展一套分页外壳

### 4.3 总则三：提交协议只允许 canonical 字段

提交对象、查询参数、详情对象字段必须统一到单一 canonical 名称。

不再允许：

- `userName` 与 `username` 并存
- `nickName` 与 `nickname` 并存
- `phonenumber` 与 `mobile` 并存
- `sex` 与 `gender` 并存
- `userId` 与 `id` 在同一业务域无约束混用
- `page` 与 `pageNum` 双写
- `items / list / rows` 多种列表字段并存

## 5. `data` 返回类别基线

## 5.1 分页列表基线

所有管理后台分页列表接口，`ApiResponse.data` 统一为 [PageResult.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/domain/response/PageResult.java)：

```json
{
  "items": [],
  "total": 0,
  "page": 1,
  "pageSize": 10,
  "totalPages": 0
}
```

架构要求：

- 后端统一使用 `PageResult<T>`
- 前端统一只消费 `items / total / page / pageSize / totalPages`
- 前端禁止再写 `items ?? list ?? rows`
- 前端禁止再从任意分页响应中猜测 `rows / list / records / current / size`
- 分页组件、分页状态、列表 hooks 与 API 类型必须全部以这 5 个字段为唯一来源

说明：

- 这不是新设计，而是对 [PageResult.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/domain/response/PageResult.java) 现有事实的严格落实
- 因而分页 canonical 已经确定，不再讨论“是否再抽另一套分页壳”
- 因此 `TableDataInfo.java` 不再有保留价值，应直接清理

## 5.2 非分页列表基线

对于菜单树选项、导航列表、字典选项、下拉源数据等非分页场景：

- 优先统一为 `ListResult<T>`
- 若现有接口已经稳定返回 `List<T>`，且语义极清晰，也可保留 `List<T>`

但必须满足：

- 同一接口永远只返回一种列表形态
- 前端消费点只写一种取值方式
- 禁止在前端同时兼容 `data.items` 与 `data`

建议：

- 新增或重构时优先使用 `ListResult<T>`
- 已稳定且被大量依赖的树接口、路由接口，可保留 `List<T>`

反向约束：

- `ListResult<T>` 不能被用于分页列表
- `List<T>` 不能被用于分页列表
- 任何“看起来像分页但只返回列表和 total”的变体都视为不合规

## 5.3 详情基线

详情接口统一要求：

- `ApiResponse.data` 直接返回唯一详情对象
- 不做同义字段双写
- 不做页面友好型别名复制

例如：

- 用户详情：只保留 `userId / userName / nickName / phonenumber / sex / remark`
- 字典详情：只保留 `dictCode / dictLabel / dictValue / dictType / remark`
- 说说详情：只保留实际业务状态字段，不再额外补一份前端别名状态

## 5.4 树与路由基线

树与路由是管理后台的特殊类，但也必须 canonical：

- 菜单管理列表：返回唯一菜单树 DTO
- 菜单下拉树：返回唯一 `TreeSelect` DTO
- 动态路由：返回唯一 `RouterVo` DTO

允许保留的转换：

- 后端菜单实体转前端路由 VO
- 平铺菜单组树

不允许保留的兼容：

- routeName 缺失时在前端猜测
- component 不存在时在前端静默裁剪并兜底
- 同一菜单 DTO 中混入实体字段、表单字段、路由字段三套语义

## 6. Canonical 查询参数基线

前端兼容层要真正拆干净，不能只统一返回体，还必须统一请求参数。

管理后台查询参数基线建议如下：

- 分页参数：`page`、`pageSize`
- 排序参数：`orderByColumn`、`isAsc` 或明确语义化字段，不得同域混用第二套命名
- 关键字查询：按业务语义命名，不使用泛滥的 `keywords`
- 主键字段：按业务域命名，不用裸 `id` 充当所有模块主键

强约束：

- `pageNum` 不再作为管理后台 canonical 查询字段
- `keywords` 不再作为通用基座默认查询字段
- 通用列表基座不得猜测 `userId/articleId/commentId/linkId`

说明：

- 这里是前端与管理端 controller/query DTO 的共同治理项
- 如后端当前已有稳定 query DTO，应直接以前者为准，不再让前端自己发明第二套别名

## 7. 各类接口的 canonical 归属

### 7.1 System 管理域

建议保留并强化现有后端返回事实：

- 用户、角色、岗位、字典、参数、公告、日志：`PageResult<T>`
- 菜单列表：唯一菜单树 DTO
- 菜单路由：唯一 `RouterVo`
- 当前用户资料：唯一 profile VO

System 域禁止的 legacy：

- `username` 代替 `userName`
- `nickname` 代替 `nickName`
- `gender` 代替 `sex`
- `mobile` 代替 `phonenumber`

### 7.2 Blog 管理域

建议保留并强化现有后端返回事实：

- 文章、分类、标签、评论、说说、树洞、导航、友链、关注、收藏、博客用户：`PageResult<T>`
- 门户导航栏等纯列表接口：`ListResult<T>`
- 各详情页：唯一详情对象

Blog 域禁止的 legacy：

- 在页面层把 `userStatus/isDisable/status` 混用
- 在页面层把 `sex/gender` 混用
- 在通用壳层通过 `items ?? list`、`id ?? userId` 吸收不一致

## 8. 前端必须删除的兼容层

以下内容属于必须删除的 legacy，而不是正常前端职责：

- `src/types/api/**` 中的 alias 字段
- `src/api/**` 中多结构 normalize fallback
- `src/api/blog/base.ts` 中对 `page/pageNum`、`items/list/rows` 的兼容
- `src/api/system/user.ts` 中对 `userName/username` 等别名并存
- `src/views/blog/shared/ModuleListShell.vue` 中：
  - `items ?? list`
  - `id/userId/articleId/commentId/linkId` 猜测
  - `keywords` 默认查询字段
  - `pageNum` 默认分页字段

删除后的前端应当只保留两类逻辑：

1. 请求调用
2. 确定性的展示投影

例如：

- 将 canonical 字段渲染到表格
- 将菜单树渲染为组件需要的格式
- 将字典值转标签文本

这些属于展示层职责，不属于兼容层。

## 8.5 后端必须删除的旧响应残留

本轮不是只清前端，后端 common 基座中也必须同步删掉旧协议入口：

- 删除 `AjaxResult.java`
- 删除 `TableDataInfo.java`
- 删除 [BaseController.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/controller/BaseController.java) 中以下旧 helper：
  - `getDataTable(List<?>)`
  - `success()`
  - `success(String)`
  - `success(Object)`
  - `error()`
  - `error(String)`
  - `warn(String)`
  - `toAjax(int)`
  - `toAjax(boolean)`

保留项只剩：

- `startPage()`
- `clearPage()`
- `toPageResult(List<T>)`
- `InitBinder` 中的日期绑定能力

## 9. 后端需要做的不是“改外壳”，而是“冻结 `data` 合同”

后端本轮需要承担的工作，不是重写 `ApiResponse`，而是：

- 逐域确认管理端 controller 的唯一返回类型
- 逐域确认 query DTO 的唯一字段名
- 清理同一接口的双形态 `data`
- 清理不必要的 Entity/VO/DTO 混用
- 对菜单、树、路由这类特殊响应给出唯一 DTO 规范
- 删除 `AjaxResult`、`TableDataInfo` 与 `BaseController` 中对应旧 helper
- 强制所有分页接口统一到 `PageResult<T>`，并禁止分页字段集漂移

后端本轮明确不做：

- 不改 `ApiResponse`
- 不改 `ApiResponseBodyAdvice`
- 不改登录协议
- 不把内部 Feign `R<T>` 强行拉平到管理端协议

## 10. 推荐落地顺序

建议按以下顺序冻结 canonical 合同并拆前端兼容：

1. system 用户域
2. system 字典域
3. system 菜单与路由域
4. blog 用户域
5. blog 内容管理域
6. 通用列表基座与 API 类型基座

原因：

- 用户、字典、菜单是后台协议的三条主轴
- 这三条先稳定，才能反向清空通用壳层 fallback
- 通用基座必须最后删，否则会在拆分过程中持续阻断业务页迁移

## 11. 完成态判定标准

兼容层是否真的拆干净，不看口号，只看下面五条是否同时成立：

1. 管理后台所有浏览器接口继续统一包在 `ApiResponse` 外壳里
2. 所有分页接口统一返回 `PageResult<T>`
3. 所有分页接口字段集严格固定为 `items / total / page / pageSize / totalPages`
4. 前端类型定义中不存在 alias 字段
5. 前端 API 层不存在 `?? list ?? rows`、`?? username`、`?? id` 之类 fallback
6. 通用壳层不再猜主键、猜分页字段、猜查询字段

只要任意一条不成立，兼容层就还没拆干净。

## 12. 最终结论

这次治理的核心不是“推翻后端响应体”，而是：

> 保持 `ApiResponse` 外壳完全稳定，以 `PageResult`、`ListResult` 和唯一业务 DTO 作为 `ApiResponse.data` 的 canonical 协议基线；然后由前端彻底删除所有 legacy alias 与 fallback。

所以，第二份文档的最终判断是：

- **统一响应外壳优秀且应保持不动**
- **真正要重定标准的是 `ApiResponse.data`**
- **兼容层拆除的主要代码工作量仍在前端**
- **但前提是后端先把 `data` 内合同冻结为唯一 canonical 形态**

这才是 `sourcelin-ui-admin` 从迁移态走向严格 canonical 管理端的正确路径。

