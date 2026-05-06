# 收藏模型全量重构方案（无兼容/无迁移）

## 1. 目标与约束

### 1.1 目标
- 将收藏能力从“仅文章收藏”升级为“多类型收藏（文章、说说）”。
- 个人中心收藏支持按“全部、文章、说说”分类筛选。
- 说说列表支持收藏/取消收藏，并回显收藏态。

### 1.2 强约束
- 不做历史兼容：旧字段、旧入参、旧返回结构直接下线。
- 不做迁移脚本：按未上线项目处理，直接重建/替换收藏模型。
- 必须闭环落地：数据库、后端、前台、后台管理、权限判定链路全部完成。

## 2. 范围

### 2.1 本次包含
- 数据库 `b_collect` 结构重建。
- `Collect` 领域模型与 Mapper/Service/Controller 全量改造。
- 文章详情收藏链路改造（含 `READ_AUTH_AFTER_COLLECT` 判定）。
- 说说收藏能力新增（列表返回收藏态 + 收藏接口接入）。
- 用户中心收藏分类切换（全部/文章/说说）。
- 管理后台收藏管理字段与筛选重构。

### 2.2 本次不包含
- 树洞/评论收藏。
- 收藏通知推送。
- 历史数据迁移、灰度兼容、双写。

## 3. 数据模型

## 3.1 b_collect 新结构
- `id` bigint PK
- `user_id` bigint NOT NULL
- `target_type` varchar(20) NOT NULL（`article` / `say`）
- `target_id` bigint NOT NULL
- `deleted` int default 0
- `create_time` datetime NOT NULL
- `update_time` datetime NULL
- `create_by` varchar(64) NULL
- `update_by` varchar(64) NULL
- `remark` varchar(500) NULL

## 3.2 约束与索引
- 唯一约束：`uk_user_target(user_id, target_type, target_id)`。
- 索引：
  - `idx_user_type_time(user_id, target_type, create_time)`
  - `idx_target(target_type, target_id, deleted)`

## 3.3 Java 实体
`Collect.java` 字段改为：
- `id`
- `userId`
- `targetType`
- `targetId`
- `deleted`
- `article`（当 `targetType=article` 时填充）
- `say`（当 `targetType=say` 时填充）

## 4. API 契约

## 4.1 前台收藏接口

### POST /front/collects
请求体：
```json
{
  "targetType": "article",
  "targetId": 86313
}
```
返回：
```json
{
  "collectId": 1001,
  "collected": true
}
```

### GET /front/collects
查询参数：
- `page`
- `pageSize`
- `type=all|article|say`
- `state=all|active|inactive`
- `userId`（可选）

返回项新增：
- `targetType`
- `targetId`
- `active`
- `article`（可空）
- `say`（可空）

### DELETE /front/collects/{id}
- 取消收藏记录。
- 若为文章收藏，维护 `b_article.collect_count`。
- 若为说说收藏，不维护额外计数（本次不新增 say.collect_count）。

## 4.2 前台内容接口
- `GET /front/articles/{id}`：收藏态判定改为按 `targetType=article,targetId=id`。
- `GET /front/says`：每条说说新增 `isCollected`、`collectId`（登录态下返回）。

## 5. 后端改造清单

### 5.1 必改文件
- `domain/Collect.java`
- `mapper/CollectMapper.java`
- `mapper/blog/CollectMapper.xml`
- `service/ICollectService.java`
- `service/impl/CollectServiceImpl.java`
- `controller/front/FrontCollectController.java`
- `controller/front/FrontArticleController.java`
- `service/impl/ArticleReadAuthServiceImpl.java`
- `controller/front/FrontSayController.java`
- `controller/admin/CollectController.java`
- `docs/sql/sourcelin-cloud.sql`

### 5.2 新增能力
- `selectCollectByUserAndTarget(userId, targetType, targetId)`
- `countCollectByUserIdAndType(userId, targetType)`（如统计需要）
- 收藏列表联表查询 article/say 双分支

## 6. 前端改造清单（platform）

### 6.1 API
- `collect.api.ts` 改造为 `targetType + targetId` 协议。
- 收藏列表查询增加 `type=all|article|say`。

### 6.2 页面与交互
- `useArticleDetail.ts`：收藏/取消改用新入参。
- `say.api.ts` + `say.types.ts`：接入 `isCollected/collectId`。
- `SayFeedCard.vue`：新增说说收藏按钮与交互。
- `useUserCenter.ts` + `UserPage.vue`：收藏筛选切换改为“全部/文章/说说”。
- `UserCollectList.vue`：兼容双类型卡片展示。

## 7. 管理后台改造清单（admin）
- `CollectItem` 类型新增：`targetType/targetId/say`。
- 收藏管理查询条件改为 `userId + targetType + targetId`。
- 列表展示目标类型与目标标题（文章标题或说说摘要）。

## 8. 执行顺序
1. 重建 `b_collect` 与 SQL 文档。
2. 后端 `Collect` 全链路改造并编译通过。
3. 文章详情收藏链路改造并验证。
4. 说说收藏能力接入并验证。
5. 用户中心收藏分类与展示改造。
6. 管理后台收藏管理改造。
7. 前后端联合回归。

## 9. 验收标准
- 文章可收藏/取消，文章详情收藏态准确。
- 说说可收藏/取消，列表收藏态准确。
- 用户中心收藏支持“全部/文章/说说”且分页正确。
- `state=inactive` 可看到失效目标记录。
- 管理后台收藏列表可按目标类型筛选。
- 前端类型检查通过，后端模块编译通过。

## 10. 风险与处理
- 风险：老代码残留 `articleId` 导致编译或运行错误。
- 处理：全仓检索 `Collect.articleId`、`selectCollectByUserAndArticle`、`article_id` 并清零。

- 风险：说说列表收藏态 N+1 查询。
- 处理：批量查询当前页 `sayId` 对应收藏关系并回填。

- 风险：管理后台字段切换导致查询失效。
- 处理：同步更新 admin API 类型、查询表单和列表列定义。