import { requestData } from '@/shared/api/http'

export interface BloggerApplyVO {
  id?: number
  userId?: number
  reason?: string
  contact?: string
  status?: number // 0=待审核, 1=已通过, 2=已拒绝
  auditOpinion?: string
  auditTime?: string
  auditBy?: string
  createTime?: string
  updateTime?: string
}

export function submitBloggerApply(data: { reason: string; contact?: string }) {
  return requestData<void>({
    url: '/front/blogger/apply',
    method: 'post',
    data
  })
}

export function getBloggerApplyStatus() {
  return requestData<BloggerApplyVO | null>({
    url: '/front/blogger/apply/status',
    method: 'get'
  })
}
