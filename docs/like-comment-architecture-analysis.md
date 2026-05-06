# 点赞与收藏能力架构分析（文章 / 说说 / 树洞）

- 分析日期：2026-04-22
- 角色视角：企业级系统架构师
- 范围：`sourcelin-blog` + `sourcelin-ui-platform` + `sourcelin-ui-admin`
- 说明：本版已按要求从“点赞 vs 评论”调整为“点赞 vs 收藏”

## 1. 执行摘要

当前系统已完成点赞与收藏的同构收敛，形成统一互动域：

1. 收藏：统一到 `b_collect(user_id + target_type + target_id)`，支持 `article/say/treehole` 三目标幂等收藏与取消收藏。
2. 点赞：新增 `b_like_record(user_id + target_type + target_id)`，支持三目标幂等点赞与取消点赞。
3. 接口：统一为 `PUT/DELETE /front/interactions/*`，前台状态统一由服务端真实态回填。

结论：

- 点赞与收藏已具备统一语义、统一契约、统一治理入口。
- 当前代码与 SQL 基线已同步到点赞/收藏统一互动最终态。

## 2. 对比边界

本次仅对比“点赞”和“收藏”两类行为，不展开评论链路：

- 点赞对象：文章、说说、树洞。
- 收藏对象：文章、说说、树洞（当前已支持）。

## 3. 点赞现状分析（切换前问题基线）

### 3.1 文章点赞（中等可用）

关键链路：

- `POST /front/articles/like/{id}`
- `FrontArticleController#like`
- `BlogInteractionGuard#tryArticleLike`
- `ArticleMapper#updateLikeCount`

已有能力：

1. 登录校验。
2. Redis 限频 + 去重（TTL 30 天）。
3. 失败回滚去重键。

主要问题：

1. 去重是窗口型，不是永久唯一语义。
2. SQL 更新条件仅 `id`，未附 `deleted/status` 保护。
3. 详情 `isLike` 未对齐真实状态（固定 `false`）。

### 3.2 说说点赞（高风险）

关键链路：

- `POST /front/says/like/{id}`
- `FrontSayController#like`
- `SayMapper#updateLikeCount`

问题：

1. 无登录校验。
2. 无去重。
3. 无限频。
4. 无目标状态校验。

### 3.3 树洞点赞（高风险）

关键链路：

- `POST /front/treeholes/like/{id}`
- `FrontTreeholeController#like`
- `TreeholeMapper#updateLikeCount`

问题与说说点赞同级：无登录/去重/限频/状态校验。

## 4. 收藏现状分析（切换前问题基线）

### 4.1 已具备能力（优于点赞）

关键链路（切换前）：

- `POST /front/collects`
- `DELETE /front/collects/{id}`
- `FrontCollectController`
- `b_collect` 唯一键 `uk_user_target(user_id,target_type,target_id)`

已有能力：

1. 登录态强约束。
2. 用户-目标唯一语义（数据库唯一键）。
3. 取消收藏与恢复收藏闭环（软删 + 恢复）。
4. 前台列表支持 `type/state` 过滤，后台可管理。

### 4.2 主要缺口

1. 目标校验不足：新增收藏时未统一校验目标是否存在/可见/未删除。
2. 事务一致性不足：`insert/delete collect` 与 `article.collect_count` 变更在控制器层串行调用，非单事务闭环。
3. 计数口径不统一：当前只维护文章 `collect_count`；说说/树洞没有同级收藏计数口径。
4. 能力覆盖不对称：收藏尚未覆盖树洞，和点赞对象范围不一致。

## 5. 点赞 vs 收藏架构对比

| 维度 | 点赞 | 收藏 |
|---|---|---|
| 主体约束 | 文章有登录，说说/树洞弱 | 统一登录强约束 |
| 去重语义 | 依赖 Redis TTL 或缺失 | DB 唯一键强去重 |
| 幂等语义 | 弱（多为 POST + 累加） | 强（同用户同目标唯一） |
| 取消能力 | 无统一取消 | 已具备取消收藏 |
| 可审计性 | 弱（无点赞记录表） | 强（`b_collect` 可审计） |
| 计数一致性 | 说说/树洞高风险 | 中等（仍有事务缺口） |
| 企业级成熟度 | 中低 | 中高 |

判断：收藏是当前可复用“标准模型”，点赞应向收藏对齐。

## 6. 风险分级

### P0（立即处理）

1. 说说/树洞点赞可被脚本刷量（无后端防护）。
2. 点赞无持久行为记录，审计与风控追踪缺失。

### P1（近期处理）

1. 点赞与收藏接口语义不统一（POST 累加 vs 唯一行为）。
2. 收藏目标校验与计数更新事务边界不完整。
3. 前台 `isLike/isCollected` 状态回显来源不统一。

### P2（中期优化）

1. 树洞收藏能力缺失导致互动能力不对称。
2. 后台缺少“点赞与收藏联合治理”看板与异常审计。

## 7. 最终态实施方案（一次性切换，无兼容）

### 7.1 目标原则

1. 点赞与收藏统一为“用户行为记录模型”。
2. API 统一为幂等 `PUT/DELETE`。
3. 所有计数变更必须在服务层事务内完成。
4. 前台状态只以服务端真实态回显。

### 7.2 数据模型（最终态）

1. 新增 `b_like_record`
   - 字段：`id,user_id,target_type,target_id,state,created_time,updated_time,deleted`
   - 约束：`uk_user_target(user_id,target_type,target_id)`
   - 语义：`state=1` 点赞，`state=0` 取消点赞（或统一用 `deleted`）
2. 重构 `b_collect`（保留表名）
   - 与 `b_like_record` 语义对齐：幂等行为记录，不再由控制器拼装补丁逻辑
   - `target_type` 最终统一 `article/say/treehole`
3. 内容计数口径
   - `b_article.like_count/collect_count`
   - `b_say.like_count/collect_count`（新增）
   - `b_treehole.like_count/collect_count`（新增）

### 7.3 最终接口契约

前台（博客前台）：

1. `PUT /front/interactions/likes/{targetType}/{targetId}`
2. `DELETE /front/interactions/likes/{targetType}/{targetId}`
3. `PUT /front/interactions/collects/{targetType}/{targetId}`
4. `DELETE /front/interactions/collects/{targetType}/{targetId}`
5. `GET /front/interactions/targets/stat?targetType=...&targetIds=...`
   - 返回：`likeCount`、`collectCount`、`likedByMe`、`collectedByMe`

后台管理：

1. `GET /admin/interactions/actions`（点赞/收藏行为审计）
2. `GET /admin/interactions/dashboard/overview`（总览）
3. `PUT /admin/interactions/risk/rules`（限频策略）

### 7.4 后端实施（sourcelin-blog）

1. 新增互动域：`interaction`（controller/service/mapper/domain/dto/vo）。
2. 新增 `LikeRecord` 全链路，收藏链路收口到同构服务。
3. 下线旧接口：
   - `/front/articles/like/{id}`
   - `/front/says/like/{id}`
   - `/front/treeholes/like/{id}`
   - `/front/collects`（旧 `POST/DELETE` 形态）
4. 所有计数更新统一在事务内执行，SQL 必带目标状态条件。

### 7.5 博客前台实施（sourcelin-ui-platform）

1. 新增统一互动 API：`interaction.api.ts`。
2. `ArticlePage/SayPage/TreeholePage` 全部切到统一点赞/收藏调用。
3. 删除本地伪状态拦截：`liked/collected` 首屏均来自后端。
4. 用户中心收藏页支持 `article/say/treehole` 三类型。

### 7.6 后台管理实施（sourcelin-ui-admin）

1. 新增互动行为查询页（点赞/收藏合并维度）。
2. 收藏管理页升级为“目标对象统一视图”。
3. 新增互动总览页（点赞总量、收藏总量、日活行为用户、异常拦截率）。

### 7.7 切换与回滚（整包）

切换顺序：

1. 维护窗口停写。
2. 执行 DDL/DML（新表 + 旧数据迁移 + 全量计数重算）。
3. 部署后端新版本。
4. 部署前台与后台新版本。
5. 冒烟通过后放流。

回滚策略：

1. 下线新版本。
2. 恢复切换前数据库备份。
3. 恢复旧版本应用。

## 8. 验收标准（DoD）

1. 点赞/收藏均支持幂等 PUT/DELETE，重复请求无副作用。
2. 点赞与收藏在三类目标（article/say/treehole）能力一致。
3. 前台 `likedByMe/collectedByMe` 与后端真实态一致。
4. 后台可审计行为记录并追溯用户与目标。
5. 计数核对误差为 0（记录表聚合值 = 内容表展示值）。

## 9. 关键代码索引

- 点赞：
  - `sourcelin-modules/sourcelin-blog/src/main/java/com/sourcelin/blog/controller/front/FrontArticleController.java`
  - `sourcelin-modules/sourcelin-blog/src/main/java/com/sourcelin/blog/controller/front/FrontSayController.java`
  - `sourcelin-modules/sourcelin-blog/src/main/java/com/sourcelin/blog/controller/front/FrontTreeholeController.java`
  - `sourcelin-modules/sourcelin-blog/src/main/java/com/sourcelin/blog/service/BlogInteractionGuard.java`
- 收藏：
  - `sourcelin-modules/sourcelin-blog/src/main/java/com/sourcelin/blog/controller/front/FrontCollectController.java`
  - `sourcelin-modules/sourcelin-blog/src/main/java/com/sourcelin/blog/domain/Collect.java`
  - `sourcelin-modules/sourcelin-blog/src/main/resources/mapper/blog/CollectMapper.xml`
  - `docs/sql/sourcelin-cloud.sql`（`b_collect`）

## 10. 最终结论

本项目当前应以“收藏模型”为标准，推动点赞完成同构重构。完成后将形成：

1. 同一语义：点赞/收藏均为“用户-目标唯一行为”。
2. 同一协议：统一幂等接口与状态回显。
3. 同一治理：统一限频、审计、后台运营能力。

这条路线能在不引入临时兼容层的前提下，把点赞与收藏一起提升到企业级可运营基础设施。

## 11. 实施进展（2026-04-22）

已完成：

1. 前台统一互动路由落地：
   - `PUT/DELETE /front/interactions/likes/{targetType}/{targetId}`
   - `PUT/DELETE /front/interactions/collects/{targetType}/{targetId}`
   - `GET /front/interactions/targets/stat`
   - `GET /front/interactions/collects`
2. 旧前台收藏协议已下线：`FrontCollectController` 已删除。
3. 点赞/收藏服务已拆分为：
   - `IInteractionLikeService` / `InteractionLikeServiceImpl`
   - `IInteractionCollectService` / `InteractionCollectServiceImpl`
4. 后台互动治理接口已新增：
   - `GET /admin/interactions/dashboard/overview`
   - `GET /admin/interactions/actions`
5. 前台与后台 UI 已接入新契约：
   - platform 收藏列表改为 `/front/interactions/collects`
   - admin 新增互动总览页与互动记录页

已完成验证：

1. 后端编译：
   - `mvn -pl sourcelin-modules/sourcelin-blog -am -DskipTests compile` 通过。
2. 后端新增测试：
   - `mvn --% -pl sourcelin-modules/sourcelin-blog -am -Dtest=FrontInteractionControllerTest,InteractionLikeServiceImplTest,InteractionCollectServiceImplTest -Dsurefire.failIfNoSpecifiedTests=false test` 通过（13/13）。
3. 前端校验：
   - `sourcelin-ui-platform`：`npm run typecheck`、`npm run build` 通过。
   - `sourcelin-ui-admin`：`pnpm run type-check`、`pnpm run build` 通过。
4. 运行态冒烟：
   - 前台三目标点赞/收藏幂等（article/say/treehole）通过。
   - 后台 `overview/actions` 与收藏 `treehole` 过滤查询通过。

剩余事项：

1. 无阻塞项；发布后执行 `docs/sql/2026-04-22-like-collect-reconcile-check.sql` 做计数对账即可。
