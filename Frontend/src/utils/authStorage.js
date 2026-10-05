import { AUTH_STORAGE_KEY, OTP_CONTEXT_KEY } from '../constants/authConstants'

const parse = (value) => { try { return JSON.parse(value || 'null') } catch { return null } }

export const getAuthSession = () => parse(localStorage.getItem(AUTH_STORAGE_KEY))
export const setAuthSession = (session) => localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session))
export const clearAuthSession = () => localStorage.removeItem(AUTH_STORAGE_KEY)
export const getOtpContext = () => parse(sessionStorage.getItem(OTP_CONTEXT_KEY))
export const setOtpContext = (context) => sessionStorage.setItem(OTP_CONTEXT_KEY, JSON.stringify(context))
export const clearOtpContext = () => sessionStorage.removeItem(OTP_CONTEXT_KEY)
