import { useCallback, useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import Button from '../../components/common/Button'
import Card from '../../components/common/Card'
import EmptyState from '../../components/common/EmptyState'
import FormLayout from '../../components/common/FormLayout'
import Input from '../../components/common/Input'
import Loader from '../../components/common/Loader'
import Modal from '../../components/common/Modal'
import PageHeader from '../../components/common/PageHeader'
import { employeeService } from '../../services/employeeService'
import { getApiError } from '../../utils/apiError'
import { hasErrors } from '../../utils/validators'
import styles from './ProfilePage.module.css'

const extractData = (response) => {
  if (!response?.success) {
    throw new Error(response?.message || 'The request could not be completed.')
  }
  return response.data
}

const display = (value) => (value !== null && value !== undefined && value !== '' ? value : 'Not available')

const initialProfileValues = (profile) => ({
  firstName: profile?.firstName || '',
  lastName: profile?.lastName || '',
  mobile: profile?.mobile || '',
  gender: profile?.gender || '',
  dob: profile?.dob || '',
  address: profile?.address || '',
  profileImage: profile?.profileImage || '',
})

const validateProfileForm = (values) => {
  const errors = {}
  if (!values.firstName?.trim()) {
    errors.firstName = 'First name is required.'
  } else if (values.firstName.length > 45) {
    errors.firstName = 'First name must be at most 45 characters.'
  }

  if (!values.lastName?.trim()) {
    errors.lastName = 'Last name is required.'
  } else if (values.lastName.length > 45) {
    errors.lastName = 'Last name must be at most 45 characters.'
  }

  if (values.mobile && !/^\d{10}$/.test(values.mobile)) {
    errors.mobile = 'Mobile number must contain exactly 10 digits.'
  }

  return errors
}

const validatePasswordForm = (values) => {
  const errors = {}
  if (!values.currentPassword) {
    errors.currentPassword = 'Current password is required.'
  }

  if (!values.newPassword) {
    errors.newPassword = 'New password is required.'
  } else if (values.newPassword.length < 6 || values.newPassword.length > 45) {
    errors.newPassword = 'New password must be between 6 and 45 characters.'
  }

  if (!values.confirmPassword) {
    errors.confirmPassword = 'Please confirm your new password.'
  } else if (values.confirmPassword !== values.newPassword) {
    errors.confirmPassword = 'Passwords do not match.'
  }

  return errors
}

export default function ProfilePage() {
  const [profile, setProfile] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')

  const [modalMode, setModalMode] = useState(null) // 'editProfile' | 'changePassword' | null

  // Profile Edit State
  const [profileValues, setProfileValues] = useState(initialProfileValues(null))
  const [profileErrors, setProfileErrors] = useState({})
  const [isSavingProfile, setIsSavingProfile] = useState(false)

  // Change Password State
  const [passwordValues, setPasswordValues] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  })
  const [passwordErrors, setPasswordErrors] = useState({})
  const [isChangingPassword, setIsChangingPassword] = useState(false)

  const loadProfile = useCallback(async () => {
    setIsLoading(true)
    setError('')
    try {
      const response = await employeeService.getProfile()
      const data = extractData(response)
      setProfile(data)
    } catch (requestError) {
      const apiError = getApiError(requestError)
      setError(apiError.message)
      toast.error(apiError.message)
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    void Promise.resolve().then(loadProfile)
  }, [loadProfile])

  const openEditProfile = () => {
    setProfileValues(initialProfileValues(profile))
    setProfileErrors({})
    setModalMode('editProfile')
  }

  const openChangePassword = () => {
    setPasswordValues({ currentPassword: '', newPassword: '', confirmPassword: '' })
    setPasswordErrors({})
    setModalMode('changePassword')
  }

  const handleProfileChange = (event) => {
    const { name, value } = event.target
    setProfileValues((current) => ({ ...current, [name]: value }))
    if (profileErrors[name]) {
      setProfileErrors((current) => ({ ...current, [name]: '' }))
    }
  }

  const handlePasswordChange = (event) => {
    const { name, value } = event.target
    setPasswordValues((current) => ({ ...current, [name]: value }))
    if (passwordErrors[name]) {
      setPasswordErrors((current) => ({ ...current, [name]: '' }))
    }
  }

  const handleProfileSubmit = async (event) => {
    event.preventDefault()
    const nextErrors = validateProfileForm(profileValues)
    setProfileErrors(nextErrors)
    if (hasErrors(nextErrors)) return

    setIsSavingProfile(true)
    try {
      const response = await employeeService.updateProfile(profileValues)
      const updated = extractData(response)
      setProfile(updated)
      toast.success(response?.message || 'Profile updated successfully!')
      setModalMode(null)
    } catch (requestError) {
      const apiError = getApiError(requestError)
      if (apiError.fields) {
        setProfileErrors(apiError.fields)
      }
      toast.error(apiError.message)
    } finally {
      setIsSavingProfile(false)
    }
  }

  const handlePasswordSubmit = async (event) => {
    event.preventDefault()
    const nextErrors = validatePasswordForm(passwordValues)
    setPasswordErrors(nextErrors)
    if (hasErrors(nextErrors)) return

    setIsChangingPassword(true)
    try {
      const response = await employeeService.changePassword({
        currentPassword: passwordValues.currentPassword,
        newPassword: passwordValues.newPassword,
      })
      extractData(response)
      toast.success(response?.message || 'Password changed successfully!')
      setPasswordValues({ currentPassword: '', newPassword: '', confirmPassword: '' })
      setModalMode(null)
    } catch (requestError) {
      const apiError = getApiError(requestError)
      if (apiError.fields) {
        setPasswordErrors(apiError.fields)
      }
      toast.error(apiError.message)
    } finally {
      setIsChangingPassword(false)
    }
  }

  const fullName = profile
    ? `${profile.firstName || ''} ${profile.lastName || ''}`.trim() || profile.name || 'Employee'
    : 'Employee'
  const initial = fullName.charAt(0).toUpperCase() || 'E'
  const deptName = profile?.departmentName || profile?.department?.departmentName
  const desigName = profile?.designationName || profile?.designation?.designationName

  return (
    <>
      <PageHeader
        title="My Profile & Settings"
        description="Manage your personal information, organization details, and account security settings."
      />

      {isLoading && (
        <div className={styles.loading}>
          <Loader label="Loading profile information" />
        </div>
      )}

      {error && !isLoading && (
        <EmptyState
          title="Profile Unavailable"
          description={error}
          action={<Button onClick={loadProfile}>Try again</Button>}
        />
      )}

      {!isLoading && !error && profile && (
        <div className={styles.layout}>
          <section className={styles.profileHero}>
            {profile.profileImage ? (
              <img src={profile.profileImage} alt={fullName} className={styles.avatar} />
            ) : (
              <div className={styles.avatarFallback}>{initial}</div>
            )}
            <div className={styles.heroContent}>
              <h2 className={styles.name}>{fullName}</h2>
              <div className={styles.heroMeta}>
                <span className={styles.roleBadge}>
                  {profile.roleName || profile.roles?.[0] || 'Employee'}
                </span>
                {deptName && <span className={styles.deptBadge}>{deptName}</span>}
                {desigName && <span className={styles.deptBadge}>{desigName}</span>}
              </div>
              <p className={styles.email}>{display(profile.email)}</p>
            </div>
            <div className={styles.heroActions}>
              <Button onClick={openEditProfile}>Update Profile</Button>
            </div>
          </section>

          <Card>
            <div style={{ marginBottom: '1rem', paddingBottom: '0.75rem', borderBottom: '1px solid var(--color-border)' }}>
              <h3 style={{ margin: 0, fontSize: '1.125rem', fontWeight: 800 }}>Personal & Professional Information</h3>
            </div>
            <dl className={styles.detailsGrid}>
              <div>
                <dt>First Name</dt>
                <dd>{display(profile.firstName)}</dd>
              </div>
              <div>
                <dt>Last Name</dt>
                <dd>{display(profile.lastName)}</dd>
              </div>
              <div>
                <dt>Email Address</dt>
                <dd>{display(profile.email)}</dd>
              </div>
              <div>
                <dt>Mobile Number</dt>
                <dd>{display(profile.mobile)}</dd>
              </div>
              <div>
                <dt>Gender</dt>
                <dd>{display(profile.gender)}</dd>
              </div>
              <div>
                <dt>Date of Birth</dt>
                <dd>{display(profile.dob)}</dd>
              </div>
              <div>
                <dt>Department</dt>
                <dd>{display(deptName)}</dd>
              </div>
              <div>
                <dt>Designation</dt>
                <dd>{display(desigName)}</dd>
              </div>
              <div style={{ gridColumn: '1 / -1' }}>
                <dt>Address</dt>
                <dd style={{ lineHeight: 1.5 }}>{display(profile.address)}</dd>
              </div>
            </dl>
          </Card>

          <Card>
            <div className={styles.securityCard}>
              <div className={styles.securityInfo}>
                <h3>Account Security & Password</h3>
                <p>Keep your account secure by regularly updating your password with a strong, unique value.</p>
              </div>
              <Button variant="secondary" onClick={openChangePassword}>
                Change Password
              </Button>
            </div>
          </Card>
        </div>
      )}

      {modalMode === 'editProfile' && (
        <Modal
          isOpen
          title="Update Profile Information"
          onClose={() => !isSavingProfile && setModalMode(null)}
        >
          <FormLayout onSubmit={handleProfileSubmit} noValidate>
            <div className={styles.gridTwo}>
              <Input
                id="firstName"
                name="firstName"
                label="First Name"
                placeholder="e.g. John"
                value={profileValues.firstName}
                onChange={handleProfileChange}
                error={profileErrors.firstName}
                disabled={isSavingProfile}
              />
              <Input
                id="lastName"
                name="lastName"
                label="Last Name"
                placeholder="e.g. Doe"
                value={profileValues.lastName}
                onChange={handleProfileChange}
                error={profileErrors.lastName}
                disabled={isSavingProfile}
              />
            </div>

            <div className={styles.gridTwo}>
              <Input
                id="mobile"
                name="mobile"
                label="Mobile Number"
                placeholder="e.g. 9876543210"
                inputMode="numeric"
                value={profileValues.mobile}
                onChange={handleProfileChange}
                error={profileErrors.mobile}
                disabled={isSavingProfile}
              />
              <div className={styles.selectField}>
                <label htmlFor="gender">Gender</label>
                <select
                  id="gender"
                  name="gender"
                  value={profileValues.gender}
                  onChange={handleProfileChange}
                  disabled={isSavingProfile}
                >
                  <option value="">Select Gender</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
                {profileErrors.gender && (
                  <span className={styles.error}>{profileErrors.gender}</span>
                )}
              </div>
            </div>

            <Input
              id="dob"
              name="dob"
              type="date"
              label="Date of Birth"
              value={profileValues.dob}
              onChange={handleProfileChange}
              error={profileErrors.dob}
              disabled={isSavingProfile}
            />

            <Input
              id="address"
              name="address"
              label="Address"
              placeholder="Enter your current location or street address"
              value={profileValues.address}
              onChange={handleProfileChange}
              error={profileErrors.address}
              disabled={isSavingProfile}
            />

            <Input
              id="profileImage"
              name="profileImage"
              type="url"
              label="Profile Image URL"
              placeholder="https://example.com/avatar.jpg"
              value={profileValues.profileImage}
              onChange={handleProfileChange}
              error={profileErrors.profileImage}
              disabled={isSavingProfile}
            />

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
              <Button type="submit" isLoading={isSavingProfile} disabled={isSavingProfile}>
                Save Profile Changes
              </Button>
            </div>
          </FormLayout>
        </Modal>
      )}

      {modalMode === 'changePassword' && (
        <Modal
          isOpen
          title="Change Account Password"
          onClose={() => !isChangingPassword && setModalMode(null)}
        >
          <FormLayout onSubmit={handlePasswordSubmit} noValidate>
            <div style={{ padding: '0.75rem', borderRadius: '6px', background: 'var(--color-surface-muted)', border: '1px solid var(--color-border-subtle)', fontSize: '0.8125rem', color: 'var(--color-text-secondary)', marginBottom: '0.5rem' }}>
              Your new password must be between 6 and 45 characters long.
            </div>

            <Input
              id="currentPassword"
              name="currentPassword"
              type="password"
              label="Current Password"
              placeholder="Enter current password"
              value={passwordValues.currentPassword}
              onChange={handlePasswordChange}
              error={passwordErrors.currentPassword}
              disabled={isChangingPassword}
              autoComplete="current-password"
            />

            <Input
              id="newPassword"
              name="newPassword"
              type="password"
              label="New Password"
              placeholder="Enter new password"
              value={passwordValues.newPassword}
              onChange={handlePasswordChange}
              error={passwordErrors.newPassword}
              disabled={isChangingPassword}
              autoComplete="new-password"
            />

            <Input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              label="Confirm New Password"
              placeholder="Re-enter new password"
              value={passwordValues.confirmPassword}
              onChange={handlePasswordChange}
              error={passwordErrors.confirmPassword}
              disabled={isChangingPassword}
              autoComplete="new-password"
            />

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1rem' }}>
              <Button type="submit" isLoading={isChangingPassword} disabled={isChangingPassword}>
                Update Password
              </Button>
            </div>
          </FormLayout>
        </Modal>
      )}
    </>
  )
}

