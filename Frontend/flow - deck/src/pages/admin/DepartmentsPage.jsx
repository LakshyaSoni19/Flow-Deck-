import { useCallback, useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import ConfirmDialog from '../../components/common/ConfirmDialog'
import DataTable from '../../components/common/DataTable'
import EmptyState from '../../components/common/EmptyState'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import PageHeader from '../../components/common/PageHeader'
import SearchBar from '../../components/common/SearchBar'
import DepartmentForm from '../../components/departments/DepartmentForm'
import { adminService } from '../../services/adminService'
import { getApiError } from '../../utils/apiError'
import styles from './DepartmentsPage.module.css'

const initialPage = { content: [], pageNo: 0, pageSize: 10, totalPages: 0 }
const isSuccessful = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}

function DepartmentsPage() {
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

  const loadDepartments = useCallback(async () => {
    setIsLoading(true)
    setRequestError('')
    const params = { pageNo: page.pageNo, pageSize: page.pageSize, sortBy, sortDir }
    try {
      const response = search.trim()
        ? await adminService.searchDepartments({ ...params, query: search.trim() })
        : await adminService.getDepartments(params)
      setPage((current) => ({ ...current, ...isSuccessful(response) }))
    } catch (error) {
      const apiError = getApiError(error)
      setRequestError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsLoading(false)
    }
  }, [page.pageNo, page.pageSize, search, sortBy, sortDir])

  useEffect(() => {
    void Promise.resolve().then(loadDepartments)
  }, [loadDepartments])

  const updateSearch = (event) => {
    setSearch(event.target.value)
    setPage((current) => ({ ...current, pageNo: 0 }))
  }

  const editDepartment = async (departmentId) => {
    setModal({ loading: true })
    try {
      const detail = isSuccessful(await adminService.getDepartmentById(departmentId))
      setModal({ department: detail })
    } catch (error) {
      const apiError = getApiError(error)
      setModal(null)
      toast.error(apiError.message)
    }
  }

  const saveDepartment = async (payload, setFormError) => {
    setIsSaving(true)
    try {
      const response = modal?.department
        ? await adminService.updateDepartment(modal.department.id, payload)
        : await adminService.createDepartment(payload)
      isSuccessful(response)
      toast.success(response.message)
      setModal(null)
      loadDepartments()
    } catch (error) {
      const apiError = getApiError(error)
      setFormError(apiError.fields?.name || apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsSaving(false)
    }
  }

  const deleteDepartment = async () => {
    if (!pendingDelete) return
    setIsDeleting(true)
    try {
      const response = await adminService.deleteDepartment(pendingDelete.id)
      isSuccessful(response)
      toast.success(response.message)
      setPendingDelete(null)
      if (page.content.length === 1 && page.pageNo > 0) {
        setPage((current) => ({ ...current, pageNo: current.pageNo - 1 }))
      } else {
        loadDepartments()
      }
    } catch (error) {
      const apiError = getApiError(error)
      toast.error(apiError.message)
    } finally {
      setIsDeleting(false)
    }
  }

  const columns = [
    { key: 'name', header: 'Department Name' },
    {
      key: 'createdAt',
      header: 'Created Date',
      render: (dept) => (dept.createdAt ? new Date(dept.createdAt).toLocaleDateString() : 'N/A'),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (department) => (
        <div className={styles.actions}>
          <Button variant="secondary" onClick={() => editDepartment(department.id)}>
            Edit
          </Button>
          <Button
            variant="ghost"
            className={styles.deleteBtn}
            onClick={() => setPendingDelete(department)}
          >
            Delete
          </Button>
        </div>
      ),
    },
  ]

  return (
    <>
      <PageHeader
        title="Departments"
        description="Configure and manage organizational department structures across the enterprise."
        actions={
          <Button onClick={() => setModal({ department: null })}>
            + Add Department
          </Button>
        }
      />

      <section className={styles.toolbar}>
        <SearchBar
          value={search}
          onChange={updateSearch}
          placeholder="Search departments by name..."
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
            <option value="name">Department Name</option>
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
          <Loader label="Loading department records" />
        </div>
      )}

      {requestError && !isLoading && (
        <EmptyState
          title="Departments Unavailable"
          description={requestError}
          action={<Button onClick={loadDepartments}>Try again</Button>}
        />
      )}

      {!isLoading && !requestError && (
        <DataTable
          columns={columns}
          data={page.content || []}
          emptyMessage={
            search ? 'No departments match your search query.' : 'No departments have been created yet.'
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
          title={
            modal.loading
              ? 'Loading Department'
              : modal.department
                ? 'Edit Department'
                : 'Add Department'
          }
          onClose={() => !isSaving && setModal(null)}
        >
          {modal.loading ? (
            <div className={styles.loading}>
              <Loader label="Fetching department details" />
            </div>
          ) : (
            <DepartmentForm
              department={modal.department}
              isSaving={isSaving}
              onSubmit={saveDepartment}
            />
          )}
        </Modal>
      )}

      <ConfirmDialog
        isOpen={Boolean(pendingDelete)}
        onClose={() => !isDeleting && setPendingDelete(null)}
        onConfirm={deleteDepartment}
        title="Delete Department"
        description={`Are you sure you want to delete "${pendingDelete?.name || 'this department'}"? This action cannot be undone.`}
        confirmLabel="Delete Department"
        isLoading={isDeleting}
      />
    </>
  )
}

export default DepartmentsPage
