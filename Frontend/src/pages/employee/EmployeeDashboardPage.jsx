import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import DataTable from '../../components/common/DataTable'
import EmptyState from '../../components/common/EmptyState'
import PageHeader from '../../components/common/PageHeader'
import DashboardSkeleton from '../../components/dashboard/DashboardSkeleton'
import { useAuth } from '../../hooks/useAuth'
import { employeeService } from '../../services/employeeService'
import { getApiError } from '../../utils/apiError'
import pageStyles from './EmployeeDashboardPage.module.css'

const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}

export default function EmployeeDashboardPage() {
  const [data, setData] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')
  const navigate = useNavigate()
  const { user } = useAuth()

  const empName = user
    ? `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email || 'Employee'
    : 'Employee'

  const loadDashboard = useCallback(async () => {
    setIsLoading(true)
    setError('')
    try {
      const [overviewRes, projectsRes, tasksRes] = await Promise.all([
        employeeService.getDashboard(),
        employeeService
          .getAssignedProjects({ pageNo: 0, pageSize: 6, sortBy: 'id', sortDir: 'asc' })
          .catch((err) => ({ error: err })),
        employeeService
          .getAssignedTasks({ pageNo: 0, pageSize: 5, sortBy: 'id', sortDir: 'desc' })
          .catch((err) => ({ error: err })),
      ])

      const overview = extractData(overviewRes)

      let projects = { content: [] }
      if (projectsRes && !projectsRes.error) {
        try {
          projects = extractData(projectsRes)
        } catch {
          // Fallback to empty list
        }
      }

      let tasks = { content: [] }
      if (tasksRes && !tasksRes.error) {
        try {
          tasks = extractData(tasksRes)
        } catch {
          // Fallback to empty list
        }
      }

      setData({ overview, projects, tasks })
    } catch (requestError) {
      const apiError = getApiError(requestError)
      setError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    void Promise.resolve().then(loadDashboard)
  }, [loadDashboard])

  const completionPercentage = data?.overview?.completionPercentage ?? 0
  const upcomingDeadlines = data?.overview?.upcomingDeadlines?.length
    ? data.overview.upcomingDeadlines
    : data?.tasks?.content || []

  const deadlineColumns = [
    { key: 'title', header: 'Task Name' },
    { key: 'projectName', header: 'Project' },
    { key: 'dueDate', header: 'Due Date' },
    {
      key: 'priorityName',
      header: 'Priority',
      render: (task) => {
        const prio = String(task.priorityName || task.taskPriority || '').toUpperCase()
        if (prio.includes('HIGH'))
          return <span className={`${pageStyles.badge} ${pageStyles.badgeHigh}`}>High</span>
        if (prio.includes('MEDIUM'))
          return <span className={`${pageStyles.badge} ${pageStyles.badgeMedium}`}>Medium</span>
        return <span className={`${pageStyles.badge} ${pageStyles.badgeLow}`}>{task.priorityName || task.taskPriority || 'Low'}</span>
      },
    },
    {
      key: 'taskStatus',
      header: 'Status',
      render: (task) => {
        const name = String(task.taskStatus || '').toUpperCase()
        if (name.includes('COMPLET'))
          return <span className={`${pageStyles.badge} ${pageStyles.badgeCompleted}`}>Completed</span>
        if (name.includes('PROGRESS'))
          return <span className={`${pageStyles.badge} ${pageStyles.badgeInProgress}`}>In Progress</span>
        if (name.includes('OVERDUE'))
          return <span className={`${pageStyles.badge} ${pageStyles.badgeOverdue}`}>Overdue</span>
        return <span className={`${pageStyles.badge} ${pageStyles.badgePending}`}>{task.taskStatus || 'Pending'}</span>
      },
    },
  ]

  return (
    <>
      <PageHeader
        title="Employee Dashboard"
        description="Your personal work summary, task completion metrics, assigned projects, and upcoming deadlines."
      />

      {/* Hero Welcome Banner */}
      <section className={pageStyles.heroCard}>
        <div className={pageStyles.heroContent}>
          <span className={pageStyles.heroTag}>Employee Workspace</span>
          <h2 className={pageStyles.heroTitle}>Welcome back, {empName}</h2>
          <p className={pageStyles.heroSubtitle}>
            Welcome to your Flow Deck Employee Portal. Track your assigned tasks, upcoming deadlines, task completion progress, and project deliverables.
          </p>
        </div>
        <div className={pageStyles.heroBadges}>
          <div className={pageStyles.statusPill}>
            <span className={pageStyles.dot} />
            Account: Active
          </div>
          <div className={pageStyles.statusPill}>Role: Employee</div>
        </div>
      </section>

      {isLoading && <DashboardSkeleton count={5} />}

      {error && !isLoading && (
        <EmptyState
          title="Dashboard Unavailable"
          description={error}
          action={<Button onClick={loadDashboard}>Try again</Button>}
        />
      )}

      {data && !isLoading && (
        <>
          {/* Task Metrics Grid */}
          <section className={pageStyles.cardGrid}>
            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>Total Assigned Tasks</p>
                <span className={pageStyles.metricIcon}>📋</span>
              </div>
              <p className={pageStyles.metricValue}>{data.overview.totalAssignedTasks ?? 0}</p>
            </div>

            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>Completed Tasks</p>
                <span className={pageStyles.metricIcon}>✅</span>
              </div>
              <p className={pageStyles.metricValue}>{data.overview.completedTasks ?? 0}</p>
            </div>

            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>Pending Tasks</p>
                <span className={pageStyles.metricIcon}>⏳</span>
              </div>
              <p className={pageStyles.metricValue}>{data.overview.pendingTasks ?? 0}</p>
            </div>

            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>Overdue Tasks</p>
                <span className={pageStyles.metricIcon}>⚠️</span>
              </div>
              <p className={pageStyles.metricValue}>{data.overview.overdueTasks ?? 0}</p>
            </div>

            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>Completion Rate</p>
                <span className={pageStyles.metricIcon}>📊</span>
              </div>
              <p className={pageStyles.metricValue}>{Math.round(completionPercentage)}%</p>
            </div>
          </section>

          {/* Progress & Personal Summary Split Section */}
          <div className={pageStyles.detailGrid}>
            <div className={pageStyles.progressCard}>
              <div className={pageStyles.progressHeader}>
                <h3>Task Completion Progress</h3>
                <span className={pageStyles.progressPercent}>{Math.round(completionPercentage)}%</span>
              </div>

              <div className={pageStyles.progressTrack}>
                <div
                  className={pageStyles.progressFill}
                  style={{ width: `${Math.min(100, Math.max(0, completionPercentage))}%` }}
                />
              </div>

              <div className={pageStyles.progressPills}>
                <span className={pageStyles.progressPill}>
                  Assigned: {data.overview.totalAssignedTasks ?? 0}
                </span>
                <span className={pageStyles.progressPill}>
                  Completed: {data.overview.completedTasks ?? 0}
                </span>
                <span className={pageStyles.progressPill}>
                  Pending: {data.overview.pendingTasks ?? 0}
                </span>
                <span className={pageStyles.progressPill}>
                  Overdue: {data.overview.overdueTasks ?? 0}
                </span>
              </div>
            </div>

            <div className={pageStyles.summaryCard}>
              <h3 style={{ margin: 0, fontSize: '1.0625rem', fontWeight: 700 }}>
                Personal Work Summary
              </h3>
              <div className={pageStyles.summaryList}>
                <div className={pageStyles.summaryRow}>
                  <span style={{ color: 'var(--color-text-secondary)', fontWeight: 500 }}>
                    Completion Rate
                  </span>
                  <span style={{ fontWeight: 700, color: 'var(--color-primary)' }}>
                    {Math.round(completionPercentage)}%
                  </span>
                </div>
                <div className={pageStyles.summaryRow}>
                  <span style={{ color: 'var(--color-text-secondary)', fontWeight: 500 }}>
                    Total Assigned Tasks
                  </span>
                  <span style={{ fontWeight: 700 }}>{data.overview.totalAssignedTasks ?? 0}</span>
                </div>
                <div className={pageStyles.summaryRow}>
                  <span style={{ color: 'var(--color-text-secondary)', fontWeight: 500 }}>
                    Completed Tasks
                  </span>
                  <span style={{ fontWeight: 700 }}>{data.overview.completedTasks ?? 0}</span>
                </div>
                <div className={pageStyles.summaryRow}>
                  <span style={{ color: 'var(--color-text-secondary)', fontWeight: 500 }}>
                    Overdue Tasks
                  </span>
                  <span style={{ fontWeight: 700, color: 'var(--color-danger)' }}>
                    {data.overview.overdueTasks ?? 0}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Upcoming Deadline Tasks */}
          <div className={pageStyles.sectionHeader}>
            <h2 className={pageStyles.sectionTitle}>Upcoming Deadline Tasks</h2>
          </div>
          {upcomingDeadlines.length ? (
            <DataTable columns={deadlineColumns} data={upcomingDeadlines} />
          ) : (
            <EmptyState
              title="No Upcoming Deadlines"
              description="You have no pending deadlines or upcoming tasks scheduled."
            />
          )}

          {/* Assigned Projects */}
          <div className={pageStyles.sectionHeader}>
            <h2 className={pageStyles.sectionTitle}>Assigned Projects</h2>
          </div>
          {data.projects?.content?.length ? (
            <div className={pageStyles.projectGrid}>
              {data.projects.content.map((project) => (
                <div key={project.id} className={pageStyles.projectCard}>
                  <div>
                    <h3 className={pageStyles.projectName}>{project.projectName}</h3>
                    <p className={pageStyles.projectMeta}>
                      Code: {project.projectCode || 'N/A'} • Status: {project.status || 'Active'}
                    </p>
                  </div>
                  <Button variant="secondary" onClick={() => navigate('/employee/projects')}>
                    View Projects
                  </Button>
                </div>
              ))}
            </div>
          ) : (
            <EmptyState
              title="No Assigned Projects"
              description="There are currently no projects assigned to your profile."
            />
          )}

          {/* Quick Actions */}
          <div className={pageStyles.sectionHeader}>
            <h2 className={pageStyles.sectionTitle}>Quick Actions</h2>
          </div>
          <div className={pageStyles.quickActionsGrid}>
            <div className={pageStyles.actionCard}>
              <div>
                <h3 className={pageStyles.actionTitle}>My Projects</h3>
                <p className={pageStyles.actionDesc}>
                  Inspect project details, progress metrics, and assigned team members.
                </p>
              </div>
              <Button onClick={() => navigate('/employee/projects')}>View My Projects</Button>
            </div>

            <div className={pageStyles.actionCard}>
              <div>
                <h3 className={pageStyles.actionTitle}>My Tasks</h3>
                <p className={pageStyles.actionDesc}>
                  View assigned tasks, filter by pending/completed/overdue, update task status, and post comments.
                </p>
              </div>
              <Button variant="secondary" onClick={() => navigate('/employee/tasks')}>
                View My Tasks
              </Button>
            </div>

            <div className={pageStyles.actionCard}>
              <div>
                <h3 className={pageStyles.actionTitle}>My Profile</h3>
                <p className={pageStyles.actionDesc}>
                  Update personal profile details, contact information, and change account password.
                </p>
              </div>
              <Button variant="ghost" onClick={() => navigate('/employee/profile')}>
                My Profile & Settings
              </Button>
            </div>
          </div>
        </>
      )}
    </>
  )
}

