import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthFormCard from '../../components/auth/AuthFormCard'
import Button from '../../components/common/Button'
import FormLayout from '../../components/common/FormLayout'
import Input from '../../components/common/Input'
import { ROUTE_PATHS } from '../../constants/routePaths'
import { useAuthRequest } from '../../hooks/useAuthRequest'
import { authService } from '../../services/authService'
import { clearOtpContext, getOtpContext } from '../../utils/authStorage'
import { hasErrors, validateResetPassword } from '../../utils/validators'

function ResetPasswordPage() { const context = getOtpContext(); const [values, setValues] = useState({ email: context?.email || '', otp: context?.otp || '', newPassword: '' }); const [errors, setErrors] = useState({}); const { execute, isSubmitting } = useAuthRequest(); const navigate = useNavigate(); const update = (event) => setValues({ ...values, [event.target.name]: event.target.value }); const submit = (event) => { event.preventDefault(); const nextErrors = validateResetPassword(values); setErrors(nextErrors); if (hasErrors(nextErrors)) return; execute(() => authService.resetPassword(values), { setErrors, onSuccess: () => { clearOtpContext(); navigate(ROUTE_PATHS.LOGIN) } }) }; return <AuthFormCard title="Reset password" description="Set a new password after verifying your OTP."><FormLayout onSubmit={submit} noValidate><Input id="email" name="email" type="email" label="Email" value={values.email} onChange={update} error={errors.email} /><Input id="otp" name="otp" inputMode="numeric" maxLength="6" label="OTP" value={values.otp} onChange={update} error={errors.otp} /><Input id="newPassword" name="newPassword" type="password" label="New password" value={values.newPassword} onChange={update} error={errors.newPassword} /><Button type="submit" isLoading={isSubmitting}>Reset password</Button></FormLayout></AuthFormCard> }
export default ResetPasswordPage
