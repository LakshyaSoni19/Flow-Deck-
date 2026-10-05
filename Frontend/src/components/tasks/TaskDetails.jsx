import Card from '../common/Card'
import { PriorityBadge, StatusBadge } from './TaskBadges'
import { getAssigneeLabel } from '../../utils/taskNormalization'

const display = (value) => value || 'Not available'

function TaskDetails({ task }) {
  if (!task) return null

  const assignee = getAssigneeLabel(task)

  return (
    <Card>
      <div style={{ marginBottom: '1.25rem', paddingBottom: '1rem', borderBottom: '1px solid var(--color-border)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.75rem' }}>
          <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 800, color: 'var(--color-text-primary)' }}>
            {task.title || 'Untitled Task'}
          </h3>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <StatusBadge value={task.taskStatus} />
            <PriorityBadge value={task.taskPriority} />
          </div>
        </div>
      </div>

      <dl style={{ display: 'grid', gridTemplateColumns: 'repeat(2, minmax(0, 1fr))', gap: '1rem', margin: 0 }}>
        <div>
          <dt style={{ color: 'var(--color-text-muted)', fontSize: '0.8125rem', fontWeight: 600 }}>Assigned Employee</dt>
          <dd style={{ margin: '0.25rem 0 0', fontWeight: 700, color: 'var(--color-text-primary)' }}>{assignee}</dd>
        </div>
        <div>
          <dt style={{ color: 'var(--color-text-muted)', fontSize: '0.8125rem', fontWeight: 600 }}>Due Date</dt>
          <dd style={{ margin: '0.25rem 0 0', fontWeight: 700, color: 'var(--color-text-primary)' }}>
            {task.dueDate ? new Date(task.dueDate).toLocaleDateString() : 'N/A'}
          </dd>
        </div>
        <div>
          <dt style={{ color: 'var(--color-text-muted)', fontSize: '0.8125rem', fontWeight: 600 }}>Start Date</dt>
          <dd style={{ margin: '0.25rem 0 0', fontWeight: 700, color: 'var(--color-text-primary)' }}>
            {task.startDate ? new Date(task.startDate).toLocaleDateString() : 'N/A'}
          </dd>
        </div>
        <div>
          <dt style={{ color: 'var(--color-text-muted)', fontSize: '0.8125rem', fontWeight: 600 }}>Estimated Hours</dt>
          <dd style={{ margin: '0.25rem 0 0', fontWeight: 700, color: 'var(--color-text-primary)' }}>
            {task.estimatedHours ? `${task.estimatedHours} hrs` : 'N/A'}
          </dd>
        </div>
        {task.actualHours && (
          <div>
            <dt style={{ color: 'var(--color-text-muted)', fontSize: '0.8125rem', fontWeight: 600 }}>Actual Hours</dt>
            <dd style={{ margin: '0.25rem 0 0', fontWeight: 700, color: 'var(--color-text-primary)' }}>{task.actualHours} hrs</dd>
          </div>
        )}
        <div style={{ gridColumn: '1 / -1' }}>
          <dt style={{ color: 'var(--color-text-muted)', fontSize: '0.8125rem', fontWeight: 600 }}>Task Scope & Description</dt>
          <dd style={{ margin: '0.25rem 0 0', lineHeight: 1.5, color: 'var(--color-text-primary)' }}>{display(task.description)}</dd>
        </div>
      </dl>
    </Card>
  )
}

export default TaskDetails
