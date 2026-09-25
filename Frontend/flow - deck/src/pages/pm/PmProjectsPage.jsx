import { useCallback, useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import DataTable from '../../components/common/DataTable'
import EmptyState from '../../components/common/EmptyState'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import PageHeader from '../../components/common/PageHeader'
import ProjectWorkspaceDetails from '../../components/pm/ProjectWorkspaceDetails'
import { pmService } from '../../services/pmService'
import { getApiError } from '../../utils/apiError'
import styles from './PmProjectsPage.module.css'

const initialPage = { content: [], pageNo: 0, pageSize: 10, totalPages: 0 }
const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}
const managerName = (project) =>
  project.managerName || project.projectManagerName || 'Not assigned'

function PmProjectsPage() {
  const [page, setPage] = useState(initialPage)
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')
  const [isLoading, setIsLoading] = useState(true)
  const [requestError, setRequestError] = useState('')
  const [modal, setModal] = useState(null)

  const loadProjects = useCallback(async () => {
    setIsLoading(true)
    setRequestError('')
    try {
      const data = extractData(
        await pmService.getAssignedProjects({
          pageNo: page.pageNo,
          pageSize: page.pageSize,
          sortBy,
          sortDir,
        }),
      )
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

  const openProject = async (projectId) => {
    setModal({ loading: true })
    try {
      const [details, members, overview, progress, stats, summaryRes] = await Promise.all([
        pmService.getProjectDetails(projectId),
        pmService.getProjectMembers(projectId),
        pmService.getProjectOverview(projectId),
        pmService.getProjectProgress(projectId),
        pmService.getProjectStats(projectId),
        pmService.getEmployeeSummary(projectId).catch(() => ({ success: true, data: [] })),
      ])

      let summaryData = []
      if (summaryRes && summaryRes.success && summaryRes.data) {
        summaryData = Array.isArray(summaryRes.data) ? summaryRes.data : [summaryRes.data]
      }

      setModal({
        details: extractData(details),
        members: extractData(members),
        overview: extractData(overview),
        progress: extractData(progress),
        stats: extractData(stats),
        employeeSummary: summaryData,
      })
    } catch (error) {
      setModal(null)
      toast.error(getApiError(error).message)
    }
  }

  const refreshMembers = async (projectId) => {
    try {
      const response = await pmService.getProjectMembers(projectId)
      const members = extractData(response)
      setModal((prev) => (prev ? { ...prev, members: Array.isArray(members) ? members : [] } : prev))
    } catch (error) {
      toast.error(getApiError(error).message)
    }
  }

  const handleAddMember = async (projectId, payload) => {
    try {
      const response = await pmService.addProjectMember(projectId, payload)
      extractData(response)
      toast.success(response?.message || 'Member assigned to project workspace successfully!')
      await refreshMembers(projectId)
    } catch (error) {
      const apiError = getApiError(error)
      toast.error(apiError.message)
      throw error
    }
  }

  const handleRemoveMember = async (projectId, userId) => {
    try {
      const response = await pmService.removeProjectMember(projectId, userId)
      extractData(response)
      toast.success(response?.message || 'Member removed from project workspace successfully!')
      await refreshMembers(projectId)
    } catch (error) {
      const apiError = getApiError(error)
      toast.error(apiError.message)
      throw error
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
          <Button onClick={() => openProject(project.id)}>
            View Workspace
          </Button>
        </div>
      ),
    },
  ]

  return (
    <>
      <PageHeader
        title="Assigned Projects"
        description="Monitor project scope, delivery schedules, completion metrics, and team workspace details."
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
          <Loader label="Loading assigned project records" />
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
          emptyMessage="No projects are currently assigned to your manager profile."
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
          title={modal.loading ? 'Loading Project Workspace' : `${modal.details?.projectName || 'Project Workspace'} Workspace`}
          onClose={() => setModal(null)}
        >
          {modal.loading ? (
            <div className={styles.loading}>
              <Loader label="Fetching project workspace details" />
            </div>
          ) : (
            <ProjectWorkspaceDetails
              details={modal.details}
              members={modal.members}
              overview={modal.overview}
              progress={modal.progress}
              stats={modal.stats}
              employeeSummary={modal.employeeSummary}
              onAddMember={handleAddMember}
              onRemoveMember={handleRemoveMember}
            />
          )}
        </Modal>
      )}
    </>
  )
}

export default PmProjectsPage
