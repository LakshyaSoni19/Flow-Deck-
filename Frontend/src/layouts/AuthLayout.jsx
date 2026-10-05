import { Outlet } from 'react-router-dom'
import styles from './AuthLayout.module.css'

function AuthLayout() {
  return (
    <main className={styles.layout}>
      <div className={styles.brand}>Flow Deck</div>
      <div className={styles.content}><Outlet /></div>
    </main>
  )
}

export default AuthLayout
