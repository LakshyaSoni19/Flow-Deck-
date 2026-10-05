import { useState } from 'react'
import toast from 'react-hot-toast'
import Button from '../common/Button'
import Card from '../common/Card'
import ConfirmDialog from '../common/ConfirmDialog'
import DataTable from '../common/DataTable'
import FormLayout from '../common/FormLayout'
import UserSelect from '../common/UserSelect'
import Modal from '../common/Modal'
import { lookupService } from '../../services/lookupService'
import styles from './ProjectWorkspaceDetails.module.css'

const display = (value) => value ?? 'Not available'
const memberUser = (member) => member?.user || member || {}
const memberName = (member) => {
  const user = memberUser(member)
  return member?.userName || member?.name || [user.firstName, user.lastName].filter(Boolean).join(' ') || user.name || member?.email || user.email || 'Not available'
}
const summaryEmployeeName = (row) =>
  row.employeeName ||
  row.employeeUserName ||
  row.userName ||
  row.name ||
  'Not available'

function ProjectWorkspaceDetails({
  details,
  overview,
  progress,
  stats,
  members = [],
  employeeSummary = [],
  onAddMember,
  onRemoveMember,
}) {
  const [activeTab, setActiveTab] = useState('overview')
  const [isAddModalOpen, setIsAddModalOpen] = useState(false)
  const [selectedUser, setSelectedUser] = useState(null)
  const [userIdError, setUserIdError] = useState('')
  const [isAdding, setIsAdding] = useState(false)

  const [pendingRemoveMember, setPendingRemoveMember] = useState(null)
  const [isRemoving, setIsRemoving] = useState(false)

  const handleAddSubmit = async (event) => {
    event.preventDefault()
    setUserIdError('')
    if (!selectedUser?.id) {
      setUserIdError('Select a user to add.')
      return
    }
    const userId = selectedUser.id
    const isDuplicate = members.some((member) => {
      const memberId = member.userId ?? memberUser(member).id
      return Number(memberId) === Number(userId)
    })
    if (isDuplicate) {
      toast.error('This user is already a team member of this project.')
      return
    }

    setIsAdding(true)
    try {
      if (onAddMember) {
        await onAddMember(details.id, { userId })
      }
      setIsAddModalOpen(false)
      setSelectedUser(null)
    } catch {
      // Handled upstream
    } finally {
      setIsAdding(false)
    }
  }

  const handleRemoveConfirm = async () => {
    if (!pendingRemoveMember) return
    const memberUserId = pendingRemoveMember.userId ?? memberUser(pendingRemoveMember).id
    if (!memberUserId) {
      toast.error('Unable to resolve target user ID for removal.')
      return
    }

    setIsRemoving(true)
    try {
      if (onRemoveMember) {
        await onRemoveMember(details.id, memberUserId)
      }
      setPendingRemoveMember(null)
    } catch {
      // Handled upstream
    } finally {
      setIsRemoving(false)
    }
  }

  const memberColumns = [
    {
      key: 'member',
      header: 'Team Member',
      render: (member) => {
        const name = memberName(member)
        const initial = name.charAt(0).toUpperCase() || 'M'
        return (
          <div className={styles.memberCell}>
            <span className={styles.memberAvatar}>{initial}</span>
            <span className={styles.memberName}>{name}</span>
          </div>
        )
      },
    },
    {
      key: 'role',
      header: 'Role',
      render: (member) => (
        <span style={{ display: 'inline-block', padding: '0.15rem 0.5rem', borderRadius: '4px', background: 'rgba(15, 118, 110, 0.1)', color: '#0f766e', fontSize: '0.75rem', fontWeight: 700 }}>
          {member.roleName || member.role || 'Team Member'}
        </span>
      ),
    },
    { key: 'email', header: 'Email Address', render: (member) => member.email || memberUser(member).email || 'N/A' },
    {
      key: 'actions',
      header: 'Actions',
      render: (member) => (
        <div className={styles.actions}>
          <Button
            variant="ghost"
            className={styles.deleteBtn}
            onClick={() => setPendingRemoveMember(member)}
            aria-label={`Remove ${memberName(member)} from project`}
          >
            Remove
          </Button>
        </div>
      ),
    },
  ]

  const employeeSummaryColumns = [
    {
      key: 'employeeName',
      header: 'Employee Profile',
      render: (row) => {
        const name = summaryEmployeeName(row)
        const initial = name.charAt(0).toUpperCase() || 'E'
        return (
          <div className={styles.memberCell}>
            <span className={styles.memberAvatar}>{initial}</span>
            <span className={styles.memberName}>{name}</span>
          </div>
        )
      },
    },
    {
      key: 'totalAssignedTasks',
      header: 'Total Tasks',
      render: (row) => row.totalAssignedTasks ?? row.totalTasks ?? 0,
    },
    { key: 'completedTasks', header: 'Completed', render: (row) => row.completedTasks ?? 0 },
    { key: 'pendingTasks', header: 'Pending', render: (row) => row.pendingTasks ?? 0 },
    { key: 'inProgressTasks', header: 'In Progress', render: (row) => row.inProgressTasks ?? 0 },
    { key: 'overdueTasks', header: 'Overdue', render: (row) => row.overdueTasks ?? 0 },
    {
      key: 'completionPercentage',
      header: 'Completion %',
      render: (row) => {
        const pct = Math.min(100, Math.max(0, row.completionPercentage ?? 0))
        return (
          <div style={{ width: '100%', minWidth: '90px' }}>
            <span style={{ fontSize: '0.8125rem', fontWeight: 700, color: 'var(--color-text-primary)' }}>
              {pct}%
            </span>
            <div className={styles.progressTrack} style={{ height: '0.375rem', margin: '0.25rem 0 0' }}>
              <div className={styles.progressBar} style={{ width: `${pct}%` }} />
            </div>
          </div>
        )
      },
    },
  ]

  const completionPct = Math.min(
    100,
    Math.max(0, progress.completionPercentage ?? overview.completionPercentage ?? 0),
  )

  return (
    <div className={styles.layout}>
      <nav className={styles.tabBar} aria-label="Project Workspace Navigation">
        <button
          className={`${styles.tabButton} ${activeTab === 'overview' ? styles.activeTab : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          Overview
        </button>
        <button
          className={`${styles.tabButton} ${activeTab === 'progress' ? styles.activeTab : ''}`}
          onClick={() => setActiveTab('progress')}
        >
          Progress
        </button>
        <button
          className={`${styles.tabButton} ${activeTab === 'stats' ? styles.activeTab : ''}`}
          onClick={() => setActiveTab('stats')}
        >
          Statistics
        </button>
        <button
          className={`${styles.tabButton} ${activeTab === 'members' ? styles.activeTab : ''}`}
          onClick={() => setActiveTab('members')}
        >
          Team Members ({members.length})
        </button>
        <button
          className={`${styles.tabButton} ${activeTab === 'summary' ? styles.activeTab : ''}`}
          onClick={() => setActiveTab('summary')}
        >
          Employee Summary
        </button>
      </nav>

      {activeTab === 'overview' && (
        <div style={{ display: 'grid', gap: '1.25rem' }}>
          <Card>
            <div style={{ marginBottom: '1rem', paddingBottom: '0.75rem', borderBottom: '1px solid var(--color-border)' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem' }}>
                <h3 style={{ margin: 0, fontSize: '1.125rem', fontWeight: 800 }}>
                  {details.projectName || 'Untitled Project'}
                </h3>
                <span style={{ padding: '0.2rem 0.6rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 700, background: 'rgba(15, 118, 110, 0.1)', color: '#0f766e' }}>
                  {details.status || 'NOT_STARTED'}
                </span>
              </div>
            </div>
            <dl className={styles.details}>
              <div>
                <dt>Project Code</dt>
                <dd>{display(details.projectCode)}</dd>
              </div>
              <div>
                <dt>Execution Status</dt>
                <dd>{display(details.status)}</dd>
              </div>
              <div>
                <dt>Start Date</dt>
                <dd>{details.startDate ? new Date(details.startDate).toLocaleDateString() : 'N/A'}</dd>
              </div>
              <div>
                <dt>Target End Date</dt>
                <dd>{details.endDate ? new Date(details.endDate).toLocaleDateString() : 'N/A'}</dd>
              </div>
              <div style={{ gridColumn: '1 / -1' }}>
                <dt>Project Description</dt>
                <dd style={{ lineHeight: 1.5 }}>{display(details.description)}</dd>
              </div>
            </dl>
          </Card>

          <div className={styles.grid}>
            <Card>
              <div className={styles.metricCard}>
                <span className={styles.metricTitle}>Total Tasks</span>
                <span className={styles.metricValue}>{display(overview.totalTasks ?? 0)}</span>
              </div>
            </Card>
            <Card>
              <div className={styles.metricCard}>
                <span className={styles.metricTitle}>Completed Tasks</span>
                <span className={styles.metricValue} style={{ color: '#16a34a' }}>
                  {display(overview.completedTasks ?? 0)}
                </span>
              </div>
            </Card>
            <Card>
              <div className={styles.metricCard}>
                <span className={styles.metricTitle}>Pending Tasks</span>
                <span className={styles.metricValue} style={{ color: '#d97706' }}>
                  {display(overview.pendingTasks ?? 0)}
                </span>
              </div>
            </Card>
          </div>
        </div>
      )}

      {activeTab === 'progress' && (
        <div style={{ display: 'grid', gap: '1.25rem' }}>
          <Card>
            <div className={styles.sectionHeader}>
              <h2>Project Execution Progress</h2>
              <span style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--color-primary)' }}>
                {completionPct}%
              </span>
            </div>
            <div className={styles.progressTrack}>
              <div className={styles.progressBar} style={{ width: `${completionPct}%` }} />
            </div>
            <p style={{ margin: 0, fontSize: '0.8125rem', color: 'var(--color-text-secondary)' }}>
              Overall workflow task completion rate across all assigned team members.
            </p>
          </Card>

          <div className={styles.grid}>
            <Card>
              <div className={styles.metricCard}>
                <span className={styles.metricTitle}>In Progress Tasks</span>
                <span className={styles.metricValue} style={{ color: '#0f766e' }}>
                  {display(progress.inProgressTasks ?? overview.inProgressTasks ?? 0)}
                </span>
              </div>
            </Card>
            <Card>
              <div className={styles.metricCard}>
                <span className={styles.metricTitle}>Pending Tasks</span>
                <span className={styles.metricValue} style={{ color: '#d97706' }}>
                  {display(progress.pendingTasks ?? overview.pendingTasks ?? 0)}
                </span>
              </div>
            </Card>
            <Card>
              <div className={styles.metricCard}>
                <span className={styles.metricTitle}>Overdue Tasks</span>
                <span className={styles.metricValue} style={{ color: '#dc2626' }}>
                  {display(progress.overdueTasks ?? overview.overdueTasks ?? 0)}
                </span>
              </div>
            </Card>
          </div>
        </div>
      )}

      {activeTab === 'stats' && (
        <div className={styles.grid}>
          <Card>
            <div className={styles.metricCard}>
              <span className={styles.metricTitle}>Active Team Members</span>
              <span className={styles.metricValue}>
                {display(members.length || stats.totalMembers || overview.totalMembers || 0)}
              </span>
            </div>
          </Card>
          <Card>
            <div className={styles.metricCard}>
              <span className={styles.metricTitle}>Total Workspace Tasks</span>
              <span className={styles.metricValue}>
                {display(stats.totalTasks ?? overview.totalTasks ?? 0)}
              </span>
            </div>
          </Card>
          <Card>
            <div className={styles.metricCard}>
              <span className={styles.metricTitle}>Completed Deliverables</span>
              <span className={styles.metricValue} style={{ color: '#16a34a' }}>
                {display(stats.completedTasks ?? overview.completedTasks ?? 0)}
              </span>
            </div>
          </Card>
        </div>
      )}

      {activeTab === 'members' && (
        <section style={{ display: 'grid', gap: '1rem' }}>
          <div className={styles.sectionHeader}>
            <h2>Assigned Team Members</h2>
            <Button onClick={() => setIsAddModalOpen(true)}>+ Add Member</Button>
          </div>
          <DataTable
            columns={memberColumns}
            data={members}
            emptyMessage="No project members are currently assigned. Click '+ Add Member' to assign team members."
          />
        </section>
      )}

      {activeTab === 'summary' && (
        <section style={{ display: 'grid', gap: '1rem' }}>
          <div className={styles.sectionHeader}>
            <h2>Employee Task Performance Summary</h2>
          </div>
          <DataTable
            columns={employeeSummaryColumns}
            data={employeeSummary}
            emptyMessage="No employee task summary metrics available for this project."
          />
        </section>
      )}

      {isAddModalOpen && (
        <Modal
          isOpen
          title="Add Team Member to Project"
          onClose={() => !isAdding && setIsAddModalOpen(false)}
        >
          <FormLayout onSubmit={handleAddSubmit} noValidate>
            <div style={{ marginBottom: '0.75rem', padding: '0.75rem', borderRadius: '6px', background: 'var(--color-surface-muted)', border: '1px solid var(--color-border-subtle)', fontSize: '0.8125rem', color: 'var(--color-text-secondary)' }}>
              Search for the person you want to add to this project workspace.
            </div>

            <UserSelect
              id="projectMember"
              label="Add Project Member"
              selectedUser={selectedUser}
              searchUsers={lookupService.searchUsers}
              onSelect={(user) => {
                setSelectedUser(user)
                if (userIdError) setUserIdError('')
              }}
              error={userIdError}
              disabled={isAdding}
            />

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
              <Button type="submit" isLoading={isAdding} disabled={isAdding}>
                Add Member
              </Button>
            </div>
          </FormLayout>
        </Modal>
      )}

      <ConfirmDialog
        isOpen={Boolean(pendingRemoveMember)}
        onClose={() => !isRemoving && setPendingRemoveMember(null)}
        onConfirm={handleRemoveConfirm}
        title="Remove Project Member"
        description={`Are you sure you want to remove "${memberName(pendingRemoveMember || {})}" from this project workspace?`}
        confirmLabel="Remove Member"
        isLoading={isRemoving}
      />
    </div>
  )
}

export default ProjectWorkspaceDetails
