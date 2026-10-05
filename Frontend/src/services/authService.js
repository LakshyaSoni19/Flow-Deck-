import { API_ENDPOINTS } from '../api/apiEndpoints'
import { httpClient } from '../api/httpClient'

const post = async (endpoint, payload) => (await httpClient.post(endpoint, payload)).data

export const authService = Object.freeze({
  login: (payload) => post(API_ENDPOINTS.AUTH.LOGIN, payload),
  register: (payload) => post(API_ENDPOINTS.AUTH.REGISTER, payload),
  sendRegistrationOtp: (payload) => post(API_ENDPOINTS.AUTH.SEND_REGISTRATION_OTP, payload),
  verifyRegistrationOtp: (payload) => post(API_ENDPOINTS.AUTH.VERIFY_REGISTRATION_OTP, payload),
  forgotPassword: (payload) => post(API_ENDPOINTS.AUTH.FORGOT_PASSWORD, payload),
  sendOtp: (payload) => post(API_ENDPOINTS.AUTH.SEND_OTP, payload),
  verifyOtp: (payload) => post(API_ENDPOINTS.AUTH.VERIFY_OTP, payload),
  resetPassword: (payload) => post(API_ENDPOINTS.AUTH.RESET_PASSWORD, payload),
  resendOtp: (payload) => post(API_ENDPOINTS.AUTH.RESEND_OTP, payload),
})
