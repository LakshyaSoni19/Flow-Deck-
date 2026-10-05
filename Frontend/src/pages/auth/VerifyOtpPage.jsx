import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthFormCard from '../../components/auth/AuthFormCard'
import AuthLinks from '../../components/auth/AuthLinks'
import Button from '../../components/common/Button'
import FormLayout from '../../components/common/FormLayout'
import OtpFields from '../../components/auth/OtpFields'
import { OTP_PURPOSES } from '../../constants/authConstants'
import { ROUTE_PATHS } from '../../constants/routePaths'
import { useAuthRequest } from '../../hooks/useAuthRequest'
import { authService } from '../../services/authService'
import { getOtpContext, setOtpContext } from '../../utils/authStorage'
import { hasErrors, validateOtpVerification } from '../../utils/validators'

function VerifyOtpPage() { const context = getOtpContext(); const [values, setValues] = useState({ email: context?.email || '', purpose: context?.purpose || OTP_PURPOSES.VERIFICATION, otp: '' }); const [errors, setErrors] = useState({}); const { execute, isSubmitting } = useAuthRequest(); const navigate = useNavigate(); const update = (event) => setValues({ ...values, [event.target.name]: event.target.value }); const submit = (event) => { event.preventDefault(); const nextErrors = validateOtpVerification(values); setErrors(nextErrors); if (hasErrors(nextErrors)) return; execute(() => authService.verifyOtp(values), { setErrors, onSuccess: () => { setOtpContext(values); if (values.purpose === OTP_PURPOSES.FORGOT_PASSWORD) navigate(ROUTE_PATHS.RESET_PASSWORD) } }) }; const resend = () => { const nextErrors = validateOtpVerification({ ...values, otp: '000000' }); delete nextErrors.otp; setErrors(nextErrors); if (hasErrors(nextErrors)) return; execute(() => authService.resendOtp({ email: values.email, purpose: values.purpose }), { setErrors }) }; return <AuthFormCard title="Verify OTP" description="Enter the code sent to your email." footer={<AuthLinks links={[{ to: ROUTE_PATHS.RESEND_OTP, label: 'Resend OTP' }, { to: ROUTE_PATHS.LOGIN, label: 'Back to sign in' }]} />}><FormLayout onSubmit={submit} noValidate><OtpFields values={values} errors={errors} onChange={update} /><Button type="submit" isLoading={isSubmitting}>Verify OTP</Button><Button variant="secondary" type="button" onClick={resend} disabled={isSubmitting}>Resend code</Button></FormLayout></AuthFormCard> }
export default VerifyOtpPage
