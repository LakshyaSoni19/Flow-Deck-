import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthFormCard from '../../components/auth/AuthFormCard'
import AuthLinks from '../../components/auth/AuthLinks'
import Button from '../../components/common/Button'
import FormLayout from '../../components/common/FormLayout'
import Input from '../../components/common/Input'
import { ROUTE_PATHS } from '../../constants/routePaths'
import { ROLES } from '../../constants/roles'
import { useAuth } from '../../hooks/useAuth'
import { useAuthRequest } from '../../hooks/useAuthRequest'
import { authService } from '../../services/authService'
import { hasErrors, validateLogin } from '../../utils/validators'

const roleDestinations = { [ROLES.ADMIN]: ROUTE_PATHS.ADMIN_DASHBOARD, [ROLES.PROJECT_MANAGER]: ROUTE_PATHS.PM_DASHBOARD, [ROLES.EMPLOYEE]: ROUTE_PATHS.EMPLOYEE_DASHBOARD }

function LoginPage() {
  const [values, setValues] = useState({ email: '', password: '' }); const [errors, setErrors] = useState({})
  const { execute, isSubmitting } = useAuthRequest(); const { startSession } = useAuth(); const navigate = useNavigate()
  const submit = (event) => { event.preventDefault(); const nextErrors = validateLogin(values); setErrors(nextErrors); if (hasErrors(nextErrors)) return; execute(() => authService.login(values), { setErrors, onSuccess: (data) => { const session = { token: data.token, user: data.user }; startSession(session); navigate(data.user.roles.map((role) => roleDestinations[role]).find(Boolean) || ROUTE_PATHS.UNAUTHORIZED, { replace: true }) } }) }
  return <AuthFormCard title="Sign in" description="Access your Flow Deck workspace." footer={<AuthLinks links={[{ to: ROUTE_PATHS.FORGOT_PASSWORD, label: 'Forgot password?' }, { to: ROUTE_PATHS.SEND_OTP, label: 'Send OTP' }, { to: ROUTE_PATHS.REGISTER, label: 'Create an account' }]} />}><FormLayout onSubmit={submit} noValidate><Input id="email" name="email" type="email" label="Email" value={values.email} onChange={(event) => setValues({ ...values, email: event.target.value })} error={errors.email} /><Input id="password" name="password" type="password" label="Password" value={values.password} onChange={(event) => setValues({ ...values, password: event.target.value })} error={errors.password} /><Button type="submit" isLoading={isSubmitting}>Sign in</Button></FormLayout></AuthFormCard>
}
export default LoginPage
