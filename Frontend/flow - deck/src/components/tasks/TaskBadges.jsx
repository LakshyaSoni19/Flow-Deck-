import styles from './TaskBadges.module.css'

export function StatusBadge({ value }) {
  if (!value) return <span className={`${styles.status} ${styles.statusDefault}`}>N/A</span>

  const valStr = String(value).toUpperCase()
  const badgeClass = valStr.includes('COMPLET')
    ? styles.statusCompleted
    : valStr.includes('PROGRESS')
      ? styles.statusInProgress
      : valStr.includes('PENDING')
        ? styles.statusPending
        : valStr.includes('OVERDUE')
          ? styles.statusOverdue
          : styles.statusDefault

  return <span className={`${styles.status} ${badgeClass}`}>{value}</span>
}

export function PriorityBadge({ value }) {
  if (!value) return <span className={`${styles.priority} ${styles.priorityLow}`}>N/A</span>

  const valStr = String(value).toUpperCase()
  const badgeClass = valStr.includes('HIGH') || valStr.includes('URGENT')
    ? styles.priorityHigh
    : valStr.includes('MEDIUM') || valStr.includes('MED')
      ? styles.priorityMedium
      : styles.priorityLow

  return <span className={`${styles.priority} ${badgeClass}`}>{value}</span>
}

