import type { Ref } from 'vue'
import type { SayItem } from '@/modules/say/model/say.types'
import { useSayCardComments } from '@/modules/say/composables/useSayCardComments'
import { useSayCardInteractions } from '@/modules/say/composables/useSayCardInteractions'
import {
  formatSayContent,
  useSayCardPresentation,
  type SayCardVariant
} from '@/modules/say/composables/useSayCardPresentation'

export { formatSayContent, type SayCardVariant }

export interface SayPreviewPayload {
  images: string[]
  index: number
}

interface UseSayCardStateOptions {
  item: Ref<SayItem>
  emitPreview: (payload: SayPreviewPayload) => void
  emitDelete: (sayId: number) => void
}

export function useSayCardState(options: UseSayCardStateOptions) {
  const interactions = useSayCardInteractions(options)
  const presentation = useSayCardPresentation({
    item: options.item,
    currentUserId: interactions.currentUserId
  })
  const comments = useSayCardComments({
    item: options.item,
    currentUserId: interactions.currentUserId,
    commentCount: interactions.commentCount
  })

  function emitPreview(index: number) {
    options.emitPreview({
      images: presentation.imageArray.value,
      index
    })
  }

  return {
    ...interactions,
    ...presentation,
    ...comments,
    emitPreview
  }
}
