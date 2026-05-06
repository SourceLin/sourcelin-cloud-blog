interface ScrollLockTarget {
  documentElement: {
    style: {
      overflow: string
    }
  }
  body: {
    style: {
      overflow: string
    }
  }
}

interface ScrollLockState {
  count: number
  previousHtmlOverflow: string
  previousBodyOverflow: string
}

const scrollLockRegistry = new WeakMap<ScrollLockTarget, ScrollLockState>()

export function createStartupScrollLock(target: ScrollLockTarget) {
  let isLocked = false

  function getState() {
    const existingState = scrollLockRegistry.get(target)

    if (existingState) {
      return existingState
    }

    const nextState: ScrollLockState = {
      count: 0,
      previousHtmlOverflow: '',
      previousBodyOverflow: ''
    }

    scrollLockRegistry.set(target, nextState)
    return nextState
  }

  function lock() {
    if (isLocked) {
      return
    }

    const state = getState()

    if (state.count === 0) {
      state.previousHtmlOverflow = target.documentElement.style.overflow
      state.previousBodyOverflow = target.body.style.overflow
    }

    state.count += 1
    target.documentElement.style.overflow = 'hidden'
    target.body.style.overflow = 'hidden'
    isLocked = true
  }

  function release() {
    if (!isLocked) {
      return
    }

    const state = getState()

    if (state.count > 0) {
      state.count -= 1
    }

    if (state.count === 0) {
      target.documentElement.style.overflow = state.previousHtmlOverflow
      target.body.style.overflow = state.previousBodyOverflow
    }

    isLocked = false
  }

  return {
    sync(visible: boolean) {
      if (visible) {
        lock()
        return
      }

      release()
    },
    release
  }
}
