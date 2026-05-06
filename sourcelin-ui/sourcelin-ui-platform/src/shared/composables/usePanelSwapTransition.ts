const PANEL_SWAP_DURATION = 340

function prepareHeightAnimation(element: Element) {
  const el = element as HTMLElement
  el.style.overflow = 'hidden'
  el.style.willChange = 'height'
  el.style.transition = `height ${PANEL_SWAP_DURATION}ms cubic-bezier(0.22, 1, 0.36, 1)`
  return el
}

function cleanupHeightAnimation(el: HTMLElement) {
  el.style.height = ''
  el.style.overflow = ''
  el.style.willChange = ''
  el.style.transition = ''
}

export function usePanelSwapTransition() {
  const onBeforeEnter = (element: Element) => {
    const el = prepareHeightAnimation(element)
    el.style.height = '0px'
  }

  const onEnter = (element: Element, done: () => void) => {
    const el = prepareHeightAnimation(element)
    requestAnimationFrame(() => {
      el.style.height = `${el.scrollHeight}px`
    })
    window.setTimeout(done, PANEL_SWAP_DURATION)
  }

  const onAfterEnter = (element: Element) => {
    cleanupHeightAnimation(element as HTMLElement)
  }

  const onBeforeLeave = (element: Element) => {
    const el = prepareHeightAnimation(element)
    el.style.height = `${el.scrollHeight}px`
  }

  const onLeave = (element: Element, done: () => void) => {
    const el = prepareHeightAnimation(element)
    requestAnimationFrame(() => {
      el.style.height = '0px'
    })
    window.setTimeout(done, PANEL_SWAP_DURATION)
  }

  const onAfterLeave = (element: Element) => {
    cleanupHeightAnimation(element as HTMLElement)
  }

  return {
    onBeforeEnter,
    onEnter,
    onAfterEnter,
    onBeforeLeave,
    onLeave,
    onAfterLeave
  }
}
