import { http } from '@/utils/request';
import { encryptLoginPassword } from '@/utils/rsa';
import type { CaptchaInfo, LoginForm, LoginToken } from '../types/auth';

const MOBILE_LOGIN_TYPE = 'mini';

export function fetchCaptcha(): Promise<CaptchaInfo> {
  return http.get<CaptchaInfo>('/auth/captcha', { loginType: MOBILE_LOGIN_TYPE }, { isToken: false });
}

export function loginByPassword(form: LoginForm): Promise<LoginToken> {
  return http.post<LoginToken>(
    '/auth/login',
    {
      username: form.username,
      password: encryptLoginPassword(form.password),
      loginType: MOBILE_LOGIN_TYPE,
      captchaCode: form.captchaCode || '',
      captchaUuid: form.captchaUuid || ''
    },
    { isToken: false }
  );
}

export function logoutRemote(): Promise<boolean> {
  return http.delete<boolean>('/auth/logout');
}
