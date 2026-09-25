import Card from '../common/Card'
import styles from './UserDetails.module.css'

const display = (value) => value || 'Not available'

function UserDetails({ user }) {
  if (!user) return null

  const fullName = `${user.firstName || ''} ${user.lastName || ''}`.trim() || 'User Profile'
  const initial = fullName.charAt(0).toUpperCase() || 'U'

  return (
    <Card>
      <div className={styles.detailsHero}>
        {user.profileImage ? (
          <img src={user.profileImage} alt={fullName} className={styles.avatarImg} />
        ) : (
          <div className={styles.avatarLarge}>{initial}</div>
        )}
        <div className={styles.heroMeta}>
          <h3>{fullName}</h3>
          <span style={{ fontSize: '0.8125rem', color: 'var(--color-text-secondary)' }}>
            {user.email || 'No email registered'}
          </span>
        </div>
      </div>

      <dl className={styles.grid}>
        <div>
          <dt>Full Name</dt>
          <dd>{fullName}</dd>
        </div>
        <div>
          <dt>Email Address</dt>
          <dd>{display(user.email)}</dd>
        </div>
        <div>
          <dt>Mobile Number</dt>
          <dd>{display(user.mobile)}</dd>
        </div>
        <div>
          <dt>Department</dt>
          <dd>{display(user.departmentName)}</dd>
        </div>
        <div>
          <dt>Designation</dt>
          <dd>{display(user.designationName)}</dd>
        </div>
        <div>
          <dt>Assigned Roles</dt>
          <dd>{user.roles?.join(', ') || 'None'}</dd>
        </div>
        <div>
          <dt>Active Status</dt>
          <dd>
            <span
              style={{
                display: 'inline-block',
                padding: '0.2rem 0.5rem',
                borderRadius: '4px',
                fontSize: '0.75rem',
                fontWeight: 700,
                background: user.isActive ? 'rgba(22, 163, 74, 0.1)' : 'rgba(220, 38, 38, 0.1)',
                color: user.isActive ? '#16a34a' : '#dc2626',
              }}
            >
              {user.isActive ? 'Active Account' : 'Inactive Account'}
            </span>
          </dd>
        </div>
        <div>
          <dt>Approval Status</dt>
          <dd>{display(user.approvalStatus)}</dd>
        </div>
        {user.gender && (
          <div>
            <dt>Gender</dt>
            <dd>{user.gender}</dd>
          </div>
        )}
        {user.dob && (
          <div>
            <dt>Date of Birth</dt>
            <dd>{user.dob}</dd>
          </div>
        )}
        {user.address && (
          <div style={{ gridColumn: '1 / -1' }}>
            <dt>Address</dt>
            <dd>{user.address}</dd>
          </div>
        )}
      </dl>
    </Card>
  )
}

export default UserDetails

