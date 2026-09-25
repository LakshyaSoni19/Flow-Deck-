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
import UserDetails from '../../components/users/UserDetails'
import UserForm from '../../components/users/UserForm'
import { adminService } from '../../services/adminService'
import { getApiError } from '../../utils/apiError'
import styles from './UsersPage.module.css'

const initialPage = { content: [], pageNo: 0, pageSize: 10, totalPages: 0 }
const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}
const userName = (user) =>
  `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email || 'User'

function UsersPage() {
  const [page, setPage] = useState(initialPage)
  const [search, setSearch] = useState('')
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')
  const [isLoading, setIsLoading] = useState(true)
  const [requestError, setRequestError] = useState('')
  const [modal, setModal] = useState(null)
  const [isSaving, setIsSaving] = useState(false)
  const [confirmation, setConfirmation] = useState(null)
  const [isChangingState, setIsChangingState] = useState(false)

  const loadUsers = useCallback(async () => {
    setIsLoading(true)
    setRequestError('')
    const params = { pageNo: page.pageNo, pageSize: page.pageSize, sortBy, sortDir }
    try {
      const response = search.trim()
        ? await adminService.searchUsers({ ...params, query: search.trim() })
        : await adminService.getUsers(params)
      setPage((current) => ({ ...current, ...extractData(response) }))
    } catch (error) {
      const apiError = getApiError(error)
      setRequestError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsLoading(false)
    }
  }, [page.pageNo, page.pageSize, search, sortBy, sortDir])

  useEffect(() => {
    void Promise.resolve().then(loadUsers)
  }, [loadUsers])

  const updateSearch = (event) => {
    setSearch(event.target.value)
    setPage((current) => ({ ...current, pageNo: 0 }))
  }

  const openUserModal = async (mode, userId) => {
    setModal({ loading: true, mode })
    try {
      setModal({ mode, user: extractData(await adminService.getUserById(userId)) })
    } catch (error) {
      setModal(null)
      toast.error(getApiError(error).message)
    }
  }

  const saveUser = async (payload, setFieldErrors) => {
    if (!modal?.user) return
    setIsSaving(true)
    try {
      const response = await adminService.updateUser(modal.user.id, payload)
      extractData(response)
      toast.success(response.message)
      setModal(null)
      loadUsers()
    } catch (error) {
      const apiError = getApiError(error)
      setFieldErrors(apiError.fields || {})
      toast.error(apiError.message)
    } finally {
      setIsSaving(false)
    }
  }

  const confirmUserAction = async () => {
    if (!confirmation) return
    setIsChangingState(true)
    try {
      const { user, action } = confirmation
      const response =
        action === 'activate'
          ? await adminService.activateUser(user.id)
          : action === 'deactivate'
            ? await adminService.deactivateUser(user.id)
            : await adminService.deleteUser(user.id)
      extractData(response)
      toast.success(response.message)
      setConfirmation(null)
      if (action === 'delete' && page.content.length === 1 && page.pageNo > 0) {
        setPage((current) => ({ ...current, pageNo: current.pageNo - 1 }))
      } else {
        loadUsers()
      }
    } catch (error) {
      toast.error(getApiError(error).message)
    } finally {
      setIsChangingState(false)
    }
  }

  const columns = [
    {
      key: 'name',
      header: 'User Profile',
      render: (user) => {
        const name = userName(user)
        const initial = name.charAt(0).toUpperCase() || 'U'
        return (
          <div className={styles.userCell}>
            {user.profileImage ? (
              <img
                src={user.profileImage}
                alt={name}
                className={styles.userAvatar}
                style={{ objectFit: 'cover' }}
              />
            ) : (
              <span className={styles.userAvatar}>{initial}</span>
            )}
            <span className={styles.userName}>{name}</span>
          </div>
        )
      },
    },
    { key: 'email', header: 'Email Address' },
    {
      key: 'roles',
      header: 'Role',
      render: (user) => (
        <span className={`${styles.badge} ${styles.badgeRole}`}>
          {user.roles?.join(', ') || 'Employee'}
        </span>
      ),
    },
    {
      key: 'departmentName',
      header: 'Department',
      render: (user) => user.departmentName || 'N/A',
    },
    {
      key: 'designationName',
      header: 'Designation',
      render: (user) => user.designationName || 'N/A',
    },
    {
      key: 'isActive',
      header: 'Status',
      render: (user) => (
        <span
          className={`${styles.badge} ${
            user.isActive ? styles.badgeActive : styles.badgeInactive
          }`}
        >
          {user.isActive ? 'Active' : 'Inactive'}
        </span>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (user) => (
        <div className={styles.actions}>
          <Button variant="secondary" onClick={() => openUserModal('details', user.id)}>
            View
          </Button>
          <Button variant="secondary" onClick={() => openUserModal('edit', user.id)}>
            Edit
          </Button>
          <Button
            variant="ghost"
            onClick={() =>
              setConfirmation({
                action: user.isActive ? 'deactivate' : 'activate',
                user,
              })
            }
          >
            {user.isActive ? 'Deactivate' : 'Activate'}
          </Button>
          <Button
            variant="ghost"
            className={styles.deleteBtn}
            onClick={() => setConfirmation({ action: 'delete', user })}
          >
            Delete
          </Button>
        </div>
      ),
    },
  ]

  const confirmationTitle =
    confirmation?.action === 'activate'
      ? 'Activate User Account'
      : confirmation?.action === 'deactivate'
        ? 'Deactivate User Account'
        : 'Delete User Account'

  const confirmationDescription =
    confirmation?.action === 'activate'
      ? `Are you sure you want to activate the account for ${userName(confirmation.user)}?`
      : confirmation?.action === 'deactivate'
        ? `Are you sure you want to deactivate the account for ${userName(confirmation.user)}? The user will be unable to log in.`
        : `Are you sure you want to soft-delete ${userName(confirmation?.user || {})}? This will mark the user account as inactive.`

  return (
    <>
      <PageHeader
        title="User Management"
        description="Inspect registered user profiles, update personal details, security roles, and account active status."
      />

      <section className={styles.toolbar}>
        <SearchBar
          value={search}
          onChange={updateSearch}
          placeholder="Search users by name or email..."
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
            <option value="firstName">First Name</option>
            <option value="email">Email Address</option>
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
          <Loader label="Loading user account records" />
        </div>
      )}

      {requestError && !isLoading && (
        <EmptyState
          title="Users Unavailable"
          description={requestError}
          action={<Button onClick={loadUsers}>Try again</Button>}
        />
      )}

      {!isLoading && !requestError && (
        <DataTable
          columns={columns}
          data={page.content || []}
          emptyMessage={
            search ? 'No registered users match your search query.' : 'No registered users are available.'
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
              ? 'Loading User Account'
              : modal.mode === 'edit'
                ? 'Edit User Profile'
                : 'User Details'
          }
          onClose={() => !isSaving && setModal(null)}
        >
          {modal.loading ? (
            <div className={styles.loading}>
              <Loader label="Fetching user details" />
            </div>
          ) : modal.mode === 'edit' ? (
            <UserForm user={modal.user} isSaving={isSaving} onSubmit={saveUser} />
          ) : (
            <UserDetails user={modal.user} />
          )}
        </Modal>
      )}

      <ConfirmDialog
        isOpen={Boolean(confirmation)}
        onClose={() => !isChangingState && setConfirmation(null)}
        onConfirm={confirmUserAction}
        title={confirmationTitle}
        description={confirmationDescription}
        confirmLabel={
          confirmation?.action === 'delete'
            ? 'Delete User'
            : confirmation?.action === 'activate'
              ? 'Activate User'
              : 'Deactivate User'
        }
        isLoading={isChangingState}
      />
    </>
  )
}

export default UsersPage
