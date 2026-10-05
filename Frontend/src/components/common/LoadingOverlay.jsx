import Loader from './Loader'
import styles from './architecture.module.css'

function LoadingOverlay({ isVisible, label = 'Loading content' }) { return isVisible ? <div className={styles.loadingOverlay}><Loader label={label} /></div> : null }
export default LoadingOverlay
