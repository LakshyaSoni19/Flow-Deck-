import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { ROUTE_PATHS } from '../constants/routePaths'

function RoleBasedRoute({ allowedRoles }) {
  const { roles } = useAuth()
  const isAllowed = allowedRoles.some((role) => roles.includes(role))

  return isAllowed ? <Outlet /> : <Navigate to={ROUTE_PATHS.UNAUTHORIZED} replace />
}

export default RoleBasedRoute
