import { NavLink } from 'react-router-dom'
import styles from './DashboardShell.module.css'

function Sidebar({ menuItems, isCollapsed, onToggle }) {
  return (
    <aside
      className={`${styles.sidebar} ${isCollapsed ? styles.collapsed : ''}`}
      aria-label="Workspace Sidebar"
    >
      <div className={styles.sidebarHeader}>
        <div className={styles.brand}>
          <span className={styles.brandIcon}>FD</span>
          {!isCollapsed && <span>Flow Deck</span>}
        </div>
        <button
          className={styles.iconButton}
          type="button"
          onClick={onToggle}
          aria-label={isCollapsed ? 'Expand navigation sidebar' : 'Collapse navigation sidebar'}
          title={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        >
          {isCollapsed ? '→' : '←'}
        </button>
      </div>

      <nav className={styles.navigation} aria-label="Workspace Navigation">
        {menuItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            title={isCollapsed ? item.label : undefined}
            className={({ isActive }) =>
              `${styles.navItem} ${isActive ? styles.navItemActive : ''}`
            }
          >
            {isCollapsed ? item.label.charAt(0) : item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}

export default Sidebar

