import { Link } from 'react-router-dom'
import styles from './AuthLinks.module.css'

function AuthLinks({ links }) { return <nav className={styles.links}>{links.map((link) => <Link key={link.to} to={link.to}>{link.label}</Link>)}</nav> }
export default AuthLinks
