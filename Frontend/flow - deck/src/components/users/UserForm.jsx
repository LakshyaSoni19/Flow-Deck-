import { useState } from 'react'
import Button from '../common/Button'
import FormLayout from '../common/FormLayout'
import Input from '../common/Input'
import LookupSelect from '../common/LookupSelect'
import { lookupService } from '../../services/lookupService'
import styles from './UserForm.module.css'

const initialValues = (user) => ({
  firstName: user?.firstName || '',
  lastName: user?.lastName || '',
  mobile: user?.mobile || '',
  gender: user?.gender || '',
  dob: user?.dob || '',
  address: user?.address || '',
  profileImage: user?.profileImage || '',
  cityId: user?.cityId ?? user?.city?.id ?? '',
  departmentId: user?.departmentId ?? user?.department?.id ?? '',
  designationId: user?.designationId ?? user?.designation?.id ?? '',
})

function UserForm({ user, isSaving, onSubmit }) {
  const [values, setValues] = useState(initialValues(user))
  const [errors, setErrors] = useState({})

  const update = (event) =>
    setValues({ ...values, [event.target.name]: event.target.value })

  const submit = (event) => {
    event.preventDefault()
    const nextErrors = {
      firstName: !values.firstName.trim()
        ? 'First name is required.'
        : values.firstName.length > 45
          ? 'First name must be at most 45 characters.'
          : '',
      lastName: !values.lastName.trim()
        ? 'Last name is required.'
        : values.lastName.length > 45
          ? 'Last name must be at most 45 characters.'
          : '',
      mobile:
        values.mobile && !/^\d{10}$/.test(values.mobile)
          ? 'Mobile number must contain exactly 10 digits.'
          : '',
      gender: !values.gender ? 'Gender is required.' : '',
      dob: !values.dob ? 'Date of birth is required.' : '',
      address: !values.address.trim() ? 'Address is required.' : '',
      cityId:
        !/^\d+$/.test(values.cityId) || Number(values.cityId) < 1
          ? 'Select a city.'
          : '',
      departmentId:
        !/^\d+$/.test(values.departmentId) || Number(values.departmentId) < 1
          ? 'Select a department.'
          : '',
      designationId:
        !/^\d+$/.test(values.designationId) || Number(values.designationId) < 1
          ? 'Select a designation.'
          : '',
    }

    setErrors(nextErrors)

    if (!Object.values(nextErrors).some(Boolean)) {
      onSubmit(
        {
          ...values,
          firstName: values.firstName.trim(),
          lastName: values.lastName.trim(),
          address: values.address.trim(),
          cityId: Number(values.cityId),
          departmentId: Number(values.departmentId),
          designationId: Number(values.designationId),
        },
        (fields) => setErrors((current) => ({ ...current, ...fields })),
      )
    }
  }

  return (
    <FormLayout onSubmit={submit} noValidate>
      <div className={styles.grid}>
        <Input
          id="firstName"
          name="firstName"
          label="First Name"
          placeholder="e.g. John"
          value={values.firstName}
          onChange={update}
          error={errors.firstName}
        />
        <Input
          id="lastName"
          name="lastName"
          label="Last Name"
          placeholder="e.g. Doe"
          value={values.lastName}
          onChange={update}
          error={errors.lastName}
        />
      </div>

      <div className={styles.grid}>
        <Input
          id="mobile"
          name="mobile"
          label="Mobile Number"
          placeholder="e.g. 9876543210"
          inputMode="numeric"
          value={values.mobile}
          onChange={update}
          error={errors.mobile}
        />
        <div className={styles.selectField}>
          <label htmlFor="gender">Gender</label>
          <select id="gender" name="gender" value={values.gender} onChange={update}>
            <option value="">Select gender</option>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
          </select>
          {errors.gender && <span>{errors.gender}</span>}
        </div>
      </div>

      <Input
        id="dob"
        name="dob"
        type="date"
        label="Date of Birth"
        value={values.dob}
        onChange={update}
        error={errors.dob}
      />

      <Input
        id="address"
        name="address"
        label="Address"
        placeholder="Enter street address"
        value={values.address}
        onChange={update}
        error={errors.address}
      />

      <Input
        id="profileImage"
        name="profileImage"
        type="url"
        label="Profile Image URL"
        placeholder="https://example.com/avatar.jpg"
        value={values.profileImage}
        onChange={update}
        error={errors.profileImage}
      />

      <div className={styles.grid}>
        <LookupSelect
          id="cityId" label="City"
          value={values.cityId}
          onChange={(value) => setValues({ ...values, cityId: value })}
          loadOptions={lookupService.getCities}
          error={errors.cityId}
        />
        <LookupSelect
          id="departmentId" label="Department"
          value={values.departmentId}
          onChange={(value) => setValues({ ...values, departmentId: value })}
          loadOptions={lookupService.getDepartments}
          error={errors.departmentId}
        />
      </div>

      <LookupSelect
        id="designationId" label="Designation"
        value={values.designationId}
        onChange={(value) => setValues({ ...values, designationId: value })}
        loadOptions={lookupService.getDesignations}
        error={errors.designationId}
      />

      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
        <Button type="submit" isLoading={isSaving} disabled={isSaving}>
          {user ? 'Save User Profile' : 'Create User'}
        </Button>
      </div>
    </FormLayout>
  )
}

export default UserForm
