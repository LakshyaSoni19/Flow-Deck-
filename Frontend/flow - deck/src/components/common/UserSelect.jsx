import { useEffect, useState } from 'react'
import Input from './Input'
import styles from './ui.module.css'

const items = (response) => { const data = response?.data ?? response; return Array.isArray(data) ? data : data?.content || [] }
const nameOf = (user) => user.name || [user.firstName, user.lastName].filter(Boolean).join(' ') || user.email

export default function UserSelect({ id, label = 'User', selectedUser, onSelect, searchUsers, error, disabled }) {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [loadError, setLoadError] = useState('')
  useEffect(() => {
    const term = query.trim()
    if (!term) return undefined
    const timer = setTimeout(async () => {
      setLoading(true); setLoadError('')
      try { setResults(items(await searchUsers(term))) } catch { setLoadError('Unable to load users. Please try again.') } finally { setLoading(false) }
    }, 300)
    return () => clearTimeout(timer)
  }, [query, searchUsers])
  return <div className={styles.userSelect}>
    <Input id={id} label={label} value={query} disabled={disabled} onChange={(e) => { setQuery(e.target.value); onSelect(null) }} placeholder={selectedUser ? nameOf(selectedUser) : 'Search by name or email'} error={error} />
    {selectedUser && <div className={styles.selectedUser}>{nameOf(selectedUser)}{selectedUser.email && <small>{selectedUser.email}</small>}</div>}
    {query.trim() && <div className={styles.userResults} role="listbox" aria-label={`${label} results`}>
      {loading ? <span>Loading users...</span> : loadError ? <span>{loadError}</span> : results.length ? results.map((user) => <button type="button" key={user.id} onClick={() => { onSelect(user); setQuery(''); setResults([]) }}><strong>{nameOf(user)}</strong>{user.email && <small>{user.email}</small>}</button>) : <span>No users found.</span>}
    </div>}
  </div>
}
