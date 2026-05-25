export interface CaptchaInfo {
  captchaEnabled: boolean;
  uuid?: string | null;
  img?: string | null;
}

export interface LoginToken {
  token: string;
  tokenName: string;
  tokenPrefix: string;
  expiresIn: number;
  loginType: string;
}

export interface LoginForm {
  username: string;
  password: string;
  captchaCode?: string;
  captchaUuid?: string | null;
}

export interface MiniProgramLoginResult {
  bound: boolean;
  newUser?: boolean;
  token?: LoginToken | null;
}

export interface MiniProgramBindPrepareResult {
  bindToken: string;
  bindMessage?: string;
}

export interface MiniProgramBindForm extends LoginForm {
  bindToken: string;
}
