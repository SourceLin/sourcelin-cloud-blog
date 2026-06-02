export interface SubscriptionTemplateMeta {
  id: string;
  name: string;
  scene: string;
  keywords: string[];
}

export const SUBSCRIPTION_TEMPLATE_META_MAP: Record<string, SubscriptionTemplateMeta> = {
  y24CeVVFAMDOvRrmMzp7TK4spP8QFG5eEeiT0pxXazM: {
    id: 'y24CeVVFAMDOvRrmMzp7TK4spP8QFG5eEeiT0pxXazM',
    name: '留言回复通知',
    scene: '文章评论回复提醒',
    keywords: ['文章标题', '回复人', '回复内容', '留言时间', '留言内容', '回复时间']
  }
};

export function resolveSubscriptionTemplateMeta(templateId: string): SubscriptionTemplateMeta {
  return SUBSCRIPTION_TEMPLATE_META_MAP[templateId] || {
    id: templateId,
    name: '订阅消息模板',
    scene: '站内消息提醒',
    keywords: []
  };
}
