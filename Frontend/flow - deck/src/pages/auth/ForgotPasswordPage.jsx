import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthFormCard from '../../components/auth/AuthFormCard'
import AuthLinks from '../../components/auth/AuthLinks'
import Button from '../../components/common/Button'
import FormLayout from '../../components/common/FormLayout'
import Input from '../../components/common/Input'
import { OTP_PURPOSES } from '../../constants/authConstants'
import { ROUTE_PATHS } from '../../constants/routePaths'
import { useAuthRequest } from '../../hooks/useAuthRequest'
import { authService } from '../../services/authService'
import { setOtpContext } from '../../utils/authStorage'
import { validateEmail } from '../../utils/validators'

function ForgotPasswordPage() { const [email, setEmail] = useState(''); const [errors, setErrors] = useState({}); const { execute, isSubmitting } = useAuthRequest(); const navigate = useNavigate(); const submit = (event) => { event.preventDefault(); const emailError = validateEmail(email); setErrors({ email: emailError }); if (emailError) return; const payload = { email, purpose: OTP_PURPOSES.FORGOT_PASSWORD }; execute(() => authService.forgotPassword(payload), { setErrors, onSuccess: () => { setOtpContext(payload); navigate(ROUTE_PATHS.FORGOT_PASSWORD_VERIFY) } }) }; return <AuthFormCard title="Forgot password" description="We will send a reset code to your registered email." footer={<AuthLinks links={[{ to: ROUTE_PATHS.LOGIN, label: 'Back to sign in' }]} />}><FormLayout onSubmit={submit} noValidate><Input id="email" type="email" label="Email" value={email} onChange={(event) => setEmail(event.target.value)} error={errors.email} /><Button type="submit" isLoading={isSubmitting}>Send reset code</Button></FormLayout></AuthFormCard> }
export default ForgotPasswordPage
