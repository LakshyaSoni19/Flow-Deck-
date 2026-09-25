import { Navigate, Route, Routes } from 'react-router-dom'
import { ROLES } from '../constants/roles'
import { ROUTE_PATHS } from '../constants/routePaths'
import AuthLayout from '../layouts/AuthLayout'
import DashboardLayout from '../layouts/DashboardLayout'
import ProtectedLayout from '../layouts/ProtectedLayout'
import AdminDashboardPage from '../pages/admin/AdminDashboardPage'
import DepartmentsPage from '../pages/admin/DepartmentsPage'
import DesignationsPage from '../pages/admin/DesignationsPage'
import ProjectsPage from '../pages/admin/ProjectsPage'
import RolesPage from '../pages/admin/RolesPage'
import UsersPage from '../pages/admin/UsersPage'
import ForgotPasswordPage from '../pages/auth/ForgotPasswordPage'
import LoginPage from '../pages/auth/LoginPage'
import RegisterPage from '../pages/auth/RegisterPage'
import ResetPasswordPage from '../pages/auth/ResetPasswordPage'
import ResendOtpPage from '../pages/auth/ResendOtpPage'
import SendOtpPage from '../pages/auth/SendOtpPage'
import SendRegistrationOtpPage from '../pages/auth/SendRegistrationOtpPage'
import VerifyRegistrationOtpPage from '../pages/auth/VerifyRegistrationOtpPage'
import VerifyOtpPage from '../pages/auth/VerifyOtpPage'
import EmployeeDashboardPage from '../pages/employee/EmployeeDashboardPage'
import MyProjectsPage from '../pages/employee/MyProjectsPage'
import MyTasksPage from '../pages/employee/MyTasksPage'
import ProfilePage from '../pages/employee/ProfilePage'
import NotFoundPage from '../pages/NotFoundPage'
import PmDashboardPage from '../pages/pm/PmDashboardPage'
import PmProjectsPage from '../pages/pm/PmProjectsPage'
import PmTasksPage from '../pages/pm/PmTasksPage'
import UnauthorizedPage from '../pages/UnauthorizedPage'
import ProtectedRoute from './ProtectedRoute'
import RoleBasedRoute from './RoleBasedRoute'

function AppRoutes() {
  return (
    <Routes>
      <Route element={<AuthLayout />}>
        <Route path={ROUTE_PATHS.LOGIN} element={<LoginPage />} />
        <Route path={ROUTE_PATHS.REGISTER} element={<RegisterPage />} />
        <Route path={ROUTE_PATHS.REGISTER_OTP} element={<SendRegistrationOtpPage />} />
        <Route path={ROUTE_PATHS.VERIFY_REGISTRATION_OTP} element={<VerifyRegistrationOtpPage />} />
        <Route path={ROUTE_PATHS.SEND_OTP} element={<SendOtpPage />} />
        <Route path={ROUTE_PATHS.VERIFY_OTP} element={<VerifyOtpPage />} />
        <Route path={ROUTE_PATHS.FORGOT_PASSWORD_VERIFY} element={<VerifyOtpPage />} />
        <Route path={ROUTE_PATHS.RESEND_OTP} element={<ResendOtpPage />} />
        <Route path={ROUTE_PATHS.FORGOT_PASSWORD} element={<ForgotPasswordPage />} />
        <Route path={ROUTE_PATHS.RESET_PASSWORD} element={<ResetPasswordPage />} />
      </Route>
      <Route path={ROUTE_PATHS.UNAUTHORIZED} element={<UnauthorizedPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<ProtectedLayout />}>
          <Route element={<DashboardLayout />}>
            <Route element={<RoleBasedRoute allowedRoles={[ROLES.ADMIN]} />}>
              <Route path={ROUTE_PATHS.ADMIN_DASHBOARD} element={<AdminDashboardPage />} />
              <Route path={ROUTE_PATHS.ADMIN_DEPARTMENTS} element={<DepartmentsPage />} />
              <Route path={ROUTE_PATHS.ADMIN_DESIGNATIONS} element={<DesignationsPage />} />
              <Route path={ROUTE_PATHS.ADMIN_USERS} element={<UsersPage />} />
              <Route path={ROUTE_PATHS.ADMIN_ROLES} element={<RolesPage />} />
              <Route path={ROUTE_PATHS.ADMIN_PROJECTS} element={<ProjectsPage />} />
            </Route>
            <Route element={<RoleBasedRoute allowedRoles={[ROLES.PROJECT_MANAGER]} />}>
              <Route path={ROUTE_PATHS.PM_DASHBOARD} element={<PmDashboardPage />} />
              <Route path={ROUTE_PATHS.PM_PROJECTS} element={<PmProjectsPage />} />
              <Route path={ROUTE_PATHS.PM_TASKS} element={<PmTasksPage />} />
            </Route>
            <Route element={<RoleBasedRoute allowedRoles={[ROLES.EMPLOYEE]} />}>
              <Route path={ROUTE_PATHS.EMPLOYEE_DASHBOARD} element={<EmployeeDashboardPage />} />
              <Route path={ROUTE_PATHS.EMPLOYEE_PROJECTS} element={<MyProjectsPage />} />
              <Route path={ROUTE_PATHS.EMPLOYEE_TASKS} element={<MyTasksPage />} />
              <Route path={ROUTE_PATHS.EMPLOYEE_PROFILE} element={<ProfilePage />} />
            </Route>
          </Route>
        </Route>
      </Route>
      <Route path="/" element={<Navigate to={ROUTE_PATHS.LOGIN} replace />} />
      <Route path={ROUTE_PATHS.NOT_FOUND} element={<NotFoundPage />} />
    </Routes>
  )
}

export default AppRoutes
