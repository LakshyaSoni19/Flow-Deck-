import Input from './Input'
import styles from './ui.module.css'

function SearchBar({ value, onChange, placeholder = 'Search', label = 'Search' }) {
  return <div className={styles.searchBar}><Input aria-label={label} value={value} onChange={onChange} placeholder={placeholder} /></div>
}

export default SearchBar
