import styles from './architecture.module.css'

function PageContainer({ children, className = '' }) { return <div className={`${styles.pageContainer} ${className}`.trim()}>{children}</div> }
export default PageContainer
