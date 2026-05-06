# 首页「发现」板块：分类筛选联动升级方案（高级评审优化版）

## 1. 评审结论

当前方案的主方向是正确的，尤其是这三点判断没有问题：

- 首页「发现」的分类与文章流确实存在认知割裂，应该从“跳转入口”升级为“同页筛选器”。
- 发现区列表应统一走 `getArticles`，不能让“全部”和“分类态”走两套数据路径。
- 视觉上应把分类与文章流收拢成一个玻璃容器，而不是继续维持上下两块拼装。

但原文档还不算完整闭环，主要缺口有 7 个：

1. **设计系统落点不够准确**：文档虽然提到 `SSurfaceChip`，但示例实现仍直接写原生 `button + sourcelin-chip-surface`，没有优先使用 UI 抽象层组件。
2. **文件路径与实际代码不一致**：项目实际是 `src/modules/article/api/article.api.ts` 和 `src/shared/components/ui/SSurfaceChip.vue`，不是文档里的 `article.service.ts` / `src/components/ui`。
3. **切换态方案不够稳**：原文要求切分类时立即清空旧列表，这会造成版面塌陷、骨架闪烁和瀑布流跳变。
4. **并发与回退未闭环**：缺少“快速连续切分类时旧请求回写新状态”的防护，也没定义 query 非法值、分类下线、浏览器前进后退的行为。
5. **归档入口设计过隐蔽**：右键/长按不适合作为主方案，桌面端发现成本高，移动端一致性也差。
6. **无障碍角色选型不够贴切**：这里是列表过滤，不是 tab 面板切换，优先用 `button + aria-pressed`，不建议伪装成 `tablist/tab`。
7. **零文章分类逻辑自相矛盾**：一边说 `count=0` 禁用，一边又说选中后展示空态，两者不能同时成立。

结论：**该方案可采纳，但必须按本文的收敛版执行，才能称得上项目内可直接落地的最优方案。**

---

## 2. 现状基线

### 2.1 当前真实代码结构

本次改造的实际落点以当前代码为准：

- 页面：`src/modules/home/pages/HomePage.vue`
- 首页组合逻辑：`src/modules/home/composables/useHomePage.ts`
- 分类区：`src/modules/home/components/HomeCategorySection.vue`
- 文章区：`src/modules/home/components/HomeArticlesSection.vue`
- 文章 API：`src/modules/article/api/article.api.ts`
- 玻璃 chip 组件：`src/shared/components/ui/SSurfaceChip.vue`

### 2.2 当前行为

- `HomeCategorySection` 使用 `SeamlessScroll` 做自动无缝滚动。
- 点击分类后执行 `useHomePage.goToCategory`，直接跳转 `/archive?categoryId=...`。
- 首页文章流来自 `getHome(...).latest`，失败时才回退到 `getArticles(...)`。
- `getArticles` 已支持 `categoryId`，但首页发现区没有使用这项能力。

### 2.3 当前问题本质

问题不在“接口能不能筛分类”，而在**首页发现区没有把分类视为本区状态的一部分**。  
现状是：

- 分类是“离开当前上下文”的导航行为；
- 列表是“与分类无关”的独立内容流；
- 两者虽然同属“发现”，却没有共享状态、共享反馈、共享容器。

这与项目当前首页已经形成的液态玻璃叙事方式不一致。

---

## 3. 一期最优方案

### 3.1 核心原则

一期只做前端改造，不依赖后端新增接口，但要把交互、状态、URL、异常、归档入口一次闭环。

最终原则如下：

1. **首页发现列表统一只走 `getArticles`。**
2. **`getHome` 继续负责首页元信息，不再承担发现列表展示来源。**
3. **分类切换不跳页，归档入口改为显性次级操作。**
4. **切换分类时保留当前版面骨架，不做整块清空。**
5. **所有交互组件优先使用 UI 抽象层，不直接落 Naive UI，也不手搓散落玻璃样式。**

### 3.2 最终交互模型

#### 默认态

- 首页进入后，发现区默认为“全部”。
- 文章流显示全站最新分页列表。
- 视觉与当前首页行为保持一致，但数据源切到 `getArticles({ page, pageSize })`。

#### 分类态

- 点击某个分类 chip，不离开首页。
- 发现区文章流切换为 `getArticles({ categoryId, page, pageSize })`。
- “加载更多”沿用当前分类继续翻页。

#### 归档入口

- 不再依赖右键/长按。
- 在筛选器行右侧提供一个显性次级动作：
  - 选中“全部”时：`全部归档`
  - 选中某分类时：`查看该分类归档`
- 点击后沿用现有路由：`/archive?categoryId=...`

这比隐藏式上下文菜单更符合首页的信息密度和项目的交互明确性。

---

## 4. 方案设计

### 4.1 数据职责重构

建议将首页发现相关职责拆成两层：

#### A. `useHomePage`

只保留首页壳层数据与导航职责：

- 站点信息
- masthead 背景与滚动模糊
- 公告 / 通知
- 轮播
- 精选
- 分类元数据
- 标签云
- 通用导航（打开文章、打开标签、打开归档）

**从这里移出：**

- `articles`
- `currentPage`
- `pageSize`
- `total`
- `loadingMore`
- `finished`
- `loadArticles`
- `loadMore`

#### B. `useDiscoverFeed`

新增 `src/modules/home/composables/useDiscoverFeed.ts`，专门维护发现区筛选与文章流状态：

- `selectedCategoryId`
- `articles`
- `loading`
- `switching`
- `loadingMore`
- `finished`
- `currentPage`
- `pageSize`
- `total`
- `selectCategory`
- `loadMore`
- `initDiscover`

### 4.2 类型与映射收口

当前 `HomeArticle` / `HomeCategory` 定义在 `useHomePage.ts` 内部，若继续新增 `useDiscoverFeed`，会导致类型和 normalizer 继续散落。

建议新增一个轻量模型文件，例如：

- `src/modules/home/model/home-discover.ts`

统一放：

- `HomeArticle`
- `HomeCategory`
- `normalizeHomeArticles`
- `normalizeHomeCategories`

这样可以避免：

- `useHomePage` 与 `useDiscoverFeed` 重复写 `normalizeArticles`
- `HomeArticlesSection` 继续从 composable 反向引用类型

这一步不是炫技，是为了让文档方案与后续代码组织保持稳定边界。

---

## 5. 发现区状态机

### 5.1 关键状态

```ts
selectedCategoryId: Ref<number | null>
loading: Ref<boolean>          // 首屏加载
switching: Ref<boolean>        // 分类切换中的轻遮罩态
loadingMore: Ref<boolean>
finished: Ref<boolean>
articles: Ref<HomeArticle[]>
currentPage: Ref<number>
total: Ref<number>
```

### 5.2 行为定义

#### 初始化

- 读取 `route.query.dc`
- 若为合法正整数，先记为 `pendingCategoryId`
- 等 `loadHome()` 拿到分类列表后校验：
  - 若分类存在，则设为初始选中
  - 若不存在，则回退到 `null` 并清理 query

这样可以避免 query 指向已删除分类时首页进入异常态。

#### 切换分类

- 若点击的是当前分类，直接返回
- 重置 `currentPage = 1`
- `finished = false`
- 更新 URL query：`router.replace`
- 发起新请求，完成后原子替换列表数据

#### 加载更多

- 若 `loadingMore` 或 `finished` 为 `true`，直接返回
- 将 `currentPage + 1`
- 仍带当前 `selectedCategoryId` 调用 `getArticles`

### 5.3 并发控制

原方案没有处理并发竞争，这在首页筛选器是实质风险。

必须增加“只提交最后一次请求结果”的保护。推荐做法：

- 在 `useDiscoverFeed` 内维护 `requestSeq`
- 每次请求前 `const seq = ++requestSeq.value`
- 请求返回后先判断 `if (seq !== requestSeq.value) return`

这样可解决：

- 用户连续点击多个分类时旧请求覆盖新结果
- 分类切换后旧的“加载更多”结果误写回当前列表

**不建议** 为分类切换加 200ms 防抖。筛选器应该即时响应，正确做法是并发保护，不是人为增加迟滞。

---

## 6. UI/UX 方案

### 6.1 组件结论

原 `HomeCategorySection` 不继续保留，直接替换为：

- `src/modules/home/components/HomeDiscoverFilter.vue`

该组件定位不是“滚动展示分类卡片”，而是“发现区状态筛选器”。

### 6.2 组件结构

```text
HomeDiscoverFilter
├── 全部 chip
├── 分类 chip 列表
└── 归档入口动作 chip
```

### 6.3 组件实现原则

- 使用 `SSurfaceChip`
- `variant="button"`
- 选中态使用 `tone="accent"`
- 计数使用 `variant="counter"` + `size="xs"`
- 不直接使用 `n-tabs` / `n-tag`
- 不直接手搓与 `SSurfaceChip` 重复的 button 基础形态

推荐模板方向：

```vue
<SSurfaceChip
  tag="button"
  variant="button"
  :tone="selected ? 'accent' : 'default'"
  :aria-pressed="selected"
>
  <span>{{ label }}</span>
  <SSurfaceChip variant="counter" size="xs">{{ count }}</SSurfaceChip>
</SSurfaceChip>
```

### 6.4 无障碍语义

这里是“过滤同一列表”，不是“切换不同 tabpanel”，推荐语义：

- 每个筛选项使用 `button`
- 用 `aria-pressed="true|false"` 表达选中
- 外层容器可加 `role="group"` 和 `aria-label="发现分类筛选"`

不建议强行使用：

- `role="tablist"`
- `role="tab"`

因为当前页面并不存在严格对应的 tab panel 结构。

### 6.5 布局与视觉

发现区改为一个完整玻璃单元：

```text
section.home-section-shell
├── HomeSectionPrelude title="发现"
├── HomeDiscoverFilter
└── HomeArticlesSection
```

调整原则：

- `.discover-stage` 的 gap 收紧，控制在 `var(--section-prelude-spacing)` 附近
- 分类筛选器与列表共享同一玻璃容器
- 分类行改为手动横向滚动，不再自动无缝轮播
- 使用边缘渐变遮罩提示可横向拖动

这一步是“视觉闭环”的关键，否则功能虽然联动了，观感仍然像两块拼件。

---

## 7. 切换态与空态设计

### 7.1 首屏加载

首屏仍使用当前发现区的骨架体系：

- 特色文章 skeleton
- `WaterfallList` 多列骨架

与 `home-discover-skeleton-analysis.md` 保持一致。

### 7.2 分类切换加载

原文档要求“清空旧列表 + skeleton”，不建议这么做。

最佳方案是：

- 首屏加载：完整 skeleton
- 分类切换：保留当前列表版式，进入 `switching` 态
- 在文章区表面叠一层轻量玻璃 veil / loading hint
- 新数据返回后整体替换

这样可以避免：

- 瀑布流整体塌陷
- 页面滚动位置突跳
- 用户误以为内容被清空

### 7.3 空态策略

零文章分类建议这样处理：

- `count === 0` 的分类 chip 保留展示，但默认禁用点击
- 视觉弱化，不作为主操作入口
- 空态仍要保留，但用于处理以下异常边界：
  - query 指向旧分类且后端已无数据
  - 分类文章数发生滞后，前端 count 非 0 但实际接口返回空
  - 后端内容临时下线

这样逻辑就闭环了，不再出现“既禁用又允许选中”的矛盾。

---

## 8. URL 与路由策略

### 8.1 URL 同步

一期纳入 query 同步，采用：

- `?dc=<categoryId>`

规则如下：

- `null` 表示全部，不写 `dc`
- 选中分类时写入 `dc`
- 使用 `router.replace`，不污染历史栈

### 8.2 路由回放

必须支持：

- 刷新后保持分类
- 浏览器后退/前进时同步恢复分类态

因此 `useDiscoverFeed` 不能只在点击时改状态，还要监听 route query 变化，保证地址栏和界面是双向同步的。

### 8.3 归档路由

归档页已有约定：

- `/archive?categoryId=<id>`

因此首页只需要复用现有路由，不新增首页到归档的特殊协议。

---

## 9. 一期实现规格

### 9.1 `useHomePage.ts`

改造目标：

- 保留 `loadHome`
- 保留 `loadHomeAnnouncements`
- 保留站点信息、分类元数据、标签、精选、轮播、导航
- 移除发现列表分页逻辑

启动方式建议改为：

```ts
onMounted(() => {
  getGuShi()
  void Promise.allSettled([
    loadHome(),
    loadHomeAnnouncements(),
    discoverFeed.initDiscover()
  ]).finally(() => {
    uiStore.markStartupHomeReady()
  })
})
```

注意点：

- `markStartupHomeReady()` 只保留一个收口点，不要在 `loadHome()` 内再次调用。
- `getHome()` 即使仍返回 `latest`，一期也只是不消费，不让它驱动 UI。

### 9.2 `useDiscoverFeed.ts`

新增文件：

- `src/modules/home/composables/useDiscoverFeed.ts`

职责：

- 初始化分类 query
- 分类切换
- URL 同步
- 并发控制
- 分页加载
- 切换态控制

### 9.3 `HomeDiscoverFilter.vue`

新增文件：

- `src/modules/home/components/HomeDiscoverFilter.vue`

职责：

- 渲染“全部 + 分类 + 归档动作”
- 管理筛选器横向滚动容器
- 输出选择事件与归档事件

### 9.4 `HomeArticlesSection.vue`

建议增强，不做重写：

- 新增 `switching?: boolean`
- 新增 `filterLabel?: string`
- 新增轻量切换遮罩态
- 保留首屏骨架逻辑
- 增加空态区块

建议表现：

- 首篇特色文章区继续保留
- 次级列表继续使用 `WaterfallList`
- 切分类时 `aria-busy` 能反映切换中状态

### 9.5 `HomePage.vue`

改造后发现区结构应为：

```vue
<section class="content-section home-section-shell ...">
  <HomeSectionPrelude title="发现" />

  <div class="discover-stage">
    <HomeDiscoverFilter
      :categories="displayCategories"
      :selected-category-id="discoverFeed.selectedCategoryId"
      :selected-category-name="discoverFeed.selectedCategoryName"
      @select="discoverFeed.selectCategory"
      @open-archive="goToArchive"
    />

    <HomeArticlesSection
      :loading="discoverFeed.loading"
      :switching="discoverFeed.switching"
      :loading-more="discoverFeed.loadingMore"
      :finished="discoverFeed.finished"
      :articles="discoverFeed.articles"
      :filter-label="discoverFeed.selectedCategoryName || '全部'"
      @load-more="discoverFeed.loadMore"
      @open-article="goToArticle"
      @open-tag="goToTag"
    />
  </div>
</section>
```

### 9.6 `HomeCategorySection.vue`

该文件不再作为首页发现区组件使用：

- 直接删除，或
- 迁移为历史备份后移除引用

一期建议直接删，避免死代码继续留存。

---

## 10. 二期优化

### 10.1 后端聚合

若后续需要进一步降低首页请求成本，可做二期：

- 为 `/front/home` 增加 `categoryId`
- 或新增轻量首页元数据接口，仅返回首页壳层所需信息

### 10.2 首页首屏冗余数据清理

当前即便前端不消费 `latest`，`getHome()` 仍可能返回一份列表分页数据。  
二期应推动接口职责收窄，避免首页首屏重复拉取：

- 一份 `getHome`
- 一份 `getArticles`

各自只负责自己的数据域。

---

## 11. 风险与对策

| 风险 | 对策 |
|------|------|
| 快速切分类导致旧数据回写 | `requestSeq` 并发保护，只提交最后一次请求 |
| 分类切换时页面跳动 | 保留旧列表，使用 `switching` 遮罩，不立即清空 |
| query 指向失效分类 | 分类列表加载后校验，不合法则回退到“全部”并清理 query |
| 浏览器后退前进状态错乱 | 监听 `route.query.dc`，而不是只监听点击事件 |
| 零文章分类交互歧义 | 展示但禁用；空态只处理数据不一致或异常边界 |
| 归档入口过深 | 用显性 action chip 替代右键/长按 |
| 与设计系统偏离 | 全部使用 `SSurfaceChip`、既有 token 和现有玻璃容器变量 |

---

## 12. 验收标准

- [ ] 首页进入后，发现区默认展示“全部”最新文章，行为与现网一致。
- [ ] 点击分类 chip 后，不离开首页，文章流切换为该分类结果。
- [ ] 分类态下继续点击“加载更多”，仍然沿用当前 `categoryId`。
- [ ] 点击“全部”后恢复全站文章流，并从第一页重新计算分页。
- [ ] 分类切换时不出现整块列表闪空，不出现旧请求覆盖新分类。
- [ ] 刷新、前进、后退后，分类选中态与 URL query 保持一致。
- [ ] 归档入口可见且明确，可从首页直接进入全部归档或分类归档。
- [ ] 发现区分类与文章流共享同一玻璃容器，视觉上形成单一内容单元。
- [ ] 业务层不直接引入 `n-*` 组件，无硬编码颜色，无 `:deep(.n-*)`。
- [ ] 文档提及的文件路径、组件名、API 文件名与实际仓库一致。

---

## 13. 实施顺序

### 阶段 1：数据边界收口

- 新增 `src/modules/home/model/home-discover.ts`
- 新增 `src/modules/home/composables/useDiscoverFeed.ts`
- 改造 `src/modules/home/composables/useHomePage.ts`

### 阶段 2：发现区 UI 替换

- 新增 `src/modules/home/components/HomeDiscoverFilter.vue`
- 改造 `src/modules/home/components/HomeArticlesSection.vue`
- 改造 `src/modules/home/pages/HomePage.vue`
- 移除 `src/modules/home/components/HomeCategorySection.vue`

### 阶段 3：路由与交互闭环

- 接入 `?dc=`
- 增加 route query 同步
- 增加归档 action chip
- 增加切换态遮罩与空态

### 阶段 4：验证

- 首页首屏
- 分类切换
- 连续快速点击
- 刷新恢复
- 浏览器前进后退
- 移动端横向筛选滚动

---

## 14. 最终建议

最优落地方案不是“把分类点击从跳页改成筛选”这么简单，而是同时完成 4 件事：

1. **把发现区改成单一状态单元**
2. **把列表统一到 `getArticles`**
3. **把切换过程从“清空重渲染”升级成“平滑换流”**
4. **把归档入口从隐藏操作改成显性操作**

只有这样，这次改造才既符合本项目的液态玻璃设计语言，也足够工程化、可维护、可验证。

---

*文档版本：2026-04-20 · 状态：高级评审后的一期最优落地方案*
