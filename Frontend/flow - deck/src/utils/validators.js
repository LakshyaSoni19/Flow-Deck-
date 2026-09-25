const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const otpPattern = /^\d{6}$/

export const validateEmail = (email) => !email ? 'Email is required.' : !emailPattern.test(email) ? 'Enter a valid email address.' : ''
export const validatePassword = (password, name = 'Password') => !password ? `${name} is required.` : password.length < 6 || password.length > 45 ? `${name} must be 6 to 45 characters.` : ''
export const validateOtp = (otp) => !otpPattern.test(otp) ? 'OTP must contain exactly 6 digits.' : ''

export function validateLogin(values) { return { email: validateEmail(values.email), password: !values.password ? 'Password is required.' : '' } }
export function validateOtpRequest(values) { return { email: validateEmail(values.email), purpose: !values.purpose ? 'Purpose is required.' : '' } }
export function validateOtpVerification(values) { return { ...validateOtpRequest(values), otp: validateOtp(values.otp) } }
export function validateResetPassword(values) { return { email: validateEmail(values.email), otp: validateOtp(values.otp), newPassword: validatePassword(values.newPassword, 'New password') } }
export function validateRegistration(values) {
  return {
    firstName: !values.firstName ? 'First name is required.' : values.firstName.length > 45 ? 'First name must be at most 45 characters.' : '',
    lastName: !values.lastName ? 'Last name is required.' : values.lastName.length > 45 ? 'Last name must be at most 45 characters.' : '',
    email: validateEmail(values.email), password: validatePassword(values.password),
    mobile: values.mobile && !/^\d{10}$/.test(values.mobile) ? 'Mobile number must contain exactly 10 digits.' : '',
    gender: !values.gender ? 'Gender is required.' : '', dob: !values.dob ? 'Date of birth is required.' : '', address: !values.address ? 'Address is required.' : '',
    cityId: !Number.isInteger(Number(values.cityId)) || Number(values.cityId) < 1 ? 'Select a city.' : '',
    departmentId: !Number.isInteger(Number(values.departmentId)) || Number(values.departmentId) < 1 ? 'Select a department.' : '',
    designationId: !Number.isInteger(Number(values.designationId)) || Number(values.designationId) < 1 ? 'Select a designation.' : '',
  }
}

export const hasErrors = (errors) => Object.values(errors).some(Boolean)
