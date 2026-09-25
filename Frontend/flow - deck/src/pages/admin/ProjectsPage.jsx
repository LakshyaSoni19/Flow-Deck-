import { useCallback, useEffect, useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import ConfirmDialog from '../../components/common/ConfirmDialog'
import DataTable from '../../components/common/DataTable'
import EmptyState from '../../components/common/EmptyState'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import PageHeader from '../../components/common/PageHeader'
import SearchBar from '../../components/common/SearchBar'
import { AssignManagerForm, ChangeStatusForm } from '../../components/projects/ProjectActionForms'
import ProjectDetails from '../../components/projects/ProjectDetails'
import ProjectForm from '../../components/projects/ProjectForm'
import { adminService } from '../../services/adminService'
import { getApiError } from '../../utils/apiError'
import styles from './ProjectsPage.module.css'

const initialPage = { content: [], pageNo: 0, pageSize: 10, totalPages: 0 }
const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}
const managerName = (project) =>
  project.managerName ||
  project.projectManagerName ||
  'Not assigned'

function ProjectsPage() {
  const [page, setPage] = useState(initialPage)
  const [search, setSearch] = useState('')
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')
  const [isLoading, setIsLoading] = useState(true)
  const [requestError, setRequestError] = useState('')
  const [modal, setModal] = useState(null)
  const [isSaving, setIsSaving] = useState(false)
  const [pendingDelete, setPendingDelete] = useState(null)
  const [isDeleting, setIsDeleting] = useState(false)

  const loadProjects = useCallback(async () => {
    setIsLoading(true)
    setRequestError('')
    try {
      const response = await adminService.getProjects({
        pageNo: page.pageNo,
        pageSize: page.pageSize,
        sortBy,
        sortDir,
      })
      setPage((current) => ({ ...current, ...extractData(response) }))
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

  const visibleProjects = useMemo(
    () =>
      page.content.filter((project) =>
        `${project.projectName || ''} ${project.projectCode || ''} ${project.description || ''}`
          .toLowerCase()
          .includes(search.trim().toLowerCase()),
      ),
    [page.content, search],
  )

  const openProjectModal = async (mode, projectId) => {
    setModal({ loading: true, mode })
    try {
      setModal({ mode, project: extractData(await adminService.getProjectById(projectId)) })
    } catch (error) {
      setModal(null)
      toast.error(getApiError(error).message)
    }
  }

  const saveProject = async (payload, setFieldErrors) => {
    setIsSaving(true)
    try {
      const response = modal?.project
        ? await adminService.updateProject(modal.project.id, payload)
        : await adminService.createProject(payload)
      extractData(response)
      toast.success(response.message)
      setModal(null)
      loadProjects()
    } catch (error) {
      const apiError = getApiError(error)
      setFieldErrors(apiError.fields || {})
      toast.error(apiError.message)
    } finally {
      setIsSaving(false)
    }
  }

  const updateProjectAction = async (request, setFieldError) => {
    setIsSaving(true)
    try {
      const response = await request()
      extractData(response)
      toast.success(response.message)
      setModal(null)
      loadProjects()
    } catch (error) {
      const apiError = getApiError(error)
      setFieldError?.(apiError.fields?.managerId || apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsSaving(false)
    }
  }

  const deleteProject = async () => {
    if (!pendingDelete) return
    setIsDeleting(true)
    try {
      const response = await adminService.deleteProject(pendingDelete.id)
      extractData(response)
      toast.success(response.message)
      setPendingDelete(null)
      if (page.content.length === 1 && page.pageNo > 0) {
        setPage((current) => ({ ...current, pageNo: current.pageNo - 1 }))
      } else {
        loadProjects()
      }
    } catch (error) {
      toast.error(getApiError(error).message)
    } finally {
      setIsDeleting(false)
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
    {
      key: 'manager',
      header: 'Project Manager',
      render: managerName,
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (project) => (
        <div className={styles.actions}>
          <Button variant="secondary" onClick={() => openProjectModal('details', project.id)}>
            View
          </Button>
          <Button variant="secondary" onClick={() => openProjectModal('edit', project.id)}>
            Edit
          </Button>
          <Button variant="ghost" onClick={() => setModal({ mode: 'manager', project })}>
            Manager
          </Button>
          <Button variant="ghost" onClick={() => setModal({ mode: 'status', project })}>
            Status
          </Button>
          <Button
            variant="ghost"
            className={styles.deleteBtn}
            onClick={() => setPendingDelete(project)}
          >
            Delete
          </Button>
        </div>
      ),
    },
  ]

  const modalTitle = modal?.loading
    ? 'Loading Project'
    : modal?.mode === 'details'
      ? 'Project Details'
      : modal?.mode === 'edit'
        ? 'Edit Project'
        : modal?.mode === 'manager'
          ? 'Assign Project Manager'
          : modal?.mode === 'status'
            ? 'Change Project Status'
            : 'Create Project'

  return (
    <>
      <PageHeader
        title="Project Management"
        description="Master administration for project creation, workflow execution status, and assigned project managers."
        actions={
          <Button onClick={() => setModal({ mode: 'create' })}>
            + Add Project
          </Button>
        }
      />

      <section className={styles.toolbar}>
        <SearchBar
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Search projects by title or code..."
        />
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
          <Loader label="Loading project records" />
        </div>
      )}

      {requestError && !isLoading && (
        <EmptyState
          title="Projects Unavailable"
          description={requestError}
          action={<Button onClick={loadProjects}>Try again</Button>}
        />
      )}

      {!isLoading && !requestError && (
        <DataTable
          columns={columns}
          data={visibleProjects}
          emptyMessage={
            search ? 'No projects match your search query.' : 'No projects have been created yet.'
          }
          pagination={{
            page: page.pageNo + 1,
            totalPages: page.totalPages,
            onPageChange: (nextPage) =>
              setPage((current) => ({ ...current, pageNo: nextPage - 1 })),
          }}
        />
      )}

      {modal && (
        <Modal
          isOpen
          title={modalTitle}
          onClose={() => !isSaving && setModal(null)}
        >
          {modal.loading ? (
            <div className={styles.loading}>
              <Loader label="Fetching project details" />
            </div>
          ) : modal.mode === 'details' ? (
            <ProjectDetails project={modal.project} />
          ) : modal.mode === 'manager' ? (
            <AssignManagerForm
              project={modal.project}
              isSaving={isSaving}
              onSubmit={(payload, setFieldError) =>
                updateProjectAction(
                  () => adminService.assignProjectManager(modal.project.id, payload),
                  setFieldError,
                )
              }
            />
          ) : modal.mode === 'status' ? (
            <ChangeStatusForm
              currentStatus={modal.project.status}
              isSaving={isSaving}
              onSubmit={(payload) =>
                updateProjectAction(() =>
                  adminService.changeProjectStatus(modal.project.id, payload),
                )
              }
            />
          ) : (
            <ProjectForm
              project={modal.mode === 'edit' ? modal.project : null}
              isSaving={isSaving}
              onSubmit={saveProject}
            />
          )}
        </Modal>
      )}

      <ConfirmDialog
        isOpen={Boolean(pendingDelete)}
        onClose={() => !isDeleting && setPendingDelete(null)}
        onConfirm={deleteProject}
        title="Delete Project"
        description={`Are you sure you want to delete "${pendingDelete?.projectName || 'this project'}"? All associated task progress will be removed.`}
        confirmLabel="Delete Project"
        isLoading={isDeleting}
      />
    </>
  )
}

export default ProjectsPage
