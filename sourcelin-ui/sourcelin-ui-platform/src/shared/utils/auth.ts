import Cookies from 'js-cookie'

const TokenKey = 'Blog-Token'
const TokenNameKey = 'Blog-Token-Name'
const TokenPrefixKey = 'Blog-Token-Prefix'
const LoginTypeKey = 'Blog-Login-Type'
const ExpiresInKey = 'Blog-Expires-In'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token: string) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getTokenName() {
  return Cookies.get(TokenNameKey) || 'Authorization'
}

export function setTokenName(tokenName: string) {
  return Cookies.set(TokenNameKey, tokenName)
}

export function removeTokenName() {
  return Cookies.remove(TokenNameKey)
}

export function getTokenPrefix() {
  return Cookies.get(TokenPrefixKey) || 'Bearer'
}

export function setTokenPrefix(tokenPrefix: string) {
  return Cookies.set(TokenPrefixKey, tokenPrefix)
}

export function removeTokenPrefix() {
  return Cookies.remove(TokenPrefixKey)
}

export function setLoginType(loginType: string) {
  return Cookies.set(LoginTypeKey, loginType)
}

export function removeLoginType() {
  return Cookies.remove(LoginTypeKey)
}

export function getExpiresIn() {
  return Cookies.get(ExpiresInKey) || -1
}

export function setExpiresIn(time: string) {
  return Cookies.set(ExpiresInKey, time)
}

export function removeExpiresIn() {
  return Cookies.remove(ExpiresInKey)
}
