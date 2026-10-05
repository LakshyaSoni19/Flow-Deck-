import { Outlet } from 'react-router-dom'
import { useState } from 'react'
import { ROLES } from '../constants/roles'
import { useAuth } from '../hooks/useAuth'
import AdminSidebarMenu from '../components/layout/AdminSidebarMenu'
import Breadcrumbs from '../components/layout/Breadcrumbs'
import EmployeeSidebarMenu from '../components/layout/EmployeeSidebarMenu'
import ProjectManagerSidebarMenu from '../components/layout/ProjectManagerSidebarMenu'
import TopNavbar from '../components/layout/TopNavbar'
import PageContainer from '../components/common/PageContainer'
import styles from '../components/layout/DashboardShell.module.css'

const sidebarByRole = {
  [ROLES.ADMIN]: AdminSidebarMenu,
  [ROLES.PROJECT_MANAGER]: ProjectManagerSidebarMenu,
  [ROLES.EMPLOYEE]: EmployeeSidebarMenu,
}

function DashboardLayout() {
  const [isCollapsed, setIsCollapsed] = useState(false)
  const { roles } = useAuth()
  const SidebarMenu = sidebarByRole[roles.find((role) => sidebarByRole[role])]

  return (
    <div className={styles.shell}>
      {!isCollapsed && (
        <div
          className={styles.sidebarOverlay}
          onClick={() => setIsCollapsed(true)}
          aria-hidden="true"
        />
      )}
      {SidebarMenu && (
        <SidebarMenu
          isCollapsed={isCollapsed}
          onToggle={() => setIsCollapsed((value) => !value)}
        />
      )}
      <div className={styles.main}>
        <TopNavbar onMenuToggle={() => setIsCollapsed((value) => !value)} />
        <PageContainer>
          <Breadcrumbs />
          <Outlet />
        </PageContainer>
      </div>
    </div>
  )
}

export default DashboardLayout

