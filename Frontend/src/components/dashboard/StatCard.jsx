import Card from '../common/Card'
import styles from './Dashboard.module.css'

function StatCard({ label, value, detail }) { return <Card><p className={styles.statLabel}>{label}</p><p className={styles.statValue}>{value}</p>{detail && <p className={styles.statDetail}>{detail}</p>}</Card> }
export default StatCard
