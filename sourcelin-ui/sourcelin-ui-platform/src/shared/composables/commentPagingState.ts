export type CommentViewMode = 'preview' | 'full'

export interface CommentPageSnapshot {
  page: number
  pageSize: number
}

interface CommentPagingStoreOptions {
  defaultPageSize: number
  pageSizeByMode?: Partial<Record<CommentViewMode, number>>
}

interface CommentTargetPagingState {
  preview: CommentPageSnapshot
  full: CommentPageSnapshot
}

function normalizePositiveInt(value: number | undefined, fallback: number) {
  const parsed = Number(value)
  if (Number.isFinite(parsed) && parsed > 0) {
    return Math.floor(parsed)
  }
  return fallback
}

export function createCommentPagingStore(options: CommentPagingStoreOptions) {
  const defaultFullPageSize = normalizePositiveInt(options.defaultPageSize, 10)
  const previewPageSize = normalizePositiveInt(options.pageSizeByMode?.preview, defaultFullPageSize)
  const fullPageSize = normalizePositiveInt(options.pageSizeByMode?.full, defaultFullPageSize)
  const targetStateMap = new Map<number, CommentTargetPagingState>()

  const createInitialState = (): CommentTargetPagingState => ({
    preview: { page: 1, pageSize: previewPageSize },
    full: { page: 1, pageSize: fullPageSize }
  })

  const ensureTargetState = (targetId: number): CommentTargetPagingState => {
    const normalizedTargetId = normalizePositiveInt(targetId, 0)
    const existing = targetStateMap.get(normalizedTargetId)
    if (existing) {
      return existing
    }
    const initialState = createInitialState()
    targetStateMap.set(normalizedTargetId, initialState)
    return initialState
  }

  const getState = (targetId: number, mode: CommentViewMode): CommentPageSnapshot => {
    const state = ensureTargetState(targetId)
    return { ...state[mode] }
  }

  const updateState = (
    targetId: number,
    mode: CommentViewMode,
    patch: Partial<CommentPageSnapshot>
  ): CommentPageSnapshot => {
    const state = ensureTargetState(targetId)
    const current = state[mode]
    const nextState: CommentPageSnapshot = {
      page: normalizePositiveInt(patch.page, current.page),
      pageSize: normalizePositiveInt(patch.pageSize, current.pageSize)
    }
    state[mode] = nextState
    return { ...nextState }
  }

  const resetMode = (targetId: number, mode: CommentViewMode): CommentPageSnapshot => {
    const state = ensureTargetState(targetId)
    state[mode] =
      mode === 'preview'
        ? { page: 1, pageSize: previewPageSize }
        : { page: 1, pageSize: fullPageSize }
    return { ...state[mode] }
  }

  return {
    getState,
    updateState,
    resetMode
  }
}
