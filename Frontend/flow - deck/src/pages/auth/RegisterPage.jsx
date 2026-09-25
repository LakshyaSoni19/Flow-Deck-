import { useNavigate } from 'react-router-dom'
import AuthFormCard from '../../components/auth/AuthFormCard'
import AuthLinks from '../../components/auth/AuthLinks'
import RegistrationForm from '../../components/auth/RegistrationForm'
import { ROUTE_PATHS } from '../../constants/routePaths'
import { useAuthRequest } from '../../hooks/useAuthRequest'
import { authService } from '../../services/authService'

function RegisterPage() { const { execute, isSubmitting } = useAuthRequest(); const navigate = useNavigate(); return <AuthFormCard title="Create account" description="Register directly. Your account requires administrator approval." footer={<AuthLinks links={[{ to: ROUTE_PATHS.REGISTER_OTP, label: 'Register with OTP' }, { to: ROUTE_PATHS.LOGIN, label: 'Already have an account?' }]} />}><RegistrationForm submitLabel="Register" isSubmitting={isSubmitting} onSubmit={(payload, setErrors) => execute(() => authService.register(payload), { setErrors, onSuccess: () => navigate(ROUTE_PATHS.LOGIN) })} /></AuthFormCard> }
export default RegisterPage
