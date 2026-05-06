/** 解析后台常见时间串（如 yyyy-MM-dd HH:mm:ss），失败返回 null */
export function parseFlexibleDateTime(input: string): Date | null {
  const raw = input?.trim()
  if (!raw) return null
  const normalized = raw.includes('T') ? raw : raw.replace(' ', 'T')
  const d = new Date(normalized)
  return Number.isNaN(d.getTime()) ? null : d
}

/**
 * 文艺风相对发布时间：片刻之前、方才发布、数分钟前、近日、去年等。
 * `now` 可注入便于测试。
 */
export function formatPublishRelativeTime(
  createTime: string,
  now: Date = new Date()
): string {
  const then = parseFlexibleDateTime(createTime)
  if (!then) return createTime?.trim() || '—'

  const ms = now.getTime() - then.getTime()
  if (ms < 0) return createTime.trim()

  const minute = 60_000
  const hour = 60 * minute
  const day = 24 * hour

  // 文艺文案
  if (ms < minute) return '片刻之前'
  if (ms < 10 * minute) return '方才发布'
  if (ms < hour) return `${Math.floor(ms / minute)}分钟前`
  if (ms < day) return `${Math.floor(ms / hour)}小时前`

  const dayCount = Math.floor(ms / day)
  if (dayCount < 7) return `${dayCount}日前`
  if (dayCount < 30) return '近日'

  let months =
    (now.getFullYear() - then.getFullYear()) * 12 + (now.getMonth() - then.getMonth())
  if (now.getDate() < then.getDate()) months -= 1
  if (months < 1) return '近日'
  if (months < 12) return `${months}个月前`

  let years = now.getFullYear() - then.getFullYear()
  if (
    now.getMonth() < then.getMonth() ||
    (now.getMonth() === then.getMonth() && now.getDate() < then.getDate())
  ) {
    years -= 1
  }
  return years >= 1 ? `${years}年前` : '去年'
}
