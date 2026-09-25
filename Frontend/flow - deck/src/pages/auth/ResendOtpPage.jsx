import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthFormCard from '../../components/auth/AuthFormCard'
import Button from '../../components/common/Button'
import FormLayout from '../../components/common/FormLayout'
import OtpFields from '../../components/auth/OtpFields'
import { OTP_PURPOSES } from '../../constants/authConstants'
import { ROUTE_PATHS } from '../../constants/routePaths'
import { useAuthRequest } from '../../hooks/useAuthRequest'
import { authService } from '../../services/authService'
import { getOtpContext, setOtpContext } from '../../utils/authStorage'
import { hasErrors, validateOtpRequest } from '../../utils/validators'

function ResendOtpPage() { const context = getOtpContext(); const [values, setValues] = useState({ email: context?.email || '', purpose: context?.purpose === 'REGISTRATION' ? OTP_PURPOSES.VERIFICATION : context?.purpose || OTP_PURPOSES.FORGOT_PASSWORD }); const [errors, setErrors] = useState({}); const { execute, isSubmitting } = useAuthRequest(); const navigate = useNavigate(); const submit = (event) => { event.preventDefault(); const nextErrors = validateOtpRequest(values); setErrors(nextErrors); if (hasErrors(nextErrors)) return; execute(() => authService.resendOtp(values), { setErrors, onSuccess: () => { setOtpContext(values); navigate(ROUTE_PATHS.VERIFY_OTP) } }) }; return <AuthFormCard title="Resend OTP" description="Request a new OTP for a supported purpose."><FormLayout onSubmit={submit} noValidate><OtpFields values={values} errors={errors} onChange={(event) => setValues({ ...values, [event.target.name]: event.target.value })} includeOtp={false} /><Button type="submit" isLoading={isSubmitting}>Resend OTP</Button></FormLayout></AuthFormCard> }
export default ResendOtpPage
