import Button from './Button'
import styles from './ui.module.css'

function Pagination({ page = 1, totalPages = 1, onPageChange }) {
  if (totalPages <= 1) return null
  return <nav className={styles.pagination} aria-label="Pagination"><Button variant="secondary" disabled={page === 1} onClick={() => onPageChange(page - 1)}>Previous</Button><span>Page {page} of {totalPages}</span><Button variant="secondary" disabled={page === totalPages} onClick={() => onPageChange(page + 1)}>Next</Button></nav>
}

export default Pagination
