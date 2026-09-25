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
import AssignRoleForm from '../../components/roles/AssignRoleForm'
import RoleForm from '../../components/roles/RoleForm'
import { adminService } from '../../services/adminService'
import { getApiError } from '../../utils/apiError'
import styles from './RolesPage.module.css'

const PAGE_SIZE = 10
const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return Array.isArray(response.data) ? response.data : response.data?.content || []
}

function RolesPage() {
  const [roles, setRoles] = useState([])
  const [search, setSearch] = useState('')
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')
  const [page, setPage] = useState(1)
  const [isLoading, setIsLoading] = useState(true)
  const [requestError, setRequestError] = useState('')
  const [modal, setModal] = useState(null)
  const [isSaving, setIsSaving] = useState(false)
  const [pendingDelete, setPendingDelete] = useState(null)
  const [isDeleting, setIsDeleting] = useState(false)

  const loadRoles = useCallback(async () => {
    setIsLoading(true)
    setRequestError('')
    try {
      setRoles(extractData(await adminService.getRoles()))
    } catch (error) {
      const apiError = getApiError(error)
      setRequestError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    void Promise.resolve().then(loadRoles)
  }, [loadRoles])

  const visibleRoles = useMemo(() => {
    const filtered = roles.filter((role) =>
      role.name?.toLowerCase().includes(search.trim().toLowerCase()),
    )
    return [...filtered].sort((left, right) => {
      const leftValue = String(left[sortBy] ?? '')
      const rightValue = String(right[sortBy] ?? '')
      const result = leftValue.localeCompare(rightValue, undefined, { numeric: true })
      return sortDir === 'asc' ? result : -result
    })
  }, [roles, search, sortBy, sortDir])

  const totalPages = Math.ceil(visibleRoles.length / PAGE_SIZE)
  const pageData = visibleRoles.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE)

  const updateSearch = (event) => {
    setSearch(event.target.value)
    setPage(1)
  }

  const saveRole = async (payload, setFormError) => {
    setIsSaving(true)
    try {
      const response = modal?.role
        ? await adminService.updateRole(modal.role.id, payload)
        : await adminService.createRole(payload)
      if (!response?.success) {
        throw new Error(response?.message || 'The request could not be completed.')
      }
      toast.success(response.message)
      setModal(null)
      loadRoles()
    } catch (error) {
      const apiError = getApiError(error)
      setFormError(apiError.fields?.name || apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsSaving(false)
    }
  }

  const assignRole = async (payload, setFieldErrors) => {
    setIsSaving(true)
    try {
      const response = await adminService.assignRole(payload)
      if (!response?.success) {
        throw new Error(response?.message || 'The request could not be completed.')
      }
      toast.success(response.message)
      setModal(null)
    } catch (error) {
      const apiError = getApiError(error)
      setFieldErrors(apiError.fields || {})
      toast.error(apiError.message)
    } finally {
      setIsSaving(false)
    }
  }

  const deleteRole = async () => {
    if (!pendingDelete) return
    setIsDeleting(true)
    try {
      const response = await adminService.deleteRole(pendingDelete.id)
      if (!response?.success) {
        throw new Error(response?.message || 'The request could not be completed.')
      }
      toast.success(response.message)
      setPendingDelete(null)
      loadRoles()
    } catch (error) {
      toast.error(getApiError(error).message)
    } finally {
      setIsDeleting(false)
    }
  }

  const columns = [
    {
      key: 'name',
      header: 'Role Name',
      render: (role) => <span className={styles.roleBadge}>{role.name}</span>,
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (role) => (
        <div className={styles.actions}>
          <Button variant="secondary" onClick={() => setModal({ role })}>
            Edit
          </Button>
          <Button
            variant="ghost"
            className={styles.deleteBtn}
            onClick={() => setPendingDelete(role)}
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
        title="Role Management"
        description="Define security access roles, configure permission hierarchies, and assign roles to user accounts."
        actions={
          <div className={styles.actions}>
            <Button variant="secondary" onClick={() => setModal({ assign: true })}>
              Assign Role to User
            </Button>
            <Button onClick={() => setModal({ role: null })}>
              + Add Role
            </Button>
          </div>
        }
      />

      <section className={styles.toolbar}>
        <SearchBar
          value={search}
          onChange={updateSearch}
          placeholder="Search security roles..."
        />
        <div className={styles.sortControl}>
          <label htmlFor="sortBy">Sort By</label>
          <select
            id="sortBy"
            value={sortBy}
            onChange={(event) => {
              setSortBy(event.target.value)
              setPage(1)
            }}
          >
            <option value="id">ID</option>
            <option value="name">Role Name</option>
          </select>
        </div>
        <div className={styles.sortControl}>
          <label htmlFor="sortDir">Direction</label>
          <select
            id="sortDir"
            value={sortDir}
            onChange={(event) => {
              setSortDir(event.target.value)
              setPage(1)
            }}
          >
            <option value="asc">Ascending (A-Z)</option>
            <option value="desc">Descending (Z-A)</option>
          </select>
        </div>
      </section>

      {isLoading && (
        <div className={styles.loading}>
          <Loader label="Loading security role records" />
        </div>
      )}

      {requestError && !isLoading && (
        <EmptyState
          title="Roles Unavailable"
          description={requestError}
          action={<Button onClick={loadRoles}>Try again</Button>}
        />
      )}

      {!isLoading && !requestError && (
        <DataTable
          columns={columns}
          data={pageData}
          emptyMessage={
            search ? 'No roles match your search query.' : 'No security roles have been created yet.'
          }
          pagination={{ page, totalPages, onPageChange: setPage }}
        />
      )}

      {modal && (
        <Modal
          isOpen
          title={
            modal.assign
              ? 'Assign Role to User'
              : modal.role
                ? 'Edit Role'
                : 'Add Role'
          }
          onClose={() => !isSaving && setModal(null)}
        >
          {modal.assign ? (
            <AssignRoleForm isSaving={isSaving} onSubmit={assignRole} />
          ) : (
            <RoleForm role={modal.role} isSaving={isSaving} onSubmit={saveRole} />
          )}
        </Modal>
      )}

      <ConfirmDialog
        isOpen={Boolean(pendingDelete)}
        onClose={() => !isDeleting && setPendingDelete(null)}
        onConfirm={deleteRole}
        title="Delete Role"
        description={`Are you sure you want to delete "${pendingDelete?.name || 'this role'}"? Users assigned to this role may lose access privileges.`}
        confirmLabel="Delete Role"
        isLoading={isDeleting}
      />
    </>
  )
}

export default RolesPage
