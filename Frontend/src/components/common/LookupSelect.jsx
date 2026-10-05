import { useEffect, useState } from 'react'
import styles from './ui.module.css'

const items = (response) => {
  const data = response?.data ?? response
  return Array.isArray(data) ? data : data?.content || []
}

export function LookupSelect({ id, label, value, onChange, loadOptions, placeholder = 'Select an option', error, disabled }) {
  const [options, setOptions] = useState([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const load = async () => {
    setLoading(true); setLoadError('')
    try { setOptions(items(await loadOptions())) } catch { setLoadError(`Unable to load ${label.toLowerCase()}. Please try again.`) } finally { setLoading(false) }
  }
  // Fetching lookup data is intentionally initiated when this selector mounts.
  // eslint-disable-next-line react-hooks/set-state-in-effect, react-hooks/exhaustive-deps
  useEffect(() => { void load() }, [])
  return <div className={styles.field}>
    <label htmlFor={id}>{label}</label>
    <select id={id} className={`${styles.input} ${error ? styles.inputError : ''}`} value={value ?? ''} onChange={(e) => onChange(e.target.value)} disabled={disabled || loading || Boolean(loadError)}>
      <option value="">{loading ? `Loading ${label.toLowerCase()}...` : loadError ? 'Unable to load options' : placeholder}</option>
      {options.map((option) => <option key={option.id} value={option.id}>{option.name || option.label || option.title}</option>)}
    </select>
    {loadError && <button type="button" className={styles.retryButton} onClick={load}>Try again</button>}
    {(error || loadError) && <span className={styles.error}>{error || loadError}</span>}
  </div>
}

export default LookupSelect
