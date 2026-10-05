import { useCallback, useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import Card from '../../components/common/Card'
import DataTable from '../../components/common/DataTable'
import EmptyState from '../../components/common/EmptyState'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import PageHeader from '../../components/common/PageHeader'
import { employeeService } from '../../services/employeeService'
import { getApiError } from '../../utils/apiError'
import styles from './MyProjectsPage.module.css'

const initialPage = { content: [], pageNo: 0, pageSize: 10, totalPages: 0 }

const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}

const display = (value) => (value !== null && value !== undefined && value !== '' ? value : 'Not available')

const managerName = (project) =>
  project?.managerName || project?.projectManagerName || 'Not assigned'

const memberName = (member) =>
  member?.userName || member?.name || member?.email || 'Not available'

const memberColumns = [
  {
    key: 'member',
    header: 'Team Member',
    render: (member) => {
      const name = memberName(member)
      const initial = name.charAt(0).toUpperCase() || 'M'
      return (
        <div className={styles.memberCell}>
          <span className={styles.memberAvatar}>{initial}</span>
          <span className={styles.memberName}>{name}</span>
        </div>
      )
    },
  },
  {
    key: 'role',
    header: 'Role',
    render: (member) => (
      <span style={{ display: 'inline-block', padding: '0.15rem 0.5rem', borderRadius: '4px', background: 'rgba(15, 118, 110, 0.1)', color: '#0f766e', fontSize: '0.75rem', fontWeight: 700 }}>
        {member?.roleName || member?.role || 'Team Member'}
      </span>
    ),
  },
  { key: 'email', header: 'Email Address', render: (member) => member?.email || 'N/A' },
]

export default function MyProjectsPage() {
  const [page, setPage] = useState(initialPage)
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')
  const [isLoading, setIsLoading] = useState(true)
  const [requestError, setRequestError] = useState('')
  const [modal, setModal] = useState(null)
  const [activeTab, setActiveTab] = useState('overview')

  const loadProjects = useCallback(async () => {
    setIsLoading(true)
    setRequestError('')
    try {
      const response = await employeeService.getAssignedProjects({
        pageNo: page.pageNo,
        pageSize: page.pageSize,
        sortBy,
        sortDir,
      })
      const data = extractData(response)
      setPage((current) => ({ ...current, ...data }))
    } catch (error) {
      const apiError = getApiError(error)
      setRequestError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsLoading(false)
    }
  }, [page.pageNo, page.pageSize, sortBy, sortDir])

  useEffect(() => {
    void Promise.resolve().then(loadProjects)
  }, [loadProjects])

  const openProjectDetails = async (projectId) => {
    setModal({ loading: true })
    setActiveTab('overview')
    try {
      const [detailsRes, progressRes, membersRes] = await Promise.all([
        employeeService.getProjectDetails(projectId),
        employeeService.getProjectProgress(projectId),
        employeeService.getProjectMembers(projectId),
      ])
      const details = extractData(detailsRes)
      const progress = extractData(progressRes)
      const members = extractData(membersRes)

      setModal({ details, progress, members })
    } catch (error) {
      setModal(null)
      toast.error(getApiError(error).message)
    }
  }

  const getStatusBadge = (status) => {
    const name = String(status || 'NOT_STARTED').toUpperCase()
    const badgeClass = name.includes('COMPLET')
      ? styles.badgeCompleted
      : name.includes('PROGRESS')
        ? styles.badgeInProgress
        : name.includes('HOLD')
          ? styles.badgeOnHold
          : styles.badgeNotStarted
    return <span className={`${styles.badge} ${badgeClass}`}>{status || 'NOT_STARTED'}</span>
  }

  const columns = [
    {
      key: 'projectName',
      header: 'Project Info',
      render: (project) => (
        <div className={styles.projectNameCell}>
          <span className={styles.projectName}>{project.projectName}</span>
          {project.projectCode && (
            <span className={styles.projectCode}>{project.projectCode}</span>
          )}
        </div>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      render: (project) => getStatusBadge(project.status),
    },
    {
      key: 'startDate',
      header: 'Start Date',
      render: (project) =>
        project.startDate ? new Date(project.startDate).toLocaleDateString() : 'N/A',
    },
    {
      key: 'endDate',
      header: 'Target End Date',
      render: (project) =>
        project.endDate ? new Date(project.endDate).toLocaleDateString() : 'N/A',
    },
    { key: 'manager', header: 'Project Manager', render: managerName },
    {
      key: 'actions',
      header: 'Actions',
      render: (project) => (
        <div className={styles.actions}>
          <Button onClick={() => openProjectDetails(project.id)}>
            View Details
          </Button>
        </div>
      ),
    },
  ]

  const completionPct = Math.min(
    100,
    Math.max(0, modal?.progress?.completionPercentage || 0),
  )

  return (
    <>
      <PageHeader
        title="My Assigned Projects"
        description="View your project assignments, execution progress, project manager details, and team members."
      />

      <section className={styles.toolbar}>
        <div className={styles.sortControl}>
          <label htmlFor="sortBy">Sort By</label>
          <select
            id="sortBy"
            value={sortBy}
            onChange={(event) => {
              setSortBy(event.target.value)
              setPage((current) => ({ ...current, pageNo: 0 }))
            }}
          >
            <option value="id">ID</option>
            <option value="projectName">Project Name</option>
            <option value="startDate">Start Date</option>
          </select>
        </div>
        <div className={styles.sortControl}>
          <label htmlFor="sortDir">Direction</label>
          <select
            id="sortDir"
            value={sortDir}
            onChange={(event) => {
              setSortDir(event.target.value)
              setPage((current) => ({ ...current, pageNo: 0 }))
            }}
          >
            <option value="asc">Ascending (A-Z)</option>
            <option value="desc">Descending (Z-A)</option>
          </select>
        </div>
      </section>

      {isLoading && (
        <div className={styles.loading}>
          <Loader label="Loading assigned projects" />
        </div>
      )}

      {requestError && !isLoading && (
        <EmptyState
          title="Assigned Projects Unavailable"
          description={requestError}
          action={<Button onClick={loadProjects}>Try again</Button>}
        />
      )}

      {!isLoading && !requestError && (
        <DataTable
          columns={columns}
          data={page.content || []}
          emptyMessage="No projects are currently assigned to your profile."
          pagination={{
            page: page.pageNo + 1,
            totalPages: page.totalPages,
            onPageChange: (nextPage) => setPage((current) => ({ ...current, pageNo: nextPage - 1 })),
          }}
        />
      )}

      {modal && (
        <Modal
          isOpen
          title={modal.loading ? 'Loading Project Details' : `${modal.details?.projectName || 'Project Details'}`}
          onClose={() => setModal(null)}
        >
          {modal.loading ? (
            <div className={styles.loading}>
              <Loader label="Fetching project details" />
            </div>
          ) : (
            <div className={styles.workspaceLayout}>
              <nav className={styles.tabBar} aria-label="Project Details Navigation">
                <button
                  className={`${styles.tabButton} ${activeTab === 'overview' ? styles.activeTab : ''}`}
                  onClick={() => setActiveTab('overview')}
                >
                  Overview
                </button>
                <button
                  className={`${styles.tabButton} ${activeTab === 'progress' ? styles.activeTab : ''}`}
                  onClick={() => setActiveTab('progress')}
                >
                  Progress & Deliverables
                </button>
                <button
                  className={`${styles.tabButton} ${activeTab === 'members' ? styles.activeTab : ''}`}
                  onClick={() => setActiveTab('members')}
                >
                  Team Members ({(modal.members || []).length})
                </button>
              </nav>

              {activeTab === 'overview' && (
                <div style={{ display: 'grid', gap: '1.25rem' }}>
                  <Card>
                    <div style={{ marginBottom: '1rem', paddingBottom: '0.75rem', borderBottom: '1px solid var(--color-border)' }}>
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem' }}>
                        <h3 style={{ margin: 0, fontSize: '1.125rem', fontWeight: 800 }}>
                          {modal.details?.projectName || 'Untitled Project'}
                        </h3>
                        {getStatusBadge(modal.details?.status)}
                      </div>
                    </div>
                    <dl className={styles.detailsGrid}>
                      <div>
                        <dt>Project Code</dt>
                        <dd>{display(modal.details?.projectCode)}</dd>
                      </div>
                      <div>
                        <dt>Execution Status</dt>
                        <dd>{display(modal.details?.status)}</dd>
                      </div>
                      <div>
                        <dt>Project Manager</dt>
                        <dd>{display(managerName(modal.details))}</dd>
                      </div>
                      <div>
                        <dt>Start Date</dt>
                        <dd>{modal.details?.startDate ? new Date(modal.details.startDate).toLocaleDateString() : 'N/A'}</dd>
                      </div>
                      <div>
                        <dt>Target End Date</dt>
                        <dd>{modal.details?.endDate ? new Date(modal.details.endDate).toLocaleDateString() : 'N/A'}</dd>
                      </div>
                      <div style={{ gridColumn: '1 / -1' }}>
                        <dt>Project Description</dt>
                        <dd style={{ lineHeight: 1.5 }}>{display(modal.details?.description)}</dd>
                      </div>
                    </dl>
                  </Card>

                  <div className={styles.metricsGrid}>
                    <Card>
                      <div className={styles.metricCard}>
                        <span className={styles.metricTitle}>Total Workspace Tasks</span>
                        <span className={styles.metricValue}>{display(modal.progress?.totalTasks ?? 0)}</span>
                      </div>
                    </Card>
                    <Card>
                      <div className={styles.metricCard}>
                        <span className={styles.metricTitle}>Completed Deliverables</span>
                        <span className={styles.metricValue} style={{ color: '#16a34a' }}>
                          {display(modal.progress?.completedTasks ?? 0)}
                        </span>
                      </div>
                    </Card>
                    <Card>
                      <div className={styles.metricCard}>
                        <span className={styles.metricTitle}>Pending Tasks</span>
                        <span className={styles.metricValue} style={{ color: '#d97706' }}>
                          {display(modal.progress?.pendingTasks ?? 0)}
                        </span>
                      </div>
                    </Card>
                  </div>
                </div>
              )}

              {activeTab === 'progress' && (
                <div style={{ display: 'grid', gap: '1.25rem' }}>
                  <Card>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem', marginBottom: '0.75rem' }}>
                      <h3 style={{ margin: 0, fontSize: '1.125rem', fontWeight: 800 }}>Project Execution Progress</h3>
                      <span style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--color-primary)' }}>
                        {completionPct}%
                      </span>
                    </div>
                    <div className={styles.progressBarContainer}>
                      <div className={styles.progressFill} style={{ width: `${completionPct}%` }} />
                    </div>
                    <p style={{ margin: 0, fontSize: '0.8125rem', color: 'var(--color-text-secondary)' }}>
                      Overall workflow completion rate across assigned team tasks.
                    </p>
                  </Card>

                  <div className={styles.metricsGrid}>
                    <Card>
                      <div className={styles.metricCard}>
                        <span className={styles.metricTitle}>In Progress Tasks</span>
                        <span className={styles.metricValue} style={{ color: '#0f766e' }}>
                          {display(modal.progress?.inProgressTasks ?? 0)}
                        </span>
                      </div>
                    </Card>
                    <Card>
                      <div className={styles.metricCard}>
                        <span className={styles.metricTitle}>Pending Tasks</span>
                        <span className={styles.metricValue} style={{ color: '#d97706' }}>
                          {display(modal.progress?.pendingTasks ?? 0)}
                        </span>
                      </div>
                    </Card>
                    <Card>
                      <div className={styles.metricCard}>
                        <span className={styles.metricTitle}>Overdue Tasks</span>
                        <span className={styles.metricValue} style={{ color: '#dc2626' }}>
                          {display(modal.progress?.overdueTasks ?? 0)}
                        </span>
                      </div>
                    </Card>
                  </div>
                </div>
              )}

              {activeTab === 'members' && (
                <section style={{ display: 'grid', gap: '1rem' }}>
                  <h3 style={{ margin: 0, fontSize: '1.125rem', fontWeight: 800 }}>Assigned Team Members</h3>
                  <DataTable
                    columns={memberColumns}
                    data={modal.members || []}
                    emptyMessage="No team members are assigned to this project."
                  />
                </section>
              )}
            </div>
          )}
        </Modal>
      )}
    </>
  )
}
