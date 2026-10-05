import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import EmptyState from '../../components/common/EmptyState'
import PageHeader from '../../components/common/PageHeader'
import DashboardSkeleton from '../../components/dashboard/DashboardSkeleton'
import { adminService } from '../../services/adminService'
import { getApiError } from '../../utils/apiError'
import styles from './AdminDashboardPage.module.css'

function AdminDashboardPage() {
  const [metrics, setMetrics] = useState({
    users: 0,
    projects: 0,
    departments: 0,
    designations: 0,
    roles: 0,
  })
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const loadMetrics = useCallback(async () => {
    setIsLoading(true)
    setError('')
    try {
      const [usersRes, projectsRes, deptsRes, desigsRes, rolesRes] = await Promise.all([
        adminService.getUsers({ pageNo: 0, pageSize: 1 }).catch(() => null),
        adminService.getProjects({ pageNo: 0, pageSize: 1 }).catch(() => null),
        adminService.getDepartments({ pageNo: 0, pageSize: 1 }).catch(() => null),
        adminService.getDesignations({ pageNo: 0, pageSize: 1 }).catch(() => null),
        adminService.getRoles().catch(() => null),
      ])

      const extractCount = (res) => {
        if (res && res.success && res.data) {
          if (typeof res.data.totalElements === 'number') return res.data.totalElements
          if (Array.isArray(res.data.content)) return res.data.content.length
          if (Array.isArray(res.data)) return res.data.length
        }
        return 0
      }

      setMetrics({
        users: extractCount(usersRes),
        projects: extractCount(projectsRes),
        departments: extractCount(deptsRes),
        designations: extractCount(desigsRes),
        roles: Array.isArray(rolesRes?.data) ? rolesRes.data.length : extractCount(rolesRes),
      })
    } catch (requestError) {
      const apiError = getApiError(requestError)
      setError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    void Promise.resolve().then(loadMetrics)
  }, [loadMetrics])

  return (
    <>
      <PageHeader
        title="Admin Dashboard"
        description="Enterprise platform overview, master data metrics, and system administration."
      />

      <section className={styles.heroCard}>
        <div className={styles.heroContent}>
          <span className={styles.heroTag}>Admin Control Center</span>
          <h2 className={styles.heroTitle}>System Administration Dashboard</h2>
          <p className={styles.heroSubtitle}>
            Welcome to Flow Deck Administrator Workspace. Monitor live system metrics, manage
            departments, designations, security roles, user accounts, and project allocations.
          </p>
        </div>
        <div className={styles.heroBadges}>
          <div className={styles.statusPill}>
            <span className={styles.dot} />
            System Status: Active
          </div>
          <div className={styles.statusPill}>RBAC Security Enforced</div>
        </div>
      </section>

      {isLoading && <DashboardSkeleton count={4} />}

      {error && !isLoading && (
        <EmptyState
          title="Metrics unavailable"
          description={error}
          action={<Button onClick={loadMetrics}>Try again</Button>}
        />
      )}

      {!isLoading && !error && (
        <>
          <section className={styles.metricsGrid}>
            <div className={styles.metricCard} onClick={() => navigate('/admin/users')} style={{ cursor: 'pointer' }}>
              <div className={styles.metricHeader}>
                <p className={styles.metricLabel}>Total Users</p>
                <span className={styles.metricIconBadge}>👥</span>
              </div>
              <p className={styles.metricValue}>{metrics.users}</p>
              <div className={styles.metricFooter}>Registered platform accounts</div>
            </div>

            <div className={styles.metricCard} onClick={() => navigate('/admin/projects')} style={{ cursor: 'pointer' }}>
              <div className={styles.metricHeader}>
                <p className={styles.metricLabel}>Total Projects</p>
                <span className={styles.metricIconBadge}>📁</span>
              </div>
              <p className={styles.metricValue}>{metrics.projects}</p>
              <div className={styles.metricFooter}>Active project master records</div>
            </div>

            <div className={styles.metricCard} onClick={() => navigate('/admin/departments')} style={{ cursor: 'pointer' }}>
              <div className={styles.metricHeader}>
                <p className={styles.metricLabel}>Departments</p>
                <span className={styles.metricIconBadge}>🏢</span>
              </div>
              <p className={styles.metricValue}>{metrics.departments}</p>
              <div className={styles.metricFooter}>Master organization units</div>
            </div>

            <div className={styles.metricCard} onClick={() => navigate('/admin/designations')} style={{ cursor: 'pointer' }}>
              <div className={styles.metricHeader}>
                <p className={styles.metricLabel}>Designations</p>
                <span className={styles.metricIconBadge}>💼</span>
              </div>
              <p className={styles.metricValue}>{metrics.designations}</p>
              <div className={styles.metricFooter}>Job title definitions</div>
            </div>

            <div className={styles.metricCard} onClick={() => navigate('/admin/roles')} style={{ cursor: 'pointer' }}>
              <div className={styles.metricHeader}>
                <p className={styles.metricLabel}>Security Roles</p>
                <span className={styles.metricIconBadge}>🛡️</span>
              </div>
              <p className={styles.metricValue}>{metrics.roles}</p>
              <div className={styles.metricFooter}>Access control roles</div>
            </div>
          </section>

          <h2 className={styles.sectionTitle}>Quick Administrative Actions</h2>
          <section className={styles.quickActionsGrid}>
            <div className={styles.actionCard}>
              <div>
                <h3 className={styles.actionCardTitle}>User Management</h3>
                <p className={styles.actionCardDesc}>
                  Inspect user accounts, assign security roles, update profile details, or activate/deactivate accounts.
                </p>
              </div>
              <Button onClick={() => navigate('/admin/users')}>Manage Users</Button>
            </div>

            <div className={styles.actionCard}>
              <div>
                <h3 className={styles.actionCardTitle}>Project Master</h3>
                <p className={styles.actionCardDesc}>
                  Create new projects, update delivery parameters, change workflow status, or assign Project Managers.
                </p>
              </div>
              <Button onClick={() => navigate('/admin/projects')}>Manage Projects</Button>
            </div>

            <div className={styles.actionCard}>
              <div>
                <h3 className={styles.actionCardTitle}>Department Master</h3>
                <p className={styles.actionCardDesc}>
                  Add, edit, or search organizational departments across the enterprise hierarchy.
                </p>
              </div>
              <Button variant="secondary" onClick={() => navigate('/admin/departments')}>
                Manage Departments
              </Button>
            </div>

            <div className={styles.actionCard}>
              <div>
                <h3 className={styles.actionCardTitle}>Designation Master</h3>
                <p className={styles.actionCardDesc}>
                  Define employee job titles, designations, and corporate position rankings.
                </p>
              </div>
              <Button variant="secondary" onClick={() => navigate('/admin/designations')}>
                Manage Designations
              </Button>
            </div>

            <div className={styles.actionCard}>
              <div>
                <h3 className={styles.actionCardTitle}>Role Assignments</h3>
                <p className={styles.actionCardDesc}>
                  Create system roles, configure permissions, and grant access privileges to registered accounts.
                </p>
              </div>
              <Button variant="secondary" onClick={() => navigate('/admin/roles')}>
                Manage Roles
              </Button>
            </div>
          </section>

          <section className={styles.overviewSection}>
            <div className={styles.infoCard}>
              <h3 className={styles.actionCardTitle}>System Overview & Health</h3>
              <p className={styles.actionCardDesc}>
                Flow Deck Task & Project Management Frontend is running in Production mode. REST API integration is active.
              </p>
              <div className={styles.infoGrid}>
                <div className={styles.infoItem}>
                  <p className={styles.infoItemTitle}>API Communication</p>
                  <p className={styles.infoItemVal}>RESTful JSON / Axios</p>
                </div>
                <div className={styles.infoItem}>
                  <p className={styles.infoItemTitle}>Auth Mechanism</p>
                  <p className={styles.infoItemVal}>JWT Bearer Interceptor</p>
                </div>
                <div className={styles.infoItem}>
                  <p className={styles.infoItemTitle}>Access Control</p>
                  <p className={styles.infoItemVal}>Role-Based (RBAC)</p>
                </div>
                <div className={styles.infoItem}>
                  <p className={styles.infoItemTitle}>UI Architecture</p>
                  <p className={styles.infoItemVal}>React 19 / CSS Modules</p>
                </div>
              </div>
            </div>

            <div className={styles.infoCard}>
              <h3 className={styles.actionCardTitle}>Quick Navigation</h3>
              <p className={styles.actionCardDesc}>Fast access to administrative modules:</p>
              <div style={{ display: 'grid', gap: '0.5rem' }}>
                <Button variant="ghost" onClick={() => navigate('/admin/users')}>
                  → Users List
                </Button>
                <Button variant="ghost" onClick={() => navigate('/admin/projects')}>
                  → Projects Master
                </Button>
                <Button variant="ghost" onClick={() => navigate('/admin/departments')}>
                  → Department Master
                </Button>
                <Button variant="ghost" onClick={() => navigate('/admin/roles')}>
                  → Security Roles
                </Button>
              </div>
            </div>
          </section>
        </>
      )}
    </>
  )
}

export default AdminDashboardPage

