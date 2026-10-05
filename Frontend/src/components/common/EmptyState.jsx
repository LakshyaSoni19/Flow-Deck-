import styles from './ui.module.css'

function EmptyState({ title, description, action }) {
  return <div className={styles.emptyState}><h2>{title}</h2>{description && <p>{description}</p>}{action}</div>
}

export default EmptyState
