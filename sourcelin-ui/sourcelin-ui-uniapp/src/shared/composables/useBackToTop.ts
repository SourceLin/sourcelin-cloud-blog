import { computed, ref } from 'vue';

interface PageScrollEvent {
  scrollTop: number;
}

export function useBackToTop(threshold = 360) {
  const backToTopVisible = ref(false);

  function handlePageScroll(event: PageScrollEvent): void {
    backToTopVisible.value = event.scrollTop > threshold;
  }

  return {
    backToTopVisible,
    handlePageScroll
  };
}

interface UseKeyboardInsetOptions {
  basePadding?: string;
  cursorSpacing?: number;
  transitionMs?: number;
}

interface KeyboardHeightChangeResult {
  height?: number;
}

interface KeyboardApi {
  onKeyboardHeightChange?: (callback: (result: KeyboardHeightChangeResult) => void) => void;
  offKeyboardHeightChange?: (callback: (result: KeyboardHeightChangeResult) => void) => void;
}

const EASING = 'cubic-bezier(0.32, 0.94, 0.6, 1)';

export function useKeyboardInset(options: UseKeyboardInsetOptions = {}) {
  const basePadding = options.basePadding ?? '32rpx';
  const cursorSpacing = options.cursorSpacing ?? 20;
  const transitionMs = options.transitionMs ?? 200;

  const keyboardHeight = ref(0);
  const keyboardVisible = ref(false);

  let listening = false;
  let keyboardHandler: ((result: KeyboardHeightChangeResult) => void) | null = null;

  function resolveKeyboardApi(): KeyboardApi | null {
    const globalWechat = (globalThis as { wx?: KeyboardApi }).wx;
    if (globalWechat) {
      return globalWechat;
    }
    if (typeof uni !== 'undefined') {
      return uni as unknown as KeyboardApi;
    }
    return null;
  }

  function applyKeyboardHeight(height?: number): void {
    const nextHeight = Math.max(0, Math.round(height ?? 0));
    keyboardHeight.value = nextHeight;
    keyboardVisible.value = nextHeight > 0;
  }

  function resetKeyboardInset(): void {
    applyKeyboardHeight(0);
  }

  function startKeyboardWatch(): void {
    if (listening) return;
    const keyboardApi = resolveKeyboardApi();
    if (typeof keyboardApi?.onKeyboardHeightChange !== 'function') return;
    keyboardHandler = (result: KeyboardHeightChangeResult) => {
      applyKeyboardHeight(result.height);
    };
    keyboardApi.onKeyboardHeightChange(keyboardHandler);
    listening = true;
  }

  function stopKeyboardWatch(): void {
    const keyboardApi = resolveKeyboardApi();
    if (listening && keyboardHandler && typeof keyboardApi?.offKeyboardHeightChange === 'function') {
      keyboardApi.offKeyboardHeightChange(keyboardHandler);
    }
    keyboardHandler = null;
    listening = false;

    resetKeyboardInset();
  }

  const sheetStyle = computed<Record<string, string>>(() => ({
    bottom: keyboardVisible.value ? `${keyboardHeight.value}px` : '0px',
    paddingBottom: keyboardVisible.value ? basePadding : `calc(${basePadding} + env(safe-area-inset-bottom))`,
    transition: `bottom ${transitionMs}ms ${EASING}, padding-bottom ${transitionMs}ms ${EASING}`
  }));

  return {
    cursorSpacing,
    keyboardHeight,
    keyboardVisible,
    resetKeyboardInset,
    sheetStyle,
    startKeyboardWatch,
    stopKeyboardWatch
  };
}
