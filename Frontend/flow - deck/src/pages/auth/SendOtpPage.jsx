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
import { setOtpContext } from '../../utils/authStorage'
import { hasErrors, validateOtpRequest } from '../../utils/validators'

function SendOtpPage() { const [values, setValues] = useState({ email: '', purpose: OTP_PURPOSES.VERIFICATION }); const [errors, setErrors] = useState({}); const { execute, isSubmitting } = useAuthRequest(); const navigate = useNavigate(); const submit = (event) => { event.preventDefault(); const nextErrors = validateOtpRequest(values); setErrors(nextErrors); if (hasErrors(nextErrors)) return; execute(() => authService.sendOtp(values), { setErrors, onSuccess: () => { setOtpContext(values); navigate(ROUTE_PATHS.VERIFY_OTP) } }) }; return <AuthFormCard title="Send OTP" description="Request a verification code for a supported purpose."><FormLayout onSubmit={submit} noValidate><OtpFields values={values} errors={errors} onChange={(event) => setValues({ ...values, [event.target.name]: event.target.value })} includeOtp={false} /><Button type="submit" isLoading={isSubmitting}>Send OTP</Button></FormLayout></AuthFormCard> }
export default SendOtpPage
