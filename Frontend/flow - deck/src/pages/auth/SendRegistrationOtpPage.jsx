import { useNavigate } from 'react-router-dom'
import AuthFormCard from '../../components/auth/AuthFormCard'
import AuthLinks from '../../components/auth/AuthLinks'
import RegistrationForm from '../../components/auth/RegistrationForm'
import { ROUTE_PATHS } from '../../constants/routePaths'
import { useAuthRequest } from '../../hooks/useAuthRequest'
import { authService } from '../../services/authService'
import { setOtpContext } from '../../utils/authStorage'

function SendRegistrationOtpPage() { const { execute, isSubmitting } = useAuthRequest(); const navigate = useNavigate(); return <AuthFormCard title="Register with OTP" description="Submit your registration details to receive a verification code." footer={<AuthLinks links={[{ to: ROUTE_PATHS.LOGIN, label: 'Back to sign in' }]} />}><RegistrationForm submitLabel="Send registration OTP" isSubmitting={isSubmitting} onSubmit={(payload, setErrors) => execute(() => authService.sendRegistrationOtp(payload), { setErrors, onSuccess: () => { setOtpContext({ email: payload.email, purpose: 'REGISTRATION' }); navigate(ROUTE_PATHS.VERIFY_REGISTRATION_OTP) } })} /></AuthFormCard> }
export default SendRegistrationOtpPage
