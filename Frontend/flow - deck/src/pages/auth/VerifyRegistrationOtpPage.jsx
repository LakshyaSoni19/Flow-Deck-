import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthFormCard from '../../components/auth/AuthFormCard'
import AuthLinks from '../../components/auth/AuthLinks'
import Button from '../../components/common/Button'
import FormLayout from '../../components/common/FormLayout'
import Input from '../../components/common/Input'
import { ROUTE_PATHS } from '../../constants/routePaths'
import { useAuthRequest } from '../../hooks/useAuthRequest'
import { authService } from '../../services/authService'
import { clearOtpContext, getOtpContext } from '../../utils/authStorage'
import { hasErrors, validateEmail, validateOtp } from '../../utils/validators'

function VerifyRegistrationOtpPage() {
  const context = getOtpContext(); const [values, setValues] = useState({ email: context?.email || '', otp: '' }); const [errors, setErrors] = useState({})
  const { execute, isSubmitting } = useAuthRequest(); const navigate = useNavigate()
  const submit = (event) => { event.preventDefault(); const nextErrors = { email: validateEmail(values.email), otp: validateOtp(values.otp) }; setErrors(nextErrors); if (hasErrors(nextErrors)) return; execute(() => authService.verifyRegistrationOtp(values), { setErrors, onSuccess: () => { clearOtpContext(); navigate(ROUTE_PATHS.LOGIN) } }) }
  return <AuthFormCard title="Verify registration OTP" description="Enter the six-digit code sent to your email." footer={<AuthLinks links={[{ to: ROUTE_PATHS.REGISTER_OTP, label: 'Start registration again' }]} />}><FormLayout onSubmit={submit} noValidate><Input id="email" name="email" type="email" label="Email" value={values.email} onChange={(event) => setValues({ ...values, email: event.target.value })} error={errors.email} /><Input id="otp" name="otp" inputMode="numeric" maxLength="6" label="OTP" value={values.otp} onChange={(event) => setValues({ ...values, otp: event.target.value })} error={errors.otp} /><Button type="submit" isLoading={isSubmitting}>Verify registration</Button></FormLayout></AuthFormCard>
}
export default VerifyRegistrationOtpPage
