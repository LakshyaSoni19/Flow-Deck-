import { useState } from 'react'
import Button from '../common/Button'
import FormLayout from '../common/FormLayout'
import Input from '../common/Input'
import UserSelect from '../common/UserSelect'
import { adminService } from '../../services/adminService'
import styles from './ProjectForm.module.css'

const statusOptions = ['NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD']
const initialValues = (project) => ({
  projectName: project?.projectName || '',
  projectCode: project?.projectCode || '',
  description: project?.description || '',
  startDate: project?.startDate || '',
  endDate: project?.endDate || '',
  status: project?.status || 'NOT_STARTED',
  managerId: project?.managerId || '',
})

function ProjectForm({ project, isSaving, onSubmit }) {
  const [values, setValues] = useState(initialValues(project))
  const [manager, setManager] = useState(() => {
    if (project?.manager) return project.manager
    if (project?.projectManager) return project.projectManager
    const id = project?.managerId ?? project?.projectManagerId
    const name = project?.managerName ?? project?.projectManagerName
    return id ? { id, name } : null
  })
  const [errors, setErrors] = useState({})

  const update = (event) =>
    setValues({ ...values, [event.target.name]: event.target.value })

  const submit = (event) => {
    event.preventDefault()
    const nextErrors = {
      projectName: !values.projectName.trim()
        ? 'Project name is required.'
        : values.projectName.length > 45
          ? 'Project name must be at most 45 characters.'
          : '',
      projectCode: !values.projectCode.trim()
        ? 'Project code is required.'
        : values.projectCode.length > 45
          ? 'Project code must be at most 45 characters.'
          : '',
      description: !values.description.trim() ? 'Description is required.' : '',
      startDate: !values.startDate ? 'Start date is required.' : '',
      endDate: !values.endDate
        ? 'End date is required.'
        : values.startDate && values.endDate < values.startDate
          ? 'End date must be after start date.'
          : '',
      status: !statusOptions.includes(values.status) ? 'Select a valid status.' : '',
      managerId: !manager?.id ? 'Select a project manager.' : '',
    }

    setErrors(nextErrors)

    if (!Object.values(nextErrors).some(Boolean)) {
      onSubmit(
        {
          ...values,
          projectName: values.projectName.trim(),
          projectCode: values.projectCode.trim(),
          description: values.description.trim(),
          managerId: manager.id,
        },
        (fields) => setErrors((current) => ({ ...current, ...fields })),
      )
    }
  }

  return (
    <FormLayout onSubmit={submit} noValidate>
      <div className={styles.grid}>
        <div>
          <Input
            id="projectName"
            name="projectName"
            label="Project Name"
            placeholder="e.g. Mobile Banking Revamp"
            value={values.projectName}
            onChange={update}
            error={errors.projectName}
            maxLength="45"
          />
          <span style={{ display: 'block', marginTop: '0.25rem', fontSize: '0.75rem', color: 'var(--color-text-muted)', textAlign: 'right' }}>
            {values.projectName.length}/45 characters
          </span>
        </div>

        <div>
          <Input
            id="projectCode"
            name="projectCode"
            label="Project Code"
            placeholder="e.g. PRJ-2026-01"
            value={values.projectCode}
            onChange={update}
            error={errors.projectCode}
            maxLength="45"
          />
          <span style={{ display: 'block', marginTop: '0.25rem', fontSize: '0.75rem', color: 'var(--color-text-muted)', textAlign: 'right' }}>
            {values.projectCode.length}/45 characters
          </span>
        </div>
      </div>

      <Input
        id="description"
        name="description"
        label="Project Scope & Description"
        placeholder="Provide background, deliverables, and targets for this project..."
        value={values.description}
        onChange={update}
        error={errors.description}
      />

      <div className={styles.grid}>
        <Input
          id="startDate"
          name="startDate"
          type="date"
          label="Start Date"
          value={values.startDate}
          onChange={update}
          error={errors.startDate}
        />
        <Input
          id="endDate"
          name="endDate"
          type="date"
          label="Target End Date"
          value={values.endDate}
          onChange={update}
          error={errors.endDate}
        />
      </div>

      <div className={styles.grid}>
        <div className={styles.selectField}>
          <label htmlFor="status">Execution Status</label>
          <select id="status" name="status" value={values.status} onChange={update}>
            {statusOptions.map((status) => (
              <option key={status} value={status}>
                {status}
              </option>
            ))}
          </select>
          {errors.status && <span>{errors.status}</span>}
        </div>

        <UserSelect
          id="managerId"
          label="Project Manager"
          selectedUser={manager}
          onSelect={(user) => { setManager(user); setErrors((current) => ({ ...current, managerId: '' })) }}
          searchUsers={(query) => adminService.searchUsers({ query })}
          error={errors.managerId}
          disabled={isSaving}
        />
      </div>

      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
        <Button type="submit" isLoading={isSaving} disabled={isSaving}>
          {project ? 'Save Project Changes' : 'Create Project'}
        </Button>
      </div>
    </FormLayout>
  )
}

export default ProjectForm
