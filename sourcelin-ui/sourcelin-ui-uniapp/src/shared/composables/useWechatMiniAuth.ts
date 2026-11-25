import { loginByMiniProgramCode } from '@/shared/api/auth.api';
import type { LoginToken, MiniProgramLoginResult } from '@/modules/auth/types/auth';

export function isWechatMiniProgram(): boolean {
  // eslint-disable-next-line no-useless-assignment -- Uniapp 条件编译仅在 MP-WEIXIN 保留赋值
  let supported = false;
  // #ifdef MP-WEIXIN
  supported = true;
  // #endif
  return supported;
}

export function getWechatCode(): Promise<string> {
  return new Promise((resolve, reject) => {
    uni.login({
      success: (res) => {
        if (res.code) {
          resolve(res.code);
          return;
        }
        reject(new Error('微信登录 code 获取失败'));
      },
      fail: (error) => reject(error)
    });
  });
}

export async function requestWechatMiniLogin(): Promise<MiniProgramLoginResult & { token: LoginToken }> {
  const code = await getWechatCode();
  const result = await loginByMiniProgramCode(code);
  if (!result.token) {
    throw new Error('微信登录结果缺少 token');
  }
  return { ...result, token: result.token };
}
