import Card from '../common/Card'
import styles from './ProjectDetails.module.css'

const display = (value) => value || 'Not available'

function ProjectDetails({ project }) {
  if (!project) return null

  const manager =
    project.managerName || project.projectManagerName || 'Not assigned'

  const statusName = String(project.status || 'NOT_STARTED').toUpperCase()
  const statusBadgeStyle = statusName.includes('COMPLET')
    ? { background: 'rgba(22, 163, 74, 0.1)', color: '#16a34a' }
    : statusName.includes('PROGRESS')
      ? { background: 'rgba(15, 118, 110, 0.1)', color: '#0f766e' }
      : statusName.includes('HOLD')
        ? { background: 'rgba(217, 119, 6, 0.1)', color: '#d97706' }
        : { background: 'rgba(100, 116, 139, 0.1)', color: '#64748b' }

  return (
    <Card>
      <div style={{ marginBottom: '1.25rem', paddingBottom: '1rem', borderBottom: '1px solid var(--color-border)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem' }}>
          <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 800, color: 'var(--color-text-primary)' }}>
            {project.projectName || 'Untitled Project'}
          </h3>
          <span style={{ padding: '0.2rem 0.6rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 700, ...statusBadgeStyle }}>
            {project.status || 'NOT_STARTED'}
          </span>
        </div>
        {project.projectCode && (
          <span style={{ display: 'inline-block', marginTop: '0.375rem', padding: '0.125rem 0.5rem', borderRadius: '4px', background: 'var(--color-surface-muted)', border: '1px solid var(--color-border-subtle)', fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-muted)' }}>
            Code: {project.projectCode}
          </span>
        )}
      </div>

      <dl className={styles.details}>
        <div>
          <dt>Project Code</dt>
          <dd>{display(project.projectCode)}</dd>
        </div>
        <div>
          <dt>Status</dt>
          <dd>{display(project.status)}</dd>
        </div>
        <div>
          <dt>Start Date</dt>
          <dd>{project.startDate ? new Date(project.startDate).toLocaleDateString() : 'N/A'}</dd>
        </div>
        <div>
          <dt>End Date</dt>
          <dd>{project.endDate ? new Date(project.endDate).toLocaleDateString() : 'N/A'}</dd>
        </div>
        <div>
          <dt>Project Manager</dt>
          <dd>{display(manager)}</dd>
        </div>
        <div>
          <dt>Created Date</dt>
          <dd>{project.createdAt ? new Date(project.createdAt).toLocaleDateString() : 'N/A'}</dd>
        </div>
        <div style={{ gridColumn: '1 / -1' }}>
          <dt>Description</dt>
          <dd style={{ lineHeight: 1.5 }}>{display(project.description)}</dd>
        </div>
      </dl>
    </Card>
  )
}

export default ProjectDetails
