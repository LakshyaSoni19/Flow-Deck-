import { useEffect, useState } from 'react'
import Button from '../common/Button'
import FormLayout from '../common/FormLayout'
import UserSelect from '../common/UserSelect'
import { adminService } from '../../services/adminService'
import styles from '../common/ui.module.css'

const unpack = (response) => { const data = response?.data ?? response; return Array.isArray(data) ? data : data?.content || [] }
function AssignRoleForm({ isSaving, onSubmit }) {
  const [user, setUser] = useState(null); const [roles, setRoles] = useState([]); const [roleId, setRoleId] = useState(''); const [errors, setErrors] = useState({}); const [loading, setLoading] = useState(true); const [loadError, setLoadError] = useState('')
  const loadRoles = async () => { setLoading(true); setLoadError(''); try { setRoles(unpack(await adminService.getRoles())) } catch { setLoadError('Unable to load roles. Please try again.') } finally { setLoading(false) } }
  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => { void loadRoles() }, [])
  const submit = (event) => { event.preventDefault(); const next = { userId: user ? '' : 'Select a user.', roleId: roleId ? '' : 'Select a role.' }; setErrors(next); if (!next.userId && !next.roleId) onSubmit({ userId: user.id, roleId: Number(roleId) }, setErrors) }
  return <FormLayout onSubmit={submit} noValidate><UserSelect id="roleUser" label="User" selectedUser={user} onSelect={(value) => { setUser(value); setErrors((e) => ({ ...e, userId: '' })) }} searchUsers={(query) => adminService.searchUsers({ query })} error={errors.userId} disabled={isSaving} /><div className={styles.field}><label htmlFor="roleId">Role</label><select id="roleId" className={`${styles.input} ${errors.roleId ? styles.inputError : ''}`} value={roleId} disabled={isSaving || loading || !!loadError} onChange={(event) => setRoleId(event.target.value)}><option value="">{loading ? 'Loading roles...' : loadError ? 'Unable to load roles' : 'Select a role'}</option>{roles.map((role) => <option key={role.id} value={role.id}>{role.name}</option>)}</select>{loadError && <button className={styles.retryButton} type="button" onClick={loadRoles}>Try again</button>}{(errors.roleId || loadError) && <span className={styles.error}>{errors.roleId || loadError}</span>}</div><div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem' }}><Button type="submit" isLoading={isSaving} disabled={isSaving || loading}>Assign Role</Button></div></FormLayout>
}
export default AssignRoleForm
