import { Link, useLocation } from 'react-router-dom'
import styles from './DashboardShell.module.css'

const toLabel = (segment) => segment.replaceAll('-', ' ').replace(/\b\w/g, (letter) => letter.toUpperCase())

function Breadcrumbs() {
  const { pathname } = useLocation()
  const segments = pathname.split('/').filter(Boolean)

  return <nav className={styles.breadcrumbs} aria-label="Breadcrumb"><Link to="/">Flow Deck</Link>{segments.map((segment, index) => <span key={`${segment}-${index}`}><span aria-hidden="true">/</span><span>{toLabel(segment)}</span></span>)}</nav>
}

export default Breadcrumbs
