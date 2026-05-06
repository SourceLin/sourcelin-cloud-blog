import { requestData } from '@/shared/api/http'
import type { InteractionStat, InteractionTargetType } from '@/shared/types/api'

export function likeTarget(targetType: InteractionTargetType, targetId: number) {
  return requestData<void>({
    url: `/front/interactions/likes/${targetType}/${targetId}`,
    method: 'put'
  })
}

export function unlikeTarget(targetType: InteractionTargetType, targetId: number) {
  return requestData<void>({
    url: `/front/interactions/likes/${targetType}/${targetId}`,
    method: 'delete'
  })
}

export function collectTarget(targetType: InteractionTargetType, targetId: number) {
  return requestData<void>({
    url: `/front/interactions/collects/${targetType}/${targetId}`,
    method: 'put'
  })
}

export function uncollectTarget(targetType: InteractionTargetType, targetId: number) {
  return requestData<void>({
    url: `/front/interactions/collects/${targetType}/${targetId}`,
    method: 'delete'
  })
}

export function getTargetStats(targetType: InteractionTargetType, targetIds: number[]) {
  return requestData<InteractionStat[]>({
    url: '/front/interactions/targets/stat',
    method: 'get',
    params: {
      targetType,
      targetIds: targetIds.join(',')
    }
  })
}
