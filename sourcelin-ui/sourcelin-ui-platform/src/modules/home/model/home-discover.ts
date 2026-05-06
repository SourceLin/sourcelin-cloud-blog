import type { ArticleListItem } from '@/modules/article/api/article.api'
import type { CategoryOption } from '@/modules/article/api/category.api'
import defaultCover from '@/assets/hero.png'

export interface HomeArticle {
  id: number
  title: string
  summary: string
  image: string
  date: string
  authorName: string
  views: number
  comments: number
  tags: string[]
  featured: boolean
}

export interface HomeCategory {
  id: number
  name: string
  icon: string
  count: number
  hue: number
}

function normalizeDate(value: unknown): string {
  const text = typeof value === 'string' ? value : ''
  return text ? text.split(' ')[0] : ''
}

function toString(value: unknown, fallback = ''): string {
  return typeof value === 'string' ? value : fallback
}

export function normalizeHomeArticles(list: ArticleListItem[] | undefined): HomeArticle[] {
  if (!Array.isArray(list)) {
    return []
  }

  return list.map((item) => ({
    id: Number(item.id) || 0,
    title: String(item.title || '未命名文章'),
    summary: String(item.summary || ''),
    image: String(item.avatar || defaultCover),
    date: normalizeDate(item.createTime),
    authorName: toString(item.author, '平台作者'),
    views: Number(item.viewCount || 0),
    comments: Number(item.commentCount || 0),
    tags: String(item.tagNames || '')
      .split(/[，,]/)
      .map((tag) => tag.trim())
      .filter(Boolean),
    featured: item.isTop === 1 || item.isRecommend === 1
  }))
}

export function normalizeHomeCategories(list: CategoryOption[] | undefined): HomeCategory[] {
  if (!Array.isArray(list)) {
    return []
  }

  return list.map((item, index) => ({
    id: Number(item.id) || index + 1,
    name: String(item.name || '未命名分类'),
    icon: String(item.icon || 'folder'),
    count: Number(item.articleCount || 0),
    hue: (index * 46) % 360
  }))
}
