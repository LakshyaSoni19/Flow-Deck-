import { ROLES } from './roles'
import { ROUTE_PATHS } from './routePaths'

export const SIDEBAR_MENUS = Object.freeze({
  [ROLES.ADMIN]: Object.freeze([
    { label: 'Dashboard', path: ROUTE_PATHS.ADMIN_DASHBOARD },
    { label: 'Departments', path: ROUTE_PATHS.ADMIN_DEPARTMENTS },
    { label: 'Designations', path: ROUTE_PATHS.ADMIN_DESIGNATIONS },
    { label: 'Users', path: ROUTE_PATHS.ADMIN_USERS },
    { label: 'Roles', path: ROUTE_PATHS.ADMIN_ROLES },
    { label: 'Projects', path: ROUTE_PATHS.ADMIN_PROJECTS },
  ]),
  [ROLES.PROJECT_MANAGER]: Object.freeze([
    { label: 'Dashboard', path: ROUTE_PATHS.PM_DASHBOARD },
    { label: 'Projects', path: ROUTE_PATHS.PM_PROJECTS },
    { label: 'Tasks', path: ROUTE_PATHS.PM_TASKS },
  ]),
  [ROLES.EMPLOYEE]: Object.freeze([
    { label: 'Dashboard', path: ROUTE_PATHS.EMPLOYEE_DASHBOARD },
    { label: 'My Projects', path: ROUTE_PATHS.EMPLOYEE_PROJECTS },
    { label: 'My Tasks', path: ROUTE_PATHS.EMPLOYEE_TASKS },
    { label: 'Profile', path: ROUTE_PATHS.EMPLOYEE_PROFILE },
  ]),
})
