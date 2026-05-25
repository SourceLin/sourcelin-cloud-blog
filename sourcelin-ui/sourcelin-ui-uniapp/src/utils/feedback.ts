export type ToastIcon = 'success' | 'error' | 'none';

interface ToastOptions {
  title: string;
  icon?: ToastIcon;
  duration?: number;
}

export function showToast(options: ToastOptions): void {
  if (!options.title) return;
  uni.showToast({
    title: options.title,
    icon: options.icon || 'none',
    duration: options.duration || 2000
  });
}

export function showInfoToast(title: string, duration?: number): void {
  showToast({ title, icon: 'none', duration });
}

export function showSuccessToast(title: string, duration?: number): void {
  showToast({ title, icon: 'success', duration });
}
