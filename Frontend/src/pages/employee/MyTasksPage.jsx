import { useCallback, useEffect, useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import Card from '../../components/common/Card'
import DataTable from '../../components/common/DataTable'
import EmptyState from '../../components/common/EmptyState'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import PageHeader from '../../components/common/PageHeader'
import SearchBar from '../../components/common/SearchBar'
import TaskComments from '../../components/tasks/TaskComments'
import { PriorityBadge, StatusBadge } from '../../components/tasks/TaskBadges'
import { useAuth } from '../../hooks/useAuth'
import { employeeService } from '../../services/employeeService'
import { getApiError } from '../../utils/apiError'
import styles from './MyTasksPage.module.css'

const initialPage = { content: [], pageNo: 0, pageSize: 10, totalPages: 0 }
const ALLOWED_STATUSES = ['TO_DO', 'IN_PROGRESS', 'COMPLETED']

const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}

const display = (value) => (value !== null && value !== undefined && value !== '' ? value : 'Not available')

export default function MyTasksPage() {
  const { user } = useAuth()
  const [activeTab, setActiveTab] = useState('ALL')
  const [page, setPage] = useState(initialPage)
  const [unpaginatedList, setUnpaginatedList] = useState([])
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [priorityFilter, setPriorityFilter] = useState('')
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')

  const [isLoading, setIsLoading] = useState(true)
  const [requestError, setRequestError] = useState('')
  const [modal, setModal] = useState(null)
  const [isUpdatingStatus, setIsUpdatingStatus] = useState(false)
  const [selectedStatus, setSelectedStatus] = useState('')

  const loadTasks = useCallback(async () => {
    setIsLoading(true)
    setRequestError('')
    try {
      if (activeTab === 'ALL') {
        const response = await employeeService.getAssignedTasks({
          search,
          status: statusFilter,
          priority: priorityFilter,
          pageNo: page.pageNo,
          pageSize: page.pageSize,
          sortBy,
          sortDir,
        })
        const data = extractData(response)
        setPage((current) => ({ ...current, ...data }))
      } else {
        let response
        if (activeTab === 'PENDING') {
          response = await employeeService.getPendingTasks()
        } else if (activeTab === 'COMPLETED') {
          response = await employeeService.getCompletedTasks()
        } else if (activeTab === 'OVERDUE') {
          response = await employeeService.getOverdueTasks()
        }
        const data = extractData(response)
        setUnpaginatedList(Array.isArray(data) ? data : [])
      }
    } catch (error) {
      const apiError = getApiError(error)
      setRequestError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsLoading(false)
    }
  }, [activeTab, search, statusFilter, priorityFilter, page.pageNo, page.pageSize, sortBy, sortDir])

  useEffect(() => {
    void Promise.resolve().then(loadTasks)
  }, [loadTasks])

  const visibleTasks = useMemo(() => {
    if (activeTab === 'ALL') {
      return page.content || []
    }

    let filtered = [...unpaginatedList]

    if (search) {
      const term = search.toLowerCase()
      filtered = filtered.filter(
        (task) =>
          task.title?.toLowerCase().includes(term) ||
          task.projectName?.toLowerCase().includes(term) ||
          task.description?.toLowerCase().includes(term),
      )
    }

    if (statusFilter) {
      filtered = filtered.filter((task) => task.taskStatus === statusFilter)
    }

    if (priorityFilter) {
      filtered = filtered.filter((task) => task.taskPriority === priorityFilter)
    }

    filtered.sort((first, second) => {
      let valA = first[sortBy] ?? ''
      let valB = second[sortBy] ?? ''
      if (typeof valA === 'string') valA = valA.toLowerCase()
      if (typeof valB === 'string') valB = valB.toLowerCase()

      if (valA < valB) return sortDir === 'asc' ? -1 : 1
      if (valA > valB) return sortDir === 'asc' ? 1 : -1
      return 0
    })

    return filtered
  }, [activeTab, page.content, unpaginatedList, search, statusFilter, priorityFilter, sortBy, sortDir])

  const fetchCommentsForTask = async (taskId) => {
    setModal((prev) => (prev ? { ...prev, commentsLoading: true, commentsError: '' } : prev))
    try {
      const response = await employeeService.getTaskComments(taskId)
      const commentsData = extractData(response)
      const comments = Array.isArray(commentsData) ? commentsData : []
      setModal((prev) => (prev ? { ...prev, comments, commentsLoading: false, commentsError: '' } : prev))
    } catch (error) {
      const apiError = getApiError(error)
      setModal((prev) => (prev ? { ...prev, commentsLoading: false, commentsError: apiError.message } : prev))
    }
  }

  const openTaskDetails = async (taskId) => {
    setModal({ mode: 'details', loading: true })
    try {
      const [taskRes, commentsRes] = await Promise.all([
        employeeService.getTaskById(taskId),
        employeeService.getTaskComments(taskId).catch((err) => ({ error: err })),
      ])
      const task = extractData(taskRes)
      let comments = []
      let commentsError = ''
      if (commentsRes && !commentsRes.error) {
        try {
          const data = extractData(commentsRes)
          comments = Array.isArray(data) ? data : []
        } catch (err) {
          commentsError = getApiError(err).message
        }
      } else if (commentsRes?.error) {
        commentsError = getApiError(commentsRes.error).message
      }

      setModal({ mode: 'details', task, comments, commentsLoading: false, commentsError })
    } catch (error) {
      setModal(null)
      toast.error(getApiError(error).message)
    }
  }

  const openStatusModal = (task) => {
    setSelectedStatus(task.taskStatus || 'TO_DO')
    setModal({ mode: 'status', task })
  }

  const handleStatusUpdate = async (event) => {
    event.preventDefault()
    if (!modal?.task?.id) return
    if (!ALLOWED_STATUSES.includes(selectedStatus)) {
      toast.error('Invalid status selected. Must be TO_DO, IN_PROGRESS, or COMPLETED.')
      return
    }

    setIsUpdatingStatus(true)
    try {
      const response = await employeeService.updateTaskStatus(modal.task.id, {
        statusName: selectedStatus,
      })
      extractData(response)
      toast.success(response?.message || 'Task status updated successfully!')
      setModal(null)
      loadTasks()
    } catch (error) {
      const apiError = getApiError(error)
      toast.error(apiError.message)
    } finally {
      setIsUpdatingStatus(false)
    }
  }

  const handleAddComment = async (commentText) => {
    if (!modal?.task?.id) return
    try {
      const response = await employeeService.addTaskComment(modal.task.id, {
        comment: commentText,
      })
      extractData(response)
      toast.success(response?.message || 'Comment added successfully!')
      await fetchCommentsForTask(modal.task.id)
    } catch (error) {
      const apiError = getApiError(error)
      toast.error(apiError.message)
      throw error
    }
  }

  const handleUpdateComment = async (commentId, commentText) => {
    if (!modal?.task?.id) return
    try {
      const response = await employeeService.updateTaskComment(commentId, {
        comment: commentText,
      })
      extractData(response)
      toast.success(response?.message || 'Comment updated successfully!')
      await fetchCommentsForTask(modal.task.id)
    } catch (error) {
      const apiError = getApiError(error)
      toast.error(apiError.message)
      throw error
    }
  }

  const handleDeleteComment = async (commentId) => {
    if (!modal?.task?.id) return
    try {
      const response = await employeeService.deleteTaskComment(commentId)
      extractData(response)
      toast.success(response?.message || 'Comment deleted successfully!')
      await fetchCommentsForTask(modal.task.id)
    } catch (error) {
      const apiError = getApiError(error)
      toast.error(apiError.message)
      throw error
    }
  }

  const columns = [
    { key: 'title', header: 'Task Title' },
    { key: 'projectName', header: 'Project', render: (task) => display(task.projectName) },
    {
      key: 'taskStatus',
      header: 'Status',
      render: (task) => <StatusBadge value={task.taskStatus} />,
    },
    {
      key: 'taskPriority',
      header: 'Priority',
      render: (task) => <PriorityBadge value={task.taskPriority} />,
    },
    {
      key: 'dueDate',
      header: 'Due Date',
      render: (task) => (task.dueDate ? new Date(task.dueDate).toLocaleDateString() : 'N/A'),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (task) => (
        <div className={styles.actions}>
          <Button onClick={() => openTaskDetails(task.id)}>
            View Details
          </Button>
          <Button variant="ghost" onClick={() => openStatusModal(task)}>
            Update Status
          </Button>
        </div>
      ),
    },
  ]

  const handleTabChange = (nextTab) => {
    setActiveTab(nextTab)
    setPage((current) => ({ ...current, pageNo: 0 }))
  }

  return (
    <>
      <PageHeader
        title="My Assigned Tasks"
        description="View assigned tasks, track execution progress, update statuses, and collaborate on task discussions."
      />

      <div className={styles.tabs} role="tablist">
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'ALL'}
          className={`${styles.tabButton} ${activeTab === 'ALL' ? styles.activeTab : ''}`}
          onClick={() => handleTabChange('ALL')}
        >
          All Assigned
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
      </div>

      <section className={styles.toolbar}>
        <div className={styles.searchWrapper}>
          <SearchBar
            value={search}
            onChange={(event) => {
              setSearch(event.target.value)
              setPage((current) => ({ ...current, pageNo: 0 }))
            }}
            placeholder="Search task title or description..."
          />
        </div>
        <div className={styles.control}>
          <label htmlFor="statusFilter">Status Filter</label>
          <select
            id="statusFilter"
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value)}
          >
            <option value="">All Statuses</option>
            <option value="TO_DO">To Do</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
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
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="URGENT">Urgent</option>
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
            <option value="taskPriority">Priority</option>
            <option value="taskStatus">Status</option>
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

      {isLoading && (
        <div className={styles.loading}>
          <Loader label="Loading assigned tasks" />
        </div>
      )}

      {requestError && !isLoading && (
        <EmptyState
          title="Assigned Tasks Unavailable"
          description={requestError}
          action={<Button onClick={loadTasks}>Try again</Button>}
        />
      )}

      {!isLoading && !requestError && (
        <DataTable
          columns={columns}
          data={visibleTasks}
          emptyMessage={
            search || statusFilter || priorityFilter
              ? 'No assigned tasks match your filter criteria.'
              : 'No assigned tasks currently found.'
          }
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
              ? 'Loading Task Details'
              : modal.mode === 'details'
                ? 'Task Details & Discussion'
                : 'Update Task Status'
          }
          onClose={() => !isUpdatingStatus && setModal(null)}
        >
          {modal.loading ? (
            <div className={styles.loading}>
              <Loader label="Fetching task information" />
            </div>
          ) : modal.mode === 'details' ? (
            <div className={styles.taskDetailsLayout}>
              <Card>
                <div style={{ marginBottom: '1rem', paddingBottom: '0.75rem', borderBottom: '1px solid var(--color-border)' }}>
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem', flexWrap: 'wrap' }}>
                    <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 800 }}>
                      {modal.task?.title || 'Untitled Task'}
                    </h3>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <StatusBadge value={modal.task?.taskStatus} />
                      <PriorityBadge value={modal.task?.taskPriority} />
                    </div>
                  </div>
                </div>

                <dl className={styles.detailsGrid}>
                  <div>
                    <dt>Assigned Project</dt>
                    <dd>{display(modal.task?.projectName)}</dd>
                  </div>
                  <div>
                    <dt>Target Due Date</dt>
                    <dd>{modal.task?.dueDate ? new Date(modal.task.dueDate).toLocaleDateString() : 'N/A'}</dd>
                  </div>
                  <div>
                    <dt>Start Date</dt>
                    <dd>{modal.task?.startDate ? new Date(modal.task.startDate).toLocaleDateString() : 'N/A'}</dd>
                  </div>
                  <div>
                    <dt>Estimated Hours</dt>
                    <dd>{modal.task?.estimatedHours ? `${modal.task.estimatedHours} hrs` : 'N/A'}</dd>
                  </div>
                  {modal.task?.actualHours && (
                    <div>
                      <dt>Actual Hours Spent</dt>
                      <dd>{modal.task.actualHours} hrs</dd>
                    </div>
                  )}
                  <div style={{ gridColumn: '1 / -1' }}>
                    <dt>Task Description</dt>
                    <dd style={{ lineHeight: 1.5 }}>{display(modal.task?.description)}</dd>
                  </div>
                </dl>

                <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1.25rem', paddingTop: '0.75rem', borderTop: '1px solid var(--color-border-subtle)' }}>
                  <Button variant="secondary" onClick={() => openStatusModal(modal.task)}>
                    Update Task Status
                  </Button>
                </div>
              </Card>

              <TaskComments
                taskId={modal.task?.id}
                comments={modal.comments || []}
                isLoading={modal.commentsLoading}
                error={modal.commentsError}
                currentUser={user}
                onRefresh={() => fetchCommentsForTask(modal.task?.id)}
                onAddComment={handleAddComment}
                onUpdateComment={handleUpdateComment}
                onDeleteComment={handleDeleteComment}
              />
            </div>
          ) : (
            <form onSubmit={handleStatusUpdate} className={styles.statusForm}>
              <p style={{ margin: 0, fontSize: '0.875rem', color: 'var(--color-text-secondary)' }}>
                Select new workflow status for <strong>{modal.task?.title}</strong>:
              </p>
              <div className={styles.radioGroup}>
                <label
                  className={`${styles.radioOption} ${selectedStatus === 'TO_DO' ? styles.radioOptionSelected : ''}`}
                >
                  <input
                    type="radio"
                    name="taskStatus"
                    value="TO_DO"
                    checked={selectedStatus === 'TO_DO'}
                    onChange={(event) => setSelectedStatus(event.target.value)}
                  />
                  <div>
                    <div style={{ fontWeight: 700 }}>To Do</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>Task is queued for work.</div>
                  </div>
                </label>
                <label
                  className={`${styles.radioOption} ${selectedStatus === 'IN_PROGRESS' ? styles.radioOptionSelected : ''}`}
                >
                  <input
                    type="radio"
                    name="taskStatus"
                    value="IN_PROGRESS"
                    checked={selectedStatus === 'IN_PROGRESS'}
                    onChange={(event) => setSelectedStatus(event.target.value)}
                  />
                  <div>
                    <div style={{ fontWeight: 700, color: '#0f766e' }}>In Progress</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>Currently working on deliverables.</div>
                  </div>
                </label>
                <label
                  className={`${styles.radioOption} ${selectedStatus === 'COMPLETED' ? styles.radioOptionSelected : ''}`}
                >
                  <input
                    type="radio"
                    name="taskStatus"
                    value="COMPLETED"
                    checked={selectedStatus === 'COMPLETED'}
                    onChange={(event) => setSelectedStatus(event.target.value)}
                  />
                  <div>
                    <div style={{ fontWeight: 700, color: '#16a34a' }}>Completed</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>Deliverables finished and verified.</div>
                  </div>
                </label>
              </div>

              <div className={styles.formActions}>
                <Button
                  type="button"
                  variant="ghost"
                  disabled={isUpdatingStatus}
                  onClick={() => setModal(null)}
                >
                  Cancel
                </Button>
                <Button type="submit" isLoading={isUpdatingStatus} disabled={isUpdatingStatus}>
                  Update Status
                </Button>
              </div>
            </form>
          )}
        </Modal>
      )}
    </>
  )
}
