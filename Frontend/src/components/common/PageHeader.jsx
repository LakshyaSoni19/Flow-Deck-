import styles from './ui.module.css'

function PageHeader({ title, description, actions }) {
  return <header className={styles.pageHeader}><div><h1>{title}</h1>{description && <p>{description}</p>}</div>{actions && <div className={styles.pageActions}>{actions}</div>}</header>
}

export default PageHeader
