import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import EmptyState from '../../components/common/EmptyState'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import DashboardSkeleton from '../../components/dashboard/DashboardSkeleton'
import { useAuth } from '../../hooks/useAuth'
import { pmService } from '../../services/pmService'
import { getApiError } from '../../utils/apiError'
import pageStyles from './PmDashboardPage.module.css'

const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}

const display = (value) => value ?? 'Not available'

function PmDashboardPage() {
  const [projects, setProjects] = useState([])
  const [projectTotal, setProjectTotal] = useState(0)
  const [selectedProjectId, setSelectedProjectId] = useState(null)
  const [dashboard, setDashboard] = useState(null)
  const [isProjectsLoading, setIsProjectsLoading] = useState(true)
  const [isDashboardLoading, setIsDashboardLoading] = useState(false)
  const [error, setError] = useState('')

  const { user } = useAuth()
  const navigate = useNavigate()

  const pmName = user
    ? `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email || 'Project Manager'
    : 'Project Manager'

  const loadProjects = useCallback(async () => {
    setIsProjectsLoading(true)
    setError('')
    try {
      const data = extractData(
        await pmService.getAssignedProjects({
          pageNo: 0,
          pageSize: 10,
          sortBy: 'id',
          sortDir: 'asc',
        }),
      )
      setProjects(data.content || [])
      setProjectTotal(data.totalElements ?? data.content?.length ?? 0)
      setSelectedProjectId((current) => current || data.content?.[0]?.id || null)
    } catch (requestError) {
      const apiError = getApiError(requestError)
      setError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsProjectsLoading(false)
    }
  }, [])

  const loadDashboard = useCallback(async () => {
    if (!selectedProjectId) {
      setDashboard(null)
      return
    }
    setIsDashboardLoading(true)
    setError('')
    try {
      const [overview, progress, stats] = await Promise.all([
        pmService.getProjectOverview(selectedProjectId),
        pmService.getProjectProgress(selectedProjectId),
        pmService.getProjectStats(selectedProjectId),
      ])
      setDashboard({
        overview: extractData(overview),
        progress: extractData(progress),
        stats: extractData(stats),
      })
    } catch (requestError) {
      const apiError = getApiError(requestError)
      setError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsDashboardLoading(false)
    }
  }, [selectedProjectId])

  useEffect(() => {
    void Promise.resolve().then(loadProjects)
  }, [loadProjects])

  useEffect(() => {
    void Promise.resolve().then(loadDashboard)
  }, [loadDashboard])

  const selectedProject = projects.find((p) => p.id === selectedProjectId)

  if (isProjectsLoading) {
    return (
      <>
        <PageHeader
          title="Project Manager Dashboard"
          description="Review delivery, team progress, and task metrics across your assigned projects."
        />
        <DashboardSkeleton />
      </>
    )
  }

  if (error && !isDashboardLoading && !projects.length) {
    return (
      <>
        <PageHeader
          title="Project Manager Dashboard"
          description="Review delivery, team progress, and task metrics across your assigned projects."
        />
        <EmptyState
          title="Dashboard Unavailable"
          description={error}
          action={<Button onClick={loadProjects}>Try again</Button>}
        />
      </>
    )
  }

  const completionPct =
    dashboard?.progress?.completionPercentage ??
    dashboard?.overview?.completionPercentage ??
    0

  const recentTasksColumns = [
    { key: 'title', header: 'Task Name' },
    { key: 'projectName', header: 'Project' },
    { key: 'dueDate', header: 'Due Date' },
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
        title="Project Manager Dashboard"
        description="Review delivery, team progress, and task metrics across your assigned projects."
      />

      {/* Hero Welcome Banner */}
      <section className={pageStyles.heroCard}>
        <div className={pageStyles.heroContent}>
          <span className={pageStyles.heroTag}>Project Manager Workspace</span>
          <h2 className={pageStyles.heroTitle}>Welcome back, {pmName}</h2>
          <p className={pageStyles.heroSubtitle}>
            {selectedProject
              ? `Currently monitoring ${selectedProject.projectName}${selectedProject.projectCode ? ` (${selectedProject.projectCode})` : ''}. Track deadlines, progress metrics, and team performance.`
              : 'Select an assigned project below to inspect delivery statistics and team task status.'}
          </p>
        </div>
        <div className={pageStyles.heroBadges}>
          <div className={pageStyles.statusPill}>
            <span className={pageStyles.dot} />
            Assigned Projects: {projectTotal}
          </div>
          <div className={pageStyles.statusPill}>Delivery Status: Active</div>
        </div>
      </section>

      {/* Assigned Projects Selector */}
      <div className={pageStyles.sectionHeader}>
        <h2 className={pageStyles.sectionTitle}>Assigned Projects ({projectTotal})</h2>
      </div>

      {projects.length ? (
        <div className={pageStyles.projectList} role="radiogroup" aria-label="Assigned projects selection">
          {projects.map((project) => {
            const isSelected = selectedProjectId === project.id
            return (
              <button
                key={project.id}
                type="button"
                role="radio"
                aria-checked={isSelected}
                className={`${pageStyles.projectButton} ${
                  isSelected ? pageStyles.projectButtonActive : ''
                }`}
                onClick={() => setSelectedProjectId(project.id)}
              >
                {isSelected && <span className={pageStyles.activeCheck}>✓</span>}
                <p className={pageStyles.projectTitle}>{project.projectName}</p>
                <span className={pageStyles.projectCode}>
                  {project.projectCode || 'Project workspace'}
                </span>
              </button>
            )
          })}
        </div>
      ) : (
        <EmptyState
          title="No Assigned Projects"
          description="You currently have no assigned projects in Flow Deck."
        />
      )}

      {isDashboardLoading && <DashboardSkeleton />}

      {error && !isDashboardLoading && selectedProjectId && (
        <EmptyState
          title="Project Dashboard Unavailable"
          description={error}
          action={<Button onClick={loadDashboard}>Try again</Button>}
        />
      )}

      {dashboard && !isDashboardLoading && (
        <>
          {/* Metrics Cards Grid */}
          <section className={pageStyles.metricsGrid}>
            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>Assigned Projects</p>
                <span className={pageStyles.metricIcon}>📁</span>
              </div>
              <p className={pageStyles.metricValue}>{projectTotal}</p>
            </div>

            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>Total Tasks</p>
                <span className={pageStyles.metricIcon}>📋</span>
              </div>
              <p className={pageStyles.metricValue}>{dashboard.overview.totalTasks ?? 0}</p>
            </div>

            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>Completed Tasks</p>
                <span className={pageStyles.metricIcon}>✅</span>
              </div>
              <p className={pageStyles.metricValue}>{dashboard.overview.completedTasks ?? 0}</p>
            </div>

            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>Pending Tasks</p>
                <span className={pageStyles.metricIcon}>⏳</span>
              </div>
              <p className={pageStyles.metricValue}>{dashboard.overview.pendingTasks ?? 0}</p>
            </div>

            <div className={pageStyles.metricCard}>
              <div className={pageStyles.metricHeader}>
                <p className={pageStyles.metricLabel}>In Progress</p>
                <span className={pageStyles.metricIcon}>⚡</span>
              </div>
              <p className={pageStyles.metricValue}>{dashboard.overview.inProgressTasks ?? 0}</p>
            </div>
          </section>

          {/* Progress & Stats Split Section */}
          <div className={pageStyles.detailGrid}>
            <div className={pageStyles.progressCard}>
              <div className={pageStyles.progressHeader}>
                <h3>Project Progress</h3>
                <span className={pageStyles.progressPercent}>{Math.round(completionPct)}%</span>
              </div>

              <div className={pageStyles.progressTrack}>
                <div
                  className={pageStyles.progressFill}
                  style={{ width: `${Math.min(100, Math.max(0, completionPct))}%` }}
                />
              </div>

              <div className={pageStyles.progressPills}>
                <span className={pageStyles.progressPill}>
                  In Progress: {display(dashboard.overview.inProgressTasks)}
                </span>
                <span className={pageStyles.progressPill}>
                  Overdue: {display(dashboard.overview.overdueTasks)}
                </span>
                <span className={pageStyles.progressPill}>
                  Completed: {display(dashboard.overview.completedTasks)}
                </span>
              </div>
            </div>

            <div className={pageStyles.statsCard}>
              <h3 style={{ margin: 0, fontSize: '1.0625rem', fontWeight: 700 }}>
                Project Overview
              </h3>
              <div className={pageStyles.statsList}>
                <div className={pageStyles.statsRow}>
                  <span className={pageStyles.statsRowLabel}>Team Members</span>
                  <span className={pageStyles.statsRowVal}>
                    {display(dashboard.stats.totalMembers ?? dashboard.overview.totalMembers)}
                  </span>
                </div>
                <div className={pageStyles.statsRow}>
                  <span className={pageStyles.statsRowLabel}>Total Tasks</span>
                  <span className={pageStyles.statsRowVal}>
                    {display(dashboard.stats.totalTasks ?? dashboard.overview.totalTasks)}
                  </span>
                </div>
                <div className={pageStyles.statsRow}>
                  <span className={pageStyles.statsRowLabel}>Completed Tasks</span>
                  <span className={pageStyles.statsRowVal}>
                    {display(dashboard.stats.completedTasks ?? dashboard.overview.completedTasks)}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Recent Tasks */}
          <div className={pageStyles.sectionHeader}>
            <h2 className={pageStyles.sectionTitle}>Recent Tasks</h2>
          </div>
          {dashboard.overview.recentlyUpdatedTasks?.length ? (
            <DataTable
              columns={recentTasksColumns}
              data={dashboard.overview.recentlyUpdatedTasks}
            />
          ) : (
            <EmptyState
              title="No Recent Tasks"
              description="There are no recent tasks recorded for this project."
            />
          )}

          {/* Quick Actions Cards */}
          <div className={pageStyles.sectionHeader}>
            <h2 className={pageStyles.sectionTitle}>Quick Management Actions</h2>
          </div>
          <div className={pageStyles.quickActionsGrid}>
            <div className={pageStyles.actionCard}>
              <div>
                <h3 className={pageStyles.actionTitle}>Assigned Projects</h3>
                <p className={pageStyles.actionDesc}>
                  Inspect project details, view statistics, and manage team member allocations.
                </p>
              </div>
              <Button onClick={() => navigate('/pm/projects')}>Manage Projects</Button>
            </div>

            <div className={pageStyles.actionCard}>
              <div>
                <h3 className={pageStyles.actionTitle}>Task Workspace</h3>
                <p className={pageStyles.actionDesc}>
                  Create tasks, assign members, update priority/status, set due dates, and monitor filter metrics.
                </p>
              </div>
              <Button variant="secondary" onClick={() => navigate('/pm/tasks')}>
                Manage Tasks
              </Button>
            </div>
          </div>
        </>
      )}
    </>
  )
}

export default PmDashboardPage
