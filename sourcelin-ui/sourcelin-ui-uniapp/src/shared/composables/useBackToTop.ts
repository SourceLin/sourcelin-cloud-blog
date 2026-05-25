import { ref } from 'vue';
import { onPageScroll } from '@dcloudio/uni-app';

export function useBackToTop(threshold = 360) {
  const backToTopVisible = ref(false);

  onPageScroll((event) => {
    backToTopVisible.value = event.scrollTop > threshold;
  });

  return {
    backToTopVisible
  };
}
