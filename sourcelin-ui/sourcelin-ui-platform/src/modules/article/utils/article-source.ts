import type { RouteLocationNormalizedLoaded, RouteLocationRaw } from 'vue-router'

type ArticleSourceType = 'category' | 'tag' | 'archive'

type SourceRouteContext = {
  label: string
  to: RouteLocationRaw
}

type ArchiveSourceQuery = {
  categoryId?: number | null
  categoryName?: string
}

function readQueryValue(value: unknown): string {
  if (Array.isArray(value)) {
    return typeof value[0] === 'string' ? value[0].trim() : ''
  }

  return typeof value === 'string' ? value.trim() : ''
}

export function buildCategoryArticleRoute(articleId: number, categoryName: string): RouteLocationRaw {
  return {
    path: `/article/${articleId}`,
    query: {
      from: 'category',
      name: categoryName
    }
  }
}

export function buildTagArticleRoute(articleId: number, tagName: string): RouteLocationRaw {
  return {
    path: `/article/${articleId}`,
    query: {
      from: 'tag',
      name: tagName
    }
  }
}

export function buildArchiveArticleRoute(articleId: number, context: ArchiveSourceQuery = {}): RouteLocationRaw {
  const query: Record<string, string> = {
    from: 'archive'
  }

  if (context.categoryId != null && context.categoryId > 0) {
    query.categoryId = String(context.categoryId)
  }

  if (context.categoryName?.trim()) {
    query.categoryName = context.categoryName.trim()
  }

  return {
    path: `/article/${articleId}`,
    query
  }
}

export function resolveArticleSourceContext(route: RouteLocationNormalizedLoaded): SourceRouteContext {
  const source = readQueryValue(route.query.from) as ArticleSourceType | ''
  const name = readQueryValue(route.query.name)
  const categoryId = readQueryValue(route.query.categoryId)
  const categoryName = readQueryValue(route.query.categoryName)

  if (source === 'category' && name) {
    return {
      label: '分类',
      to: {
        path: '/categories',
        query: { name }
      }
    }
  }

  if (source === 'tag' && name) {
    return {
      label: '标签',
      to: {
        path: '/tags',
        query: { name }
      }
    }
  }

  if (source === 'archive') {
    const query: Record<string, string> = {}

    if (categoryId) {
      query.categoryId = categoryId
    }

    if (categoryName) {
      query.categoryName = categoryName
    }

    return {
      label: '归档',
      to: {
        path: '/archive',
        query
      }
    }
  }

  return {
    label: '文章',
    to: '/archive'
  }
}
