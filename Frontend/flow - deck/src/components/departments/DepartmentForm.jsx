import { useState } from 'react'
import Button from '../common/Button'
import FormLayout from '../common/FormLayout'
import Input from '../common/Input'

function DepartmentForm({ department, isSaving, onSubmit }) {
  const [name, setName] = useState(department?.name || '')
  const [error, setError] = useState('')

  const submit = (event) => {
    event.preventDefault()
    const trimmedName = name.trim()
    const nextError = !trimmedName
      ? 'Department name is required.'
      : trimmedName.length > 45
        ? 'Department name must be at most 45 characters.'
        : ''
    setError(nextError)
    if (!nextError) {
      onSubmit({ name: trimmedName }, (message) => setError(message))
    }
  }

  return (
    <FormLayout onSubmit={submit} noValidate>
      <div>
        <Input
          id="departmentName"
          label="Department Name"
          placeholder="e.g. Product Engineering"
          value={name}
          onChange={(event) => setName(event.target.value)}
          error={error}
          maxLength="45"
        />
        <span style={{ display: 'block', marginTop: '0.25rem', fontSize: '0.75rem', color: 'var(--color-text-muted)', textAlign: 'right' }}>
          {name.length}/45 characters
        </span>
      </div>

      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
        <Button type="submit" isLoading={isSaving} disabled={isSaving}>
          {department ? 'Save Changes' : 'Create Department'}
        </Button>
      </div>
    </FormLayout>
  )
}

export default DepartmentForm

