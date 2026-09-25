import styles from './ui.module.css'

function Loader({ label = 'Loading' }) {
  return <span className={styles.loader} role="status" aria-label={label} />
}

export default Loader
