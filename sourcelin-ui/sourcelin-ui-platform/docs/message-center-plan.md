# 消息中心改造完整可执行方案

> 文档版本：v1.0 | 2026-04-08
> 适用项目：`sourcelin-ui-platform`（Vue 3 + Naive UI）、`sourcelin-blog`（Spring Boot 微服务）

---

## 一、背景与目标

当前 `/notice` 仅展示系统公告（`GET /front/notices`）。说明掘金、B 站等平台实践，将其升级为完整的**消息中心**，聚合用户的所有站内消息，提升用户粘性和互动感知。

### 消息频道规划

| 频道 ID | 频道名称 | 内容描述 | 后端依赖 |
|---------|---------|----------|---------|
| `system` | 系统公告 | 站务通知、功能更新 | 现有 `GET /front/notices` |
| `interaction` | 互动消息 | 评论了我的文章、回复了我的评论、@提及 | **需后端新增** |
| `star` | 赞与收藏 | 点赞了我的文章/评论、收藏了我的文章 | **需后端新增** |
| `follow` | 关注通知 | 新增粉丝 | **需后端新增** |

私信（私信会话）为独立模块，本方案**暂不包含**，预留扩展位。

---

## 二、分期实施

### 阶段 A — 前端壳（可独立交付，约 2 天）

**目标**：完成消息中心页面框架，系统公告频道可用，其余频道展示专业空态。

#### A1 路由变更

**文件**：`src/modules/notice/routes.ts`

```ts
// 新路由定义
export const noticeRoutes: AppRouteRecordRaw[] = [
  {
    path: '/messages',
    name: 'MessageCenter',
    component: () => import('./pages/MessageCenterPage.vue'),
    meta: { title: '消息中心', hidden: true }
  },
  {
    // 保留旧路径，重定向防止外链失效
    path: '/notice',
    redirect: '/messages',
    meta: { hidden: true }
  }
]
```

**同步更新**以下文件中 `{ name: 'Notice' }` 为 `{ name: 'MessageCenter' }`：
- `src/modules/home/components/HomeMarquee.vue`
- `src/modules/user/components/UserProfileSidebar.vue`

#### A2 消息频道配置

新建 `src/modules/notice/config/channels.ts`：

```ts
import type { Component } from 'vue'
import { appIcons } from '@/shared/components/ui/icons'

export type MessageChannel = 'system' | 'interaction' | 'star' | 'follow'

export interface ChannelConfig {
  id: MessageChannel
  name: string
  icon: Component
  description: string
  available: boolean   // false 则只展示空态
}

export const MESSAGE_CHANNELS: ChannelConfig[] = [
  {
    id: 'system',
    name: '系统公告',
    icon: appIcons.notice,
    description: '站务通知与系统公告',
    available: true
  },
  {
    id: 'interaction',
    name: '互动消息',
    icon: appIcons.comment,
    description: '评论、回复与@提及',
    available: false   // 阶段 B 后改为 true
  },
  {
    id: 'star',
    name: '赞与收藏',
    icon: appIcons.heart,
    description: '点赞与收藏动态',
    available: false
  },
  {
    id: 'follow',
    name: '关注通知',
    icon: appIcons.visitors,
    description: '新增粉丝通知',
    available: false
  }
]
```

#### A3 Composable 重写

新建 `src/modules/notice/composables/useMessageCenter.ts`（替代 `useNoticeCenter.ts`）：

```ts
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNotices } from '@/modules/notice/api/notice.api'
import { MESSAGE_CHANNELS, type MessageChannel } from '../config/channels'
import type { NoticeViewItem } from './useNoticeCenter'

export function useMessageCenter() {
  const route = useRoute()
  const router = useRouter()

  // URL query 同步频道
  const activeChannel = ref<MessageChannel>(
    (route.query.channel as MessageChannel) ?? 'system'
  )

  watch(activeChannel, (val) => {
    void router.replace({ query: { ...route.query, channel: val } })
  })

  const systemList = ref<NoticeViewItem[]>([])
  const systemLoading = ref(false)

  // 各频道加载函数（阶段 B 后替换为真实接口）
  async function loadSystem() {
    systemLoading.value = true
    try {
      const rows = await getNotices()
      systemList.value = rows.map((item) => ({
        id: Number((item as any).noticeId ?? 0),
        title: String((item as any).noticeTitle ?? ''),
        content: String((item as any).noticeContent ?? ''),
        createTimeStr: formatTime(String((item as any).createTime ?? ''))
      }))
    } finally {
      systemLoading.value = false
    }
  }

  const currentList = computed(() => {
    if (activeChannel.value === 'system') return systemList.value
    return []  // 其余频道阶段 B 填充
  })

  const currentLoading = computed(() =>
    activeChannel.value === 'system' ? systemLoading.value : false
  )

  function switchChannel(id: MessageChannel) {
    activeChannel.value = id
    if (id === 'system' && systemList.value.length === 0) {
      void loadSystem()
    }
  }

  onMounted(() => {
    if (activeChannel.value === 'system') void loadSystem()
  })

  return {
    channels: MESSAGE_CHANNELS,
    activeChannel,
    currentList,
    currentLoading,
    switchChannel
  }
}

function formatTime(time: string): string {
  if (!time) return ''
  const d = new Date(time)
  if (Number.isNaN(d.getTime())) return time
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
```

#### A4 页面结构（MessageCenterPage.vue 模板概要）

页面保持现有 `FeatureCard` 英雄区样式（与热榜、发文页一致）。

**英雄区调整**：
- 眉题 chip 改为「消息」
- 主标题「消息中心」
- 三块统计：「系统公告 N 条」「频道总数 4 个」「当前频道名」

**主体区布局**：
```
┌──────────────────────────────────────────────┐
│ 侧栏（240px）     │ 内容区                      │
│ ─ 系统公告 ✓     │ ┌──── 频道标题栏 ────────── │
│ ─ 互动消息 (即将) │ │  消息列表 / 空态组件        │
│ ─ 赞与收藏 (即将) │ │                            │
│ ─ 关注通知 (即将) │ └────────────────────────── │
└──────────────────────────────────────────────┘
```

#### A5 频道空态组件

新建 `src/modules/notice/components/ChannelComingSoon.vue`：

```vue
<template>
  <div class="channel-coming-soon sourcelin-panel-soft">
    <div class="coming-icon sourcelin-chip-surface sourcelin-chip-surface--accent">
      <SIcon :icon="icon" :size="32" />
    </div>
    <h3 class="coming-title">{{ title }}</h3>
    <p class="coming-desc">{{ desc }}</p>
    <span class="coming-badge sourcelin-chip-surface">即将开放</span>
  </div>
</template>
```

---

### 阶段 B — 后端新增通知接口（约 3-5 天）

#### B1 数据库表设计

```sql
CREATE TABLE `b_notification` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`         BIGINT        NOT NULL COMMENT '接收者用户 ID',
  `type`            VARCHAR(32)   NOT NULL COMMENT '通知类型（见下表）',
  `channel`         VARCHAR(20)   NOT NULL COMMENT '所属频道：interaction/star/follow',
  `actor_id`        BIGINT        DEFAULT NULL COMMENT '触发者用户 ID',
  `actor_nickname`  VARCHAR(64)   DEFAULT NULL COMMENT '触发者昵称（冗余）',
  `actor_avatar`    VARCHAR(255)  DEFAULT NULL COMMENT '触发者头像（冗余）',
  `target_id`       BIGINT        DEFAULT NULL COMMENT '关联对象 ID（文章/评论等）',
  `target_type`     VARCHAR(20)   DEFAULT NULL COMMENT '关联对象类型：article/comment/treehole/say',
  `target_title`    VARCHAR(255)  DEFAULT NULL COMMENT '关联对象摘要（冗余，避免 JOIN）',
  `content`         TEXT          DEFAULT NULL COMMENT '通知正文/评论内容',
  `is_read`         TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '是否已读（0未读 1已读）',
  `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted`         TINYINT(1)    NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_channel_read` (`user_id`, `channel`, `is_read`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内通知表';
```

**通知类型（`type` 字段枚举）**：

| type | 含义 | channel |
|------|------|---------|
| `ARTICLE_COMMENTED` | 有人评论了我的文章 | interaction |
| `COMMENT_REPLIED` | 有人回复了我的评论 | interaction |
| `ARTICLE_LIKED` | 文章被点赞 | star |
| `ARTICLE_COLLECTED` | 文章被收藏 | star |
| `USER_FOLLOWED` | 被新用户关注 | follow |

#### B2 前端 API 层

新建 `src/modules/notice/api/notification.api.ts`：

```ts
import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'

export type NotificationChannel = 'interaction' | 'star' | 'follow'

export interface NotificationItem {
  id: number
  type: string
  channel: NotificationChannel
  actorId: number
  actorNickname: string
  actorAvatar: string
  targetId: number
  targetType: string
  targetTitle: string
  content: string
  isRead: boolean
  createdAt: string
}

/** 获取分频道通知列表（分页） */
export function getNotifications(params: {
  channel: NotificationChannel
  page?: number
  pageSize?: number
}) {
  return requestData<PageResult<NotificationItem>>({
    url: '/front/notifications',
    method: 'get',
    params
  })
}

/** 批量标记已读 */
export function markNotificationsRead(params: {
  channel?: NotificationChannel  // 不传则全部标记
  ids?: number[]
}) {
  return requestData<void>({
    url: '/front/notifications/read',
    method: 'put',
    data: params
  })
}

/** 获取各频道未读数 */
export function getUnreadCount() {
  return requestData<{
    interaction: number
    star: number
    follow: number
    total: number
  }>({
    url: '/front/notifications/unread-count',
    method: 'get'
  })
}
```

#### B3 后端接口规范（API 文档）

**基础路径**：`/front/notifications`（需登录，通过 JWT 取 userId）

---

**接口 1：获取通知列表**

```
GET /front/notifications
```

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| channel | String | 是 | interaction / star / follow |
| page | Integer | 否 | 默认 1 |
| pageSize | Integer | 否 | 默认 20，最大 50 |

响应（`PageResult<NotificationVO>`）：

```json
{
  "code": 200,
  "data": {
    "items": [
      {
        "id": 1001,
        "type": "ARTICLE_COMMENTED",
        "channel": "interaction",
        "actorId": 88,
        "actorNickname": "张三",
        "actorAvatar": "https://cdn.example.com/avatar/88.jpg",
        "targetId": 200,
        "targetType": "article",
        "targetTitle": "如何学好 Vue 3",
        "content": "写得真好，学到了！",
        "isRead": false,
        "createdAt": "2026-04-08 10:30:00"
      }
    ],
    "total": 42,
    "page": 1,
    "pageSize": 20,
    "totalPages": 3
  }
}
```

---

**接口 2：标记已读**

```
PUT /front/notifications/read
```

请求体（JSON）：

| 字段 | 类型 | 说明 |
|------|------|------|
| channel | String | 可选，不传则全频道已读 |
| ids | Array<Long> | 可选，不传则该频道全部已读 |

响应：`{ code: 200 }`

---

**接口 3：获取未读数**

```
GET /front/notifications/unread-count
```

响应：

```json
{
  "code": 200,
  "data": {
    "interaction": 3,
    "star": 0,
    "follow": 1,
    "total": 4
  }
}
```

---

#### B4 后端实现要点

1. **Controller 层**：`FrontNotificationController`（路径 `front/notifications`，需登录鉴权）

2. **Service 层**：`INotificationService`，提供：
   - `queryByChannel(Long userId, String channel, PageDomain page)`
   - `markRead(Long userId, String channel, List<Long> ids)`
   - `countUnread(Long userId)` → 返回各频道计数 Map

3. **事件触发（推荐用 Spring ApplicationEvent 解耦）**：

   | 触发位置 | 事件类 | 写入 type |
   |---------|--------|----------|
   | `FrontCommentController.add()` | `ArticleCommentedEvent` / `CommentRepliedEvent` | ARTICLE_COMMENTED / COMMENT_REPLIED |
   | 文章点赞接口（待确认位置）| `ArticleLikedEvent` | ARTICLE_LIKED |
   | `FrontCollectController`（收藏动作）| `ArticleCollectedEvent` | ARTICLE_COLLECTED |
   | `FrontFollowController`（关注动作）| `UserFollowedEvent` | USER_FOLLOWED |

   事件 listener 异步处理（`@Async`），避免阻塞主链路。

4. **自通知过滤**：写入时检查 `actorId != userId`（不给自己发通知）。

5. **冗余字段策略**：写入时即将昵称、头像、文章标题等冗余到 `b_notification`，避免查询时多表 JOIN。

---

### 阶段 C — 增强功能（P1，后续迭代）

#### C1 Header 未读 badge

在 `AppHeader.vue` 的「消息中心」入口旁展示未读总数角标。

- 前台加载完成后调用 `GET /front/notifications/unread-count`
- 存储到 Pinia store（`ui.store.ts` 或新增 `message.store.ts`）
- 进入消息中心后，切换频道时自动 `PUT /front/notifications/read`（channel）
- badge 显示规则：总数 > 99 则显示 "99+"

#### C2 私信频道（独立评估）

私信需要完整的对话模型（会话 + 消息），工作量显著大于通知型消息，建议单独立项。

技术选型说明：
- 存储：`b_conversation`（会话表）+ `b_chat_message`（消息表）
- 实时推送：Spring WebSocket / SSE（轻量首选）
- 前端：抽成独立 `src/modules/chat/` 模块

---

## 三、文件变动清单

### 阶段 A 前端

| 操作 | 文件路径 |
|------|---------|
| 修改 | `src/modules/notice/routes.ts` |
| 新建 | `src/modules/notice/config/channels.ts` |
| 新建 | `src/modules/notice/composables/useMessageCenter.ts` |
| 新建 | `src/modules/notice/pages/MessageCenterPage.vue` |
| 新建 | `src/modules/notice/components/ChannelComingSoon.vue` |
| 新建 | `src/modules/notice/components/MessageList.vue`（复用 NoticeList 并泛化） |
| 修改 | `src/modules/home/components/HomeMarquee.vue`（name: Notice → MessageCenter） |
| 修改 | `src/modules/user/components/UserProfileSidebar.vue`（name: Notice → MessageCenter） |
| 保留 | `src/modules/notice/pages/NoticePage.vue`（可删除或保留为别名） |
| 保留 | `src/modules/notice/composables/useNoticeCenter.ts`（阶段 A 后可逐步废弃） |

### 阶段 B 前端

| 操作 | 文件路径 |
|------|---------|
| 新建 | `src/modules/notice/api/notification.api.ts` |
| 修改 | `src/modules/notice/composables/useMessageCenter.ts`（接入真实接口） |
| 修改 | `src/modules/notice/config/channels.ts`（将三个频道 available 改为 true） |
| 修改（可选）| `src/stores/` 新增 `message.store.ts`（未读数全局状态） |

### 阶段 B 后端（sourcelin-blog 微服务）

| 操作 | 路径 |
|------|------|
| 新建 SQL | `sql/alter/message_center/001_create_b_notification.sql` |
| 新建 | `domain/Notification.java` |
| 新建 | `vo/NotificationVO.java` |
| 新建 | `mapper/NotificationMapper.java` + `resources/mapper/NotificationMapper.xml` |
| 新建 | `service/INotificationService.java` + `impl/NotificationServiceImpl.java` |
| 新建 | `controller/front/FrontNotificationController.java` |
| 新建 | `event/ArticleCommentedEvent.java`、`CommentRepliedEvent.java` 等 5 个事件类 |
| 新建 | `event/NotificationEventListener.java`（@Async 处理所有通知事件） |
| 修改 | `FrontCommentController.add()`（发布事件） |
| 修改 | `FrontCollectController`（发布收藏事件） |
| 修改 | `FrontFollowController`（发布关注事件） |
| 修改 | 文章点赞接口（发布点赞事件，需确认具体位置） |

---

## 四、UI 设计规范

遵循项目现有 `FeatureCard` 英雄区 + 侧栏 + 面板模式（同 HotPage / NoticePage 已改版）。

**频道侧栏增强**（相对现有 NoticeMenu.vue）：

```
┌─────────────────────────┐
│ [icon] 系统公告    [5]   │ ← badge 数字（阶段 B 后启用）
│ [icon] 互动消息  [即将]  │
│ [icon] 赞与收藏  [即将]  │
│ [icon] 关注通知  [即将]  │
└─────────────────────────┘
```

**消息项卡片**（MesssageList 通用）：
- 左侧：触发者头像（系统公告为品牌图标）
- 中间：昵称 + 动作描述（如「评论了你的文章《...》」）
- 右侧：相对时间 + 删除按钮
- 点击跳转：跳转至对应内容页（文章详情 / 个人主页等）

---

## 五、兼容与边界

1. **未登录访问消息中心**：强制跳转登录页（`router.push('/login')`），此逻辑在 `useMessageCenter.ts` 中实现
2. **旧路由 `/notice` 兼容**：通过 `redirect` 静默重定向，不展示给用户
3. **系统公告无需登录**：`GET /front/notices` 为匿名接口，阶段 A 系统公告频道无需鉴权
4. **阶段 B 接口鉴权**：`GET /front/notifications` 强制需要 JWT，通过 `headers: { isToken: true }` 发送
5. **通知量上限**：B 端可配置单用户最多保留 200 条，超出自动清理最老记录（FIFO）

---

## 六、优先级与工时估算

| 阶段 | 工时 | 说明 |
|------|------|------|
| A 前端壳 | 1.5~2 天 | 改路由、拆 composable、空态组件、页面重构 |
| B 后端 | 2~3 天 | 建表、CRUD、事件钩子 5 个位置 |
| B 前端接入 | 0.5 天 | 替换真实接口、开放频道 |
| C1 Header badge | 0.5 天 | Pinia store + AppHeader 改造 |
| C2 私信 | 5+ 天 | 独立评估，不含在本方案中 |

---

*本方案由 AI 生成，执行前请与团队确认后端事件机制与冗余字段策略。*
