import Card from '../common/Card'
import styles from './Dashboard.module.css'

function DashboardSkeleton({ count = 4 }) { return <div className={styles.cardGrid}>{Array.from({ length: count }, (_, index) => <Card key={index}><div className={styles.skeletonLabel} /><div className={styles.skeletonValue} /></Card>)}</div> }
export default DashboardSkeleton
