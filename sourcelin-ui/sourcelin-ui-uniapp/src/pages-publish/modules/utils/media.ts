import { showInfoToast } from '@/utils/feedback';

type ImageSource = 'album' | 'camera';
type ImageSizeType = 'original' | 'compressed';

interface PickImagePathsOptions {
  count?: number;
  sizeType?: ImageSizeType[];
  sourceType?: ImageSource[];
}

interface ChooseImageSuccessResult {
  tempFilePaths?: string[] | string;
}

interface ChooseMediaSuccessResult {
  tempFiles?: Array<{
    tempFilePath?: string;
  }>;
}

const DEFAULT_SOURCES: ImageSource[] = ['album', 'camera'];
const AUTH_ERROR_PATTERN = /(auth deny|auth denied|authorize no response|permission denied)/i;
const CANCEL_ERROR_PATTERN = /(cancel|fail cancel)/i;

export async function pickSingleImagePath(options: PickImagePathsOptions = {}): Promise<string> {
  const paths = await pickImagePaths({
    ...options,
    count: 1
  });
  return paths[0] || '';
}

export async function pickImagePaths(options: PickImagePathsOptions = {}): Promise<string[]> {
  const sourceType = options.sourceType && options.sourceType.length > 0
    ? options.sourceType
    : DEFAULT_SOURCES;
  const source = await selectImageSource(sourceType);
  if (!source) return [];

  try {
    return await chooseImageBySource(source, options);
  } catch (error) {
    const errMsg = getErrorMessage(error);
    console.warn('[media] chooseImage failed', { source, errMsg, error });
    if (isCancelError(errMsg)) {
      return [];
    }
    if (isAuthError(errMsg)) {
      await promptPermissionGuide(source);
      return [];
    }

    try {
      return await chooseMediaBySource(source, options);
    } catch (fallbackError) {
      const fallbackErrMsg = getErrorMessage(fallbackError);
      console.warn('[media] chooseMedia failed', { source, errMsg: fallbackErrMsg, error: fallbackError });
      if (isCancelError(fallbackErrMsg)) {
        return [];
      }
      if (isAuthError(fallbackErrMsg)) {
        await promptPermissionGuide(source);
        return [];
      }
      showInfoToast(source === 'camera' ? '未能打开相机，请稍后重试' : '未能打开相册，请稍后重试');
      return [];
    }
  }
}

async function selectImageSource(sourceTypes: ImageSource[]): Promise<ImageSource | ''> {
  if (sourceTypes.length === 1) {
    return sourceTypes[0];
  }
  return await new Promise<ImageSource | ''>((resolve) => {
    uni.showActionSheet({
      itemList: ['从相册选择', '拍照'],
      success: (res) => resolve(res.tapIndex === 1 ? 'camera' : 'album'),
      fail: () => resolve('')
    });
  });
}

async function chooseImageBySource(source: ImageSource, options: PickImagePathsOptions): Promise<string[]> {
  const result = await new Promise<ChooseImageSuccessResult>((resolve, reject) => {
    uni.chooseImage({
      count: options.count || 1,
      sizeType: options.sizeType || ['compressed'],
      sourceType: [source],
      success: resolve,
      fail: reject
    });
  });
  return normalizeTempFilePaths(result.tempFilePaths);
}

async function chooseMediaBySource(source: ImageSource, options: PickImagePathsOptions): Promise<string[]> {
  const result = await new Promise<ChooseMediaSuccessResult>((resolve, reject) => {
    uni.chooseMedia({
      count: options.count || 1,
      mediaType: ['image'],
      sizeType: options.sizeType || ['compressed'],
      sourceType: [source],
      success: resolve,
      fail: reject
    });
  });
  return (result.tempFiles || [])
    .map((item) => item?.tempFilePath || '')
    .filter((item): item is string => Boolean(item));
}

function normalizeTempFilePaths(paths?: string[] | string): string[] {
  if (Array.isArray(paths)) {
    return paths.filter((item): item is string => Boolean(item));
  }
  if (typeof paths === 'string' && paths) {
    return [paths];
  }
  return [];
}

function getErrorMessage(error: unknown): string {
  if (typeof error === 'string') return error;
  if (error && typeof error === 'object' && 'errMsg' in error) {
    const errMsg = (error as { errMsg?: unknown }).errMsg;
    return typeof errMsg === 'string' ? errMsg : '';
  }
  return '';
}

function isCancelError(errMsg: string): boolean {
  return CANCEL_ERROR_PATTERN.test(errMsg);
}

function isAuthError(errMsg: string): boolean {
  return AUTH_ERROR_PATTERN.test(errMsg);
}

async function promptPermissionGuide(source: ImageSource): Promise<void> {
  const sourceLabel = source === 'camera' ? '相机' : '相册';
  const confirmed = await new Promise<boolean>((resolve) => {
    uni.showModal({
      title: `需要${sourceLabel}权限`,
      content: `当前无法访问${sourceLabel}。请在微信授权设置中开启后重试。`,
      confirmText: '去设置',
      cancelText: '知道了',
      success: (res) => resolve(!!res.confirm),
      fail: () => resolve(false)
    });
  });
  if (!confirmed) return;
  await new Promise<void>((resolve) => {
    uni.openSetting({
      success: () => resolve(),
      fail: () => resolve()
    });
  });
}
