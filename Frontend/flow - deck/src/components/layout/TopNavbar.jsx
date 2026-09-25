import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ROLES } from '../../constants/roles'
import { useAuth } from '../../hooks/useAuth'
import styles from './DashboardShell.module.css'

const roleLabels = {
  [ROLES.ADMIN]: 'Administrator',
  [ROLES.PROJECT_MANAGER]: 'Project Manager',
  [ROLES.EMPLOYEE]: 'Employee',
}

const roleBadgeStyles = {
  [ROLES.ADMIN]: styles.roleBadgeAdmin,
  [ROLES.PROJECT_MANAGER]: styles.roleBadgePm,
  [ROLES.EMPLOYEE]: styles.roleBadgeEmp,
}

function TopNavbar({ onMenuToggle }) {
  const { user, roles, endSession } = useAuth()
  const navigate = useNavigate()
  const [isDropdownOpen, setIsDropdownOpen] = useState(false)
  const dropdownRef = useRef(null)

  const primaryRole = roles?.[0] || ROLES.EMPLOYEE
  const fullName = user
    ? `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email || 'User'
    : 'User'
  const initial = fullName.charAt(0).toUpperCase() || 'U'

  const handleLogout = () => {
    setIsDropdownOpen(false)
    endSession()
    navigate('/login', { replace: true })
  }

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  return (
    <header className={styles.topNavbar}>
      <div className={styles.navbarLeft}>
        <button
          className={styles.mobileToggle}
          type="button"
          onClick={onMenuToggle}
          aria-label="Toggle navigation drawer"
        >
          Menu
        </button>
        <span className={styles.workspaceLabel}>Flow Deck Workspace</span>
      </div>

      <div className={styles.navbarRight}>
        <div className={styles.userMenuWrapper} ref={dropdownRef}>
          <button
            className={styles.userMenuButton}
            type="button"
            onClick={() => setIsDropdownOpen((prev) => !prev)}
            aria-expanded={isDropdownOpen}
            aria-haspopup="true"
            aria-label="User profile menu"
          >
            {user?.profileImage ? (
              <img src={user.profileImage} alt={fullName} className={styles.avatarImage} />
            ) : (
              <span className={styles.avatarFallback}>{initial}</span>
            )}
            <div className={styles.userInfoMeta}>
              <span className={styles.userName}>{fullName}</span>
              <span className={`${styles.roleBadge} ${roleBadgeStyles[primaryRole] || ''}`}>
                {roleLabels[primaryRole] || primaryRole}
              </span>
            </div>
          </button>

          {isDropdownOpen && (
            <div className={styles.dropdownMenu} role="menu" aria-orientation="vertical">
              <Link
                to="/employee/profile"
                className={styles.dropdownItem}
                role="menuitem"
                onClick={() => setIsDropdownOpen(false)}
              >
                My Profile & Settings
              </Link>
              <div className={styles.dropdownDivider} role="separator" />
              <button
                type="button"
                className={`${styles.dropdownItem} ${styles.dropdownItemDanger}`}
                role="menuitem"
                onClick={handleLogout}
              >
                Log Out
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}

export default TopNavbar

