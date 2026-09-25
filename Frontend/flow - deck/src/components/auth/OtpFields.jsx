import Input from '../common/Input'
import styles from './OtpFields.module.css'

function OtpFields({ values, errors, onChange, includePurpose = true, includeOtp = true }) {
  return <>{includePurpose && <div className={styles.field}><label htmlFor="purpose">Purpose</label><select id="purpose" name="purpose" value={values.purpose} onChange={onChange}><option value="FORGOT_PASSWORD">Forgot password</option><option value="VERIFICATION">Verification</option></select>{errors.purpose && <span>{errors.purpose}</span>}</div>}<Input id="email" name="email" type="email" label="Email" value={values.email} onChange={onChange} error={errors.email} />{includeOtp && <Input id="otp" name="otp" inputMode="numeric" maxLength="6" label="OTP" value={values.otp} onChange={onChange} error={errors.otp} />}</>
}
export default OtpFields
