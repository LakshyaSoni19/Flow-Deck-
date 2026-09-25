import { useEffect, useState } from 'react'
import Button from '../common/Button'
import FormLayout from '../common/FormLayout'
import LookupSelect from '../common/LookupSelect'
import { pmService } from '../../services/pmService'
import { getProjectMemberId, getProjectMemberUser, getUserDisplayName } from '../../utils/taskNormalization'

const unpack = (response) => {
  const data = response?.data ?? response
  return Array.isArray(data) ? data : data?.content || []
}

export default function TaskActionSelect({ label, projectId, loadOptions, isSaving, onSubmit, memberOptions = false }) {
  const [value, setValue] = useState('')
  const [members, setMembers] = useState([])
  const [loading, setLoading] = useState(memberOptions)
  const [error, setError] = useState('')

  const loadMembers = async () => {
    setLoading(true)
    setError('')
    try {
      setMembers(unpack(await pmService.getProjectMembers(projectId)))
    } catch {
      setError('Unable to load project members. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  // eslint-disable-next-line react-hooks/set-state-in-effect, react-hooks/exhaustive-deps
  useEffect(() => { if (memberOptions) void loadMembers() }, [projectId, memberOptions])

  const submit = (event) => {
    event.preventDefault()
    if (!value) {
      setError(`Select ${label.toLowerCase()}.`)
      return
    }
    onSubmit(Number(value))
  }

  if (!memberOptions) {
    return <FormLayout onSubmit={submit}><LookupSelect id="taskAction" label={label} value={value} onChange={setValue} loadOptions={loadOptions} error={error} disabled={isSaving} /><div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem' }}><Button type="submit" isLoading={isSaving}>Save Action</Button></div></FormLayout>
  }

  return <FormLayout onSubmit={submit}><div><label htmlFor="taskAction">{label}</label><select id="taskAction" value={value} onChange={(event) => setValue(event.target.value)} disabled={loading || isSaving || !!error}><option value="">{loading ? 'Loading project members...' : 'Select a project member'}</option>{members.map((member) => { const user = getProjectMemberUser(member); const userId = getProjectMemberId(member); return <option key={userId} value={userId}>{getUserDisplayName(user)}</option> })}</select>{error && <span>{error}</span>}</div><div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem' }}><Button type="submit" isLoading={isSaving}>Save Action</Button></div></FormLayout>
}
