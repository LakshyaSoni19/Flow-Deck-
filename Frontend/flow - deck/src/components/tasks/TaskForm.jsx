import { useEffect, useState } from 'react'
import Button from '../common/Button'
import FormLayout from '../common/FormLayout'
import Input from '../common/Input'
import LookupSelect from '../common/LookupSelect'
import { lookupService } from '../../services/lookupService'
import { pmService } from '../../services/pmService'
import {
  getProjectMemberId,
  getProjectMemberUser,
  getUserDisplayName,
  normalizeTask,
} from '../../utils/taskNormalization'
import styles from './TaskForm.module.css'

const initial = (task) => {
  const normalizedTask = normalizeTask(task)
  return {
    title: normalizedTask?.title || '',
    description: normalizedTask?.description || '',
    startDate: normalizedTask?.startDate || '',
    dueDate: normalizedTask?.dueDate || '',
    estimatedHours: normalizedTask?.estimatedHours || '',
    taskStatusId: normalizedTask?.taskStatusId ?? normalizedTask?.taskStatus?.id ?? '',
    taskPriorityId: normalizedTask?.taskPriorityId ?? normalizedTask?.taskPriority?.id ?? '',
    taskTypeId: normalizedTask?.taskTypeId ?? normalizedTask?.taskType?.id ?? '',
    assignedUserId: normalizedTask?.assignedUserId ?? '',
  }
}

const unpack = (response) => {
  const data = response?.data ?? response
  return Array.isArray(data) ? data : data?.content || []
}

function TaskForm({ task, isSaving, onSubmit, projectId }) {
  const [values, setValues] = useState(initial(task))
  const [errors, setErrors] = useState({})
  const [members, setMembers] = useState([])
  const [membersLoading, setMembersLoading] = useState(Boolean(projectId))
  const [membersError, setMembersError] = useState('')

  const loadMembers = async () => {
    if (!projectId) return
    setMembersLoading(true)
    setMembersError('')
    try {
      setMembers(unpack(await pmService.getProjectMembers(projectId)))
    } catch {
      setMembersError('Unable to load project members. Please try again.')
    } finally {
      setMembersLoading(false)
    }
  }

  // eslint-disable-next-line react-hooks/set-state-in-effect, react-hooks/exhaustive-deps
  useEffect(() => { void loadMembers() }, [projectId])

  const update = (event) => setValues({ ...values, [event.target.name]: event.target.value })

  const submit = (event) => {
    event.preventDefault()
    const required = ['taskStatusId', 'taskPriorityId', 'taskTypeId', 'assignedUserId']
    const next = {
      title: !values.title.trim() ? 'Task title is required.' : '',
      description: !values.description.trim() ? 'Task description is required.' : '',
      startDate: !values.startDate ? 'Start date is required.' : '',
      dueDate: !values.dueDate || values.dueDate < values.startDate ? 'Enter a valid due date.' : '',
      estimatedHours: Number(values.estimatedHours) > 0 ? '' : 'Enter estimated hours.',
    }
    required.forEach((key) => { next[key] = values[key] ? '' : 'Make a selection.' })
    setErrors(next)

    if (!Object.values(next).some(Boolean)) {
      onSubmit({
        ...values,
        title: values.title.trim(),
        description: values.description.trim(),
        estimatedHours: Number(values.estimatedHours),
        taskStatusId: Number(values.taskStatusId),
        taskPriorityId: Number(values.taskPriorityId),
        taskTypeId: Number(values.taskTypeId),
        assignedUserId: Number(values.assignedUserId),
      }, (fields) => setErrors((current) => ({ ...current, ...fields })))
    }
  }

  return (
    <FormLayout onSubmit={submit} noValidate>
      <Input id="title" name="title" label="Task Title" value={values.title} onChange={update} error={errors.title} />
      <Input id="description" name="description" label="Task Description" value={values.description} onChange={update} error={errors.description} />
      <div className={styles.grid}>
        <Input id="startDate" name="startDate" type="date" label="Start Date" value={values.startDate} onChange={update} error={errors.startDate} />
        <Input id="dueDate" name="dueDate" type="date" label="Target Due Date" value={values.dueDate} onChange={update} error={errors.dueDate} />
      </div>
      <div className={styles.grid}>
        <Input id="estimatedHours" name="estimatedHours" type="number" min="1" step=".5" label="Estimated Hours" value={values.estimatedHours} onChange={update} error={errors.estimatedHours} />
        <div className={styles.field}>
          <label htmlFor="assignedUserId">Assignee</label>
          <select id="assignedUserId" name="assignedUserId" className={styles.select} value={values.assignedUserId} onChange={update} disabled={membersLoading || !!membersError}>
            <option value="">{membersLoading ? 'Loading project members...' : membersError ? 'Unable to load members' : 'Select a project member'}</option>
            {members.map((member) => {
              const user = getProjectMemberUser(member)
              const userId = getProjectMemberId(member)
              return <option key={userId} value={userId}>{getUserDisplayName(user)}</option>
            })}
          </select>
          {membersError && <button type="button" onClick={loadMembers}>Try again</button>}
          {errors.assignedUserId && <span>{errors.assignedUserId}</span>}
        </div>
      </div>
      <div className={styles.grid}>
        <LookupSelect id="taskStatusId" label="Status" value={values.taskStatusId} onChange={(value) => setValues({ ...values, taskStatusId: value })} loadOptions={lookupService.getTaskStatuses} error={errors.taskStatusId} />
        <LookupSelect id="taskPriorityId" label="Priority" value={values.taskPriorityId} onChange={(value) => setValues({ ...values, taskPriorityId: value })} loadOptions={lookupService.getTaskPriorities} error={errors.taskPriorityId} />
      </div>
      <LookupSelect id="taskTypeId" label="Task Type" value={values.taskTypeId} onChange={(value) => setValues({ ...values, taskTypeId: value })} loadOptions={lookupService.getTaskTypes} error={errors.taskTypeId} />
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem' }}>
        <Button type="submit" isLoading={isSaving} disabled={isSaving}>{task ? 'Save Task Changes' : 'Create Task'}</Button>
      </div>
    </FormLayout>
  )
}

export default TaskForm
