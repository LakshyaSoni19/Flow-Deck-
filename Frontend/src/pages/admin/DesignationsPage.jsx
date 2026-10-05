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
import DesignationForm from '../../components/designations/DesignationForm'
import { adminService } from '../../services/adminService'
import { getApiError } from '../../utils/apiError'
import styles from './DesignationsPage.module.css'

const initialPage = { content: [], pageNo: 0, pageSize: 10, totalPages: 0 }
const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}

function DesignationsPage() {
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

  const loadDesignations = useCallback(async () => {
    setIsLoading(true)
    setRequestError('')
    const params = { pageNo: page.pageNo, pageSize: page.pageSize, sortBy, sortDir }
    try {
      const response = search.trim()
        ? await adminService.searchDesignations({ ...params, query: search.trim() })
        : await adminService.getDesignations(params)
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
    void Promise.resolve().then(loadDesignations)
  }, [loadDesignations])

  const updateSearch = (event) => {
    setSearch(event.target.value)
    setPage((current) => ({ ...current, pageNo: 0 }))
  }

  const editDesignation = async (designationId) => {
    setModal({ loading: true })
    try {
      setModal({ designation: extractData(await adminService.getDesignationById(designationId)) })
    } catch (error) {
      setModal(null)
      toast.error(getApiError(error).message)
    }
  }

  const saveDesignation = async (payload, setFormError) => {
    setIsSaving(true)
    try {
      const response = modal?.designation
        ? await adminService.updateDesignation(modal.designation.id, payload)
        : await adminService.createDesignation(payload)
      extractData(response)
      toast.success(response.message)
      setModal(null)
      loadDesignations()
    } catch (error) {
      const apiError = getApiError(error)
      setFormError(apiError.fields?.name || apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsSaving(false)
    }
  }

  const deleteDesignation = async () => {
    if (!pendingDelete) return
    setIsDeleting(true)
    try {
      const response = await adminService.deleteDesignation(pendingDelete.id)
      extractData(response)
      toast.success(response.message)
      setPendingDelete(null)
      if (page.content.length === 1 && page.pageNo > 0) {
        setPage((current) => ({ ...current, pageNo: current.pageNo - 1 }))
      } else {
        loadDesignations()
      }
    } catch (error) {
      toast.error(getApiError(error).message)
    } finally {
      setIsDeleting(false)
    }
  }

  const columns = [
    { key: 'name', header: 'Designation Title' },
    {
      key: 'createdAt',
      header: 'Created Date',
      render: (item) => (item.createdAt ? new Date(item.createdAt).toLocaleDateString() : 'N/A'),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (designation) => (
        <div className={styles.actions}>
          <Button variant="secondary" onClick={() => editDesignation(designation.id)}>
            Edit
          </Button>
          <Button
            variant="ghost"
            className={styles.deleteBtn}
            onClick={() => setPendingDelete(designation)}
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
        title="Designations"
        description="Define employee job titles, corporate hierarchy rankings, and designations."
        actions={
          <Button onClick={() => setModal({ designation: null })}>
            + Add Designation
          </Button>
        }
      />

      <section className={styles.toolbar}>
        <SearchBar
          value={search}
          onChange={updateSearch}
          placeholder="Search designations by title..."
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
            <option value="name">Designation Title</option>
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
          <Loader label="Loading designation records" />
        </div>
      )}

      {requestError && !isLoading && (
        <EmptyState
          title="Designations Unavailable"
          description={requestError}
          action={<Button onClick={loadDesignations}>Try again</Button>}
        />
      )}

      {!isLoading && !requestError && (
        <DataTable
          columns={columns}
          data={page.content || []}
          emptyMessage={
            search ? 'No designations match your search query.' : 'No designations have been created yet.'
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
              ? 'Loading Designation'
              : modal.designation
                ? 'Edit Designation'
                : 'Add Designation'
          }
          onClose={() => !isSaving && setModal(null)}
        >
          {modal.loading ? (
            <div className={styles.loading}>
              <Loader label="Fetching designation details" />
            </div>
          ) : (
            <DesignationForm
              designation={modal.designation}
              isSaving={isSaving}
              onSubmit={saveDesignation}
            />
          )}
        </Modal>
      )}

      <ConfirmDialog
        isOpen={Boolean(pendingDelete)}
        onClose={() => !isDeleting && setPendingDelete(null)}
        onConfirm={deleteDesignation}
        title="Delete Designation"
        description={`Are you sure you want to delete "${pendingDelete?.name || 'this designation'}"? This action cannot be undone.`}
        confirmLabel="Delete Designation"
        isLoading={isDeleting}
      />
    </>
  )
}

export default DesignationsPage
