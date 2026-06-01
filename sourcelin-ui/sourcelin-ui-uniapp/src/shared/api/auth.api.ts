import { http } from '@/utils/request';
import { encryptLoginPassword } from '@/utils/rsa';
import type {
  CaptchaInfo,
  LoginForm,
  MiniProgramBindPrepareResult,
  LoginToken,
  MiniProgramBindForm,
  MiniProgramLoginResult
} from '@/modules/auth/types/auth';

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

export function loginByMiniProgramCode(code: string): Promise<MiniProgramLoginResult> {
  return http.post<MiniProgramLoginResult>(
    '/auth/wechat/mini/login',
    { code },
    { isToken: false }
  );
}

export function prepareMiniProgramBind(code: string): Promise<MiniProgramBindPrepareResult> {
  return http.post<MiniProgramBindPrepareResult>(
    '/auth/wechat/mini/prepare-bind',
    { code },
    { isToken: false }
  );
}

export function bindMiniProgramAccount(form: MiniProgramBindForm): Promise<LoginToken> {
  return http.post<LoginToken>(
    '/auth/wechat/mini/bind',
    {
      bindToken: form.bindToken,
      username: form.username,
      password: encryptLoginPassword(form.password),
      captchaCode: form.captchaCode || '',
      captchaUuid: form.captchaUuid || ''
    },
    { isToken: false }
  );
}

export function logoutRemote(): Promise<boolean> {
  return http.delete<boolean>('/auth/logout');
}
