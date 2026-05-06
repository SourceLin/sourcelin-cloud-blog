# `sourcelin-ui-admin` 分页请求参数 Canonical 收敛方案

## 1. 结论

管理后台与博客后台对浏览器开放的分页请求参数，统一收敛为：

- `page`
- `pageSize`

这是唯一 canonical 方案。  
**不允许继续保留 `pageNum` 作为兼容入口。**

本方案与当前 API 契约文档形成闭环：

- [API 契约与模块治理规则](../architecture/api-contract.md)
- [2026-04-12-admin-data-canonical-contract-baseline.md](./2026-04-12-admin-data-canonical-contract-baseline.md)

## 2. 为什么不能继续保留 `page/pageNum` 双入口

只要请求参数层同时保留：

- `page`
- `pageNum`

就仍然属于兼容层，而不是 canonical 协议。

这会带来四个问题：

1. common 分页基座必须继续做双参数解析
2. 前端请求层会继续保留双写或 fallback
3. controller 与 query DTO 无法声明唯一协议
4. 文档、测试、前后端联调都会持续存在二义性

因此，既然目标已经明确为“**不允许出现兼容层**”，那么 `pageNum` 不能被保留为对外管理端协议的一部分。

## 3. 为什么 canonical 选 `page` 而不是 `pageNum`

选择 `page` 的理由不是“前端框架偏好”，而是协议闭环更自然：

### 3.1 与分页返回体对称

分页返回已经确定统一为 [PageResult.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/domain/response/PageResult.java)：

- `items`
- `total`
- `page`
- `pageSize`
- `totalPages`

如果请求仍使用 `pageNum`，就会出现：

- 请求：`pageNum`
- 响应：`page`

这本身就是协议不对称。

### 3.2 与现代前后端约定更一致

`page` / `pageSize` 更接近通用 REST 查询习惯，也更容易在浏览器、接口文档、测试脚本、前端 hooks 中达成一致。

### 3.3 与 UI 组件的映射成本最低

虽然 Element Plus、Naive UI 的分页组件事件命名不完全相同，但它们都可以非常自然地映射到：

- `page`
- `pageSize`

因此 UI 技术栈不应反向决定协议命名。

### 3.4 `pageNum` 的唯一价值只剩历史包袱

当前 `pageNum` 之所以还存在，只是因为历史基座继承自旧sourcelin风格，并不代表它是更好的新协议。

所以结论很明确：

> 在“彻底拆除兼容层”的前提下，分页请求参数只能选 `page`，不能继续选 `pageNum`。

## 4. 最终 canonical 约束

浏览器管理端分页查询参数统一为：

- `page`
- `pageSize`
- `orderByColumn`
- `isAsc`
- `reasonable`

其中：

- `page`：页码，从 1 开始
- `pageSize`：每页条数
- `orderByColumn`：排序字段
- `isAsc`：`asc` / `desc`
- `reasonable`：是否合理化分页参数

强约束：

- `pageNum` 禁止继续作为管理端对外协议字段
- `current`、`size`、`limit`、`offset` 禁止引入为第二套分页协议
- 前端、后端、网关、接口文档、测试脚本统一使用这套字段

## 5. 需要调整的代码位置

## 5.1 common 分页基座

必须调整：

- [PageDomain.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/page/PageDomain.java)
- [TableSupport.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/page/TableSupport.java)
- [PageUtils.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/utils/PageUtils.java)
- [BaseController.java](../../sourcelin-common/sourcelin-common-core/src/main/java/com/sourcelin/common/core/web/controller/BaseController.java)

调整目标：

- `PageDomain` 内部 canonical 字段改为 `page`
- `TableSupport` 只解析 `page`
- `PageUtils` 只读取 `page`
- `BaseController#toPageResult` 传出 `page`

## 5.2 controller 与 query DTO

必须保证：

- controller 不再隐式兼容 `pageNum`
- 若当前 query 对象存在 `pageNum` 语义字段，应同步清理或改名
- 对浏览器接口暴露的分页参数说明统一改为 `page`

## 5.3 前端管理后台

必须保证：

- 所有列表请求只发送 `page`
- 删除 `pageNum` 双写
- 删除从组件状态到 `pageNum` 的映射残留
- 删除 `src/api/**`、`src/types/**` 中的 legacy 分页别名

## 5.4 文档与测试

必须同步修正：

- 接口矩阵文档
- UAT / 审计 / 评审文档中的历史分页口径
- 自动化测试脚本中的历史分页参数名

## 6. 禁止的“伪闭环”方案

以下方案全部不允许：

### 方案一：后端同时收 `page` 和 `pageNum`

这是兼容层，不是 canonical。

### 方案二：前端只发 `page`，后端内部仍偷偷兼容 `pageNum`

这只是把兼容层藏到 common 基座里，问题没有消失。

### 方案三：system 用 `page`，blog 继续用 `pageNum`

这会把后台拆成两套协议，架构上不可接受。

### 方案四：文档写 `page`，代码实际继续支持 `pageNum`

这会制造“文档正确、代码错误”的假闭环。

## 7. 完成态判定标准

分页请求参数协议是否真正闭合，只看这五条：

1. common 基座不再解析 `pageNum`
2. 浏览器管理端所有分页接口只接受 `page`
3. 前端请求层不存在 `pageNum`
4. 文档与测试样例不存在 `pageNum`
5. 任何新增分页接口若出现 `pageNum`，直接视为违规

少一条都不算闭合。

## 8. 最终结论

既然要求是“**不允许出现兼容层**”，那么分页请求参数的最终选择就不能模糊：

> 管理后台分页请求参数唯一 canonical 为 `page` 与 `pageSize`；`pageNum` 必须从 common 基座、controller、前端请求层、文档与测试样例中彻底移除。

只有这样，分页协议才能和 `PageResult<T>` 一起形成真正闭环：

- 请求：`page / pageSize`
- 响应：`items / total / page / pageSize / totalPages`

这是唯一完整、对称、可持续的分页协议。

