import { useState } from 'react'
import Button from '../common/Button'
import FormLayout from '../common/FormLayout'
import UserSelect from '../common/UserSelect'
import { adminService } from '../../services/adminService'

export function AssignManagerForm({ project, isSaving, onSubmit }) {
  const [manager, setManager] = useState(() => {
    if (project?.manager) return project.manager
    if (project?.projectManager) return project.projectManager
    const id = project?.managerId ?? project?.projectManagerId
    const name = project?.managerName ?? project?.projectManagerName
    return id ? { id, name } : null
  })
  const [error, setError] = useState('')
  const submit = (event) => { event.preventDefault(); if (!manager) { setError('Select a project manager.'); return }; onSubmit({ managerId: manager.id }, setError) }
  return <FormLayout onSubmit={submit} noValidate><div style={{ marginBottom: '0.75rem', padding: '0.75rem', borderRadius: '6px', background: 'var(--color-surface-muted)', border: '1px solid var(--color-border-subtle)', fontSize: '0.8125rem', color: 'var(--color-text-secondary)' }}>Assign a designated Project Manager to lead task delegation and progress reporting for this project.</div><UserSelect id="projectManager" label="Project Manager" selectedUser={manager} onSelect={(user) => { setManager(user); setError('') }} searchUsers={(query) => adminService.searchUsers({ query })} error={error} disabled={isSaving} /><div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem' }}><Button type="submit" isLoading={isSaving} disabled={isSaving}>Assign Manager</Button></div></FormLayout>
}

export function ChangeStatusForm({ currentStatus, isSaving, onSubmit }) {
  const [status, setStatus] = useState(currentStatus || 'NOT_STARTED')
  return <FormLayout onSubmit={(event) => { event.preventDefault(); onSubmit({ status }) }}><div style={{ display: 'grid', gap: '.375rem' }}><label htmlFor="projectStatus">Select New Status</label><select id="projectStatus" value={status} onChange={(event) => setStatus(event.target.value)}><option value="NOT_STARTED">NOT_STARTED</option><option value="IN_PROGRESS">IN_PROGRESS</option><option value="COMPLETED">COMPLETED</option><option value="ON_HOLD">ON_HOLD</option></select></div><div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem' }}><Button type="submit" isLoading={isSaving}>Update Status</Button></div></FormLayout>
}
