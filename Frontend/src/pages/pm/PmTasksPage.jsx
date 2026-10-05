import { useCallback, useEffect, useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import ConfirmDialog from '../../components/common/ConfirmDialog'
import DataTable from '../../components/common/DataTable'
import EmptyState from '../../components/common/EmptyState'
import FormLayout from '../../components/common/FormLayout'
import Input from '../../components/common/Input'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import PageHeader from '../../components/common/PageHeader'
import SearchBar from '../../components/common/SearchBar'
import { PriorityBadge, StatusBadge } from '../../components/tasks/TaskBadges'
import TaskDetails from '../../components/tasks/TaskDetails'
import TaskForm from '../../components/tasks/TaskForm'
import TaskActionSelect from '../../components/tasks/TaskActionSelect'
import { lookupService } from '../../services/lookupService'
import { pmService } from '../../services/pmService'
import { getApiError } from '../../utils/apiError'
import { getAssigneeLabel } from '../../utils/taskNormalization'
import styles from './PmTasksPage.module.css'

const initialPage = { content: [], pageNo: 0, pageSize: 10, totalPages: 0 }
const extract = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}

function PmTasksPage() {
  const [assignedProjects, setAssignedProjects] = useState([])
  const [projectId, setProjectId] = useState('')
  const [activeTab, setActiveTab] = useState('ALL')
  const [page, setPage] = useState(initialPage)
  const [unpaginatedTasks, setUnpaginatedTasks] = useState([])
  const [completionPercentage, setCompletionPercentage] = useState(null)

  const [search, setSearch] = useState('')
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')
  const [statusFilter, setStatusFilter] = useState('')
  const [priorityFilter, setPriorityFilter] = useState('')

  const [isLoading, setIsLoading] = useState(false)
  const [projectsError, setProjectsError] = useState('')
  const [error, setError] = useState('')
  const [modal, setModal] = useState(null)
  const [isSaving, setIsSaving] = useState(false)
  const [pendingDelete, setPendingDelete] = useState(null)
  const [isDeleting, setIsDeleting] = useState(false)

  // Load PM assigned projects on mount for easy selection
  useEffect(() => {
    pmService
      .getAssignedProjects({ pageNo: 0, pageSize: 50, sortBy: 'id', sortDir: 'asc' })
      .then((res) => {
        const data = extract(res)
        const projects = data.content || []
        setAssignedProjects(projects)
        if (projects.length > 0) {
          setProjectId(String(projects[0].id))
        }
      })
      .catch((requestError) => {
        setProjectsError(getApiError(requestError).message)
      })
  }, [])

  const loadTasks = useCallback(async () => {
    if (!/^\d+$/.test(projectId)) return
    setIsLoading(true)
    setError('')
    try {
      if (activeTab === 'ALL') {
        const [tasksRes, progressRes] = await Promise.all([
          pmService.getProjectTasks(projectId, {
            search,
            pageNo: page.pageNo,
            pageSize: page.pageSize,
            sortBy,
            sortDir,
          }),
          pmService.getTaskCompletionPercentage(projectId).catch(() => null),
        ])

        const data = extract(tasksRes)
        setPage((current) => ({ ...current, ...data }))

        if (progressRes) {
          try {
            const progData = extract(progressRes)
            setCompletionPercentage(progData.completionPercentage ?? progData)
          } catch {
            setCompletionPercentage(null)
          }
        }
      } else {
        let response
        if (activeTab === 'PENDING') {
          response = await pmService.getPendingTasks(projectId)
        } else if (activeTab === 'IN_PROGRESS') {
          response = await pmService.getInProgressTasks(projectId)
        } else if (activeTab === 'COMPLETED') {
          response = await pmService.getCompletedTasks(projectId)
        } else if (activeTab === 'OVERDUE') {
          response = await pmService.getOverdueTasks(projectId)
        } else if (activeTab === 'HIGH_PRIORITY') {
          response = await pmService.getHighPriorityTasks(projectId)
        }

        const data = extract(response)
        const taskList = Array.isArray(data) ? data : data?.content || []
        setUnpaginatedTasks(taskList)

        // Also fetch completion percentage
        pmService
          .getTaskCompletionPercentage(projectId)
          .then((res) => {
            const progData = extract(res)
            setCompletionPercentage(progData.completionPercentage ?? progData)
          })
          .catch(() => {})
      }
    } catch (requestError) {
      const apiError = getApiError(requestError)
      setError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsLoading(false)
    }
  }, [projectId, activeTab, search, page.pageNo, page.pageSize, sortBy, sortDir])

  useEffect(() => {
    void Promise.resolve().then(loadTasks)
  }, [loadTasks])

  const visibleTasks = useMemo(() => {
    let list = activeTab === 'ALL' ? page.content || [] : [...unpaginatedTasks]

    if (activeTab !== 'ALL') {
      if (search) {
        const term = search.toLowerCase()
        list = list.filter(
          (t) =>
            t.title?.toLowerCase().includes(term) ||
            t.description?.toLowerCase().includes(term),
        )
      }
      if (statusFilter) {
        list = list.filter((t) => t.taskStatus === statusFilter)
      }
      if (priorityFilter) {
        list = list.filter((t) => t.taskPriority === priorityFilter)
      }

      list.sort((a, b) => {
        let valA = a[sortBy] ?? ''
        let valB = b[sortBy] ?? ''
        if (typeof valA === 'string') valA = valA.toLowerCase()
        if (typeof valB === 'string') valB = valB.toLowerCase()

        if (valA < valB) return sortDir === 'asc' ? -1 : 1
        if (valA > valB) return sortDir === 'asc' ? 1 : -1
        return 0
      })
    } else {
      if (statusFilter) {
        list = list.filter((t) => t.taskStatus === statusFilter)
      }
      if (priorityFilter) {
        list = list.filter((t) => t.taskPriority === priorityFilter)
      }
    }

    return list
  }, [activeTab, page.content, unpaginatedTasks, search, statusFilter, priorityFilter, sortBy, sortDir])

  const statuses = [...new Set((activeTab === 'ALL' ? page.content : unpaginatedTasks).map((t) => t.taskStatus).filter(Boolean))]
  const priorities = [...new Set((activeTab === 'ALL' ? page.content : unpaginatedTasks).map((t) => t.taskPriority).filter(Boolean))]

  const handleTabChange = (nextTab) => {
    setActiveTab(nextTab)
    setPage((current) => ({ ...current, pageNo: 0 }))
  }

  const openTask = async (mode, taskId) => {
    setModal({ loading: true, mode })
    try {
      setModal({ mode, task: extract(await pmService.getTaskById(taskId)) })
    } catch (requestError) {
      setModal(null)
      toast.error(getApiError(requestError).message)
    }
  }

  const saveTask = async (payload, setFieldErrors) => {
    setIsSaving(true)
    try {
      const response = modal?.task
        ? await pmService.updateTask(modal.task.id, payload)
        : await pmService.createTask(projectId, payload)
      extract(response)
      toast.success(response.message || 'Task saved successfully.')
      setModal(null)
      loadTasks()
    } catch (requestError) {
      const apiError = getApiError(requestError)
      setFieldErrors(apiError.fields || {})
      toast.error(apiError.message)
    } finally {
      setIsSaving(false)
    }
  }

  const action = async (request) => {
    setIsSaving(true)
    try {
      const response = await request()
      extract(response)
      toast.success(response.message || 'Action completed successfully.')
      setModal(null)
      loadTasks()
    } catch (requestError) {
      toast.error(getApiError(requestError).message)
    } finally {
      setIsSaving(false)
    }
  }

  const deleteTask = async () => {
    if (!pendingDelete) return
    setIsDeleting(true)
    try {
      const response = await pmService.deleteTask(pendingDelete.id)
      extract(response)
      toast.success(response.message || 'Task deleted successfully.')
      setPendingDelete(null)
      loadTasks()
    } catch (requestError) {
      toast.error(getApiError(requestError).message)
    } finally {
      setIsDeleting(false)
    }
  }

  const columns = [
    { key: 'title', header: 'Task Title' },
    { key: 'taskStatus', header: 'Status', render: (task) => <StatusBadge value={task.taskStatus} /> },
    { key: 'taskPriority', header: 'Priority', render: (task) => <PriorityBadge value={task.taskPriority} /> },
    {
      key: 'dueDate',
      header: 'Due Date',
      render: (task) => (task.dueDate ? new Date(task.dueDate).toLocaleDateString() : 'N/A'),
    },
    {
      key: 'assignedUserName',
      header: 'Assignee',
      render: getAssigneeLabel,
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (task) => (
        <div className={styles.actions}>
          <Button variant="secondary" onClick={() => openTask('details', task.id)}>
            View
          </Button>
          <Button variant="secondary" onClick={() => openTask('edit', task.id)}>
            Edit
          </Button>
          <Button variant="ghost" onClick={() => setModal({ mode: 'assign', task })}>
            Assign
          </Button>
          <Button variant="ghost" onClick={() => setModal({ mode: 'due-date', task })}>
            Due Date
          </Button>
          <Button variant="ghost" onClick={() => setModal({ mode: 'status', task })}>
            Status
          </Button>
          <Button variant="ghost" onClick={() => setModal({ mode: 'priority', task })}>
            Priority
          </Button>
          <Button
            variant="ghost"
            className={styles.deleteBtn}
            onClick={() => setPendingDelete(task)}
          >
            Delete
          </Button>
        </div>
      ),
    },
  ]

  // Legacy action builder retained only for compatibility with historical modal state.
  // eslint-disable-next-line no-unused-vars
  const simpleActionForm = (field, label, handler, guidanceText) => (
    <FormLayout
      onSubmit={(event) => {
        event.preventDefault()
        const value = new FormData(event.currentTarget).get(field)
        if (!/^\d+$/.test(value) || Number(value) < 1) {
          toast.error(`Enter a valid numeric ${label.toLowerCase()}.`)
          return
        }
        handler(Number(value))
      }}
    >
      {guidanceText && (
        <div style={{ marginBottom: '0.75rem', padding: '0.75rem', borderRadius: '6px', background: 'var(--color-surface-muted)', border: '1px solid var(--color-border-subtle)', fontSize: '0.8125rem', color: 'var(--color-text-secondary)' }}>
          {guidanceText}
        </div>
      )}

      <Input
        id={field}
        name={field}
        type="number"
        min="1"
        label={label}
        placeholder="e.g. 1"
      />
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
        <Button type="submit" isLoading={isSaving} disabled={isSaving}>
          Save Action
        </Button>
      </div>
    </FormLayout>
  )

  const dueDateForm = () => (
    <FormLayout
      onSubmit={(event) => {
        event.preventDefault()
        const dueDateVal = new FormData(event.currentTarget).get('dueDate')
        if (!dueDateVal) {
          toast.error('Please select a valid due date.')
          return
        }
        if (modal.task?.startDate && String(dueDateVal) < String(modal.task.startDate)) {
          toast.error('Due date cannot be before start date.')
          return
        }
        action(() => pmService.setTaskDueDate(modal.task.id, { dueDate: String(dueDateVal) }))
      }}
    >
      <div style={{ marginBottom: '0.75rem', padding: '0.75rem', borderRadius: '6px', background: 'var(--color-surface-muted)', border: '1px solid var(--color-border-subtle)', fontSize: '0.8125rem', color: 'var(--color-text-secondary)' }}>
        Current Due Date: <strong>{modal.task?.dueDate || 'Not set'}</strong>
      </div>

      <Input
        id="dueDate"
        name="dueDate"
        type="date"
        label="Target Due Date"
        defaultValue={modal.task?.dueDate || ''}
      />
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
        <Button type="submit" isLoading={isSaving} disabled={isSaving}>
          Save Due Date
        </Button>
      </div>
    </FormLayout>
  )

  return (
    <>
      <PageHeader
        title="Task Workspace"
        description="Manage project tasks, status filters, high priority items, due dates, and completion metrics."
        actions={
          <Button disabled={!/^\d+$/.test(projectId)} onClick={() => setModal({ mode: 'create' })}>
            + Create Task
          </Button>
        }
      />

      <div className={styles.projectSelectorCard}>
        <div style={{ display: 'grid', gap: '1rem', alignItems: 'end' }}>
          <div className={styles.control}>
            <label htmlFor="assignedProjectSelect">Select Active Workspace / Project</label>
            <select
              id="assignedProjectSelect"
              value={projectId}
              onChange={(event) => {
                setProjectId(event.target.value)
                setPage(initialPage)
              }}
            >
              <option value="">Select an assigned project...</option>
              {assignedProjects.map((proj) => (
                <option key={proj.id} value={proj.id}>
                  {proj.projectName}{proj.projectCode ? ` (${proj.projectCode})` : ''}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      <div className={styles.tabs} role="tablist">
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'ALL'}
          className={`${styles.tabButton} ${activeTab === 'ALL' ? styles.activeTab : ''}`}
          onClick={() => handleTabChange('ALL')}
        >
          All Tasks
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'PENDING'}
          className={`${styles.tabButton} ${activeTab === 'PENDING' ? styles.activeTab : ''}`}
          onClick={() => handleTabChange('PENDING')}
        >
          Pending
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'IN_PROGRESS'}
          className={`${styles.tabButton} ${activeTab === 'IN_PROGRESS' ? styles.activeTab : ''}`}
          onClick={() => handleTabChange('IN_PROGRESS')}
        >
          In Progress
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'COMPLETED'}
          className={`${styles.tabButton} ${activeTab === 'COMPLETED' ? styles.activeTab : ''}`}
          onClick={() => handleTabChange('COMPLETED')}
        >
          Completed
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'OVERDUE'}
          className={`${styles.tabButton} ${activeTab === 'OVERDUE' ? styles.activeTab : ''}`}
          onClick={() => handleTabChange('OVERDUE')}
        >
          Overdue
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'HIGH_PRIORITY'}
          className={`${styles.tabButton} ${activeTab === 'HIGH_PRIORITY' ? styles.activeTab : ''}`}
          onClick={() => handleTabChange('HIGH_PRIORITY')}
        >
          High Priority
        </button>
      </div>

      {projectId && completionPercentage !== null && completionPercentage !== undefined && (
        <div className={styles.metricsBanner}>
          <div>
            <p className={styles.metricTitle}>Project Task Completion Progress</p>
            <p className={styles.metricVal}>
              {typeof completionPercentage === 'number'
                ? completionPercentage.toFixed(1)
                : completionPercentage}%
            </p>
          </div>
          <div className={styles.progressTrack}>
            <div
              className={styles.progressFill}
              style={{
                width: `${Math.min(
                  100,
                  Math.max(
                    0,
                    typeof completionPercentage === 'number'
                      ? completionPercentage
                      : parseFloat(completionPercentage) || 0,
                  ),
                )}%`,
              }}
            />
          </div>
        </div>
      )}

      <section className={styles.toolbar}>
        <SearchBar
          value={search}
          onChange={(event) => {
            setSearch(event.target.value)
            setPage((current) => ({ ...current, pageNo: 0 }))
          }}
          placeholder="Search task title or description..."
        />

        <div className={styles.control}>
          <label htmlFor="statusFilter">Status Filter</label>
          <select
            id="statusFilter"
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value)}
          >
            <option value="">All Statuses</option>
            {statuses.map((status) => (
              <option key={status}>{status}</option>
            ))}
          </select>
        </div>

        <div className={styles.control}>
          <label htmlFor="priorityFilter">Priority Filter</label>
          <select
            id="priorityFilter"
            value={priorityFilter}
            onChange={(event) => setPriorityFilter(event.target.value)}
          >
            <option value="">All Priorities</option>
            {priorities.map((priority) => (
              <option key={priority}>{priority}</option>
            ))}
          </select>
        </div>

        <div className={styles.control}>
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
            <option value="title">Title</option>
            <option value="dueDate">Due Date</option>
          </select>
        </div>

        <div className={styles.control}>
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

      {!projectId && (
        <EmptyState
          title={projectsError ? 'Project Workspaces Unavailable' : 'Select a Project Workspace'}
          description={projectsError || 'Choose an assigned project from the selector above to inspect its tasks.'}
        />
      )}

      {isLoading && (
        <div className={styles.loading}>
          <Loader label="Loading workspace task records" />
        </div>
      )}

      {error && !isLoading && (
        <EmptyState
          title="Tasks Unavailable"
          description={error}
          action={<Button onClick={loadTasks}>Try again</Button>}
        />
      )}

      {projectId && !isLoading && !error && (
        <DataTable
          columns={columns}
          data={visibleTasks}
          emptyMessage="No workspace tasks match your selected query or filters."
          pagination={
            activeTab === 'ALL'
              ? {
                  page: page.pageNo + 1,
                  totalPages: page.totalPages,
                  onPageChange: (nextPage) =>
                    setPage((current) => ({ ...current, pageNo: nextPage - 1 })),
                }
              : null
          }
        />
      )}

      {modal && (
        <Modal
          isOpen
          title={
            modal.loading
              ? 'Loading Task'
              : modal.mode === 'create'
                ? 'Create Task'
                : modal.mode === 'edit'
                  ? 'Edit Task'
                  : modal.mode === 'details'
                    ? 'Task Details'
                    : modal.mode === 'due-date'
                      ? 'Set Task Due Date'
                      : `Change Task ${modal.mode}`
          }
          onClose={() => !isSaving && setModal(null)}
        >
          {modal.loading ? (
            <div className={styles.loading}>
              <Loader label="Fetching task details" />
            </div>
          ) : modal.mode === 'details' ? (
            <TaskDetails task={modal.task} />
          ) : modal.mode === 'edit' || modal.mode === 'create' ? (
            <TaskForm task={modal.task} projectId={projectId} isSaving={isSaving} onSubmit={saveTask} />
          ) : modal.mode === 'due-date' ? (
            dueDateForm()
          ) : modal.mode === 'assign' ? (
            <TaskActionSelect label="Assignee" projectId={projectId} memberOptions isSaving={isSaving} onSubmit={(value) => action(() => pmService.assignTask(modal.task.id, { assignedUserId: value }))} />
          ) : modal.mode === 'status' ? (
            <TaskActionSelect label="Status" projectId={projectId} loadOptions={lookupService.getTaskStatuses} isSaving={isSaving} onSubmit={(value) => action(() => pmService.changeTaskStatus(modal.task.id, { taskStatusId: value }))} />
          ) : (
            <TaskActionSelect label="Priority" projectId={projectId} loadOptions={lookupService.getTaskPriorities} isSaving={isSaving} onSubmit={(value) => action(() => pmService.changeTaskPriority(modal.task.id, { taskPriorityId: value }))} />
          )}
        </Modal>
      )}

      <ConfirmDialog
        isOpen={Boolean(pendingDelete)}
        onClose={() => !isDeleting && setPendingDelete(null)}
        onConfirm={deleteTask}
        title="Delete Task"
        description={`Are you sure you want to delete "${pendingDelete?.title || 'this task'}"? This action cannot be undone.`}
        confirmLabel="Delete Task"
        isLoading={isDeleting}
      />
    </>
  )
}

export default PmTasksPage
