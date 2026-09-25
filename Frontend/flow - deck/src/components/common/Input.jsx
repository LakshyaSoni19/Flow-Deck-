import { forwardRef } from 'react'
import styles from './ui.module.css'

const Input = forwardRef(function Input({ id, label, error, className = '', ...props }, ref) {
  return (
    <div className={styles.field}>
      {label && <label htmlFor={id}>{label}</label>}
      <input ref={ref} id={id} className={`${styles.input} ${error ? styles.inputError : ''} ${className}`.trim()} {...props} />
      {error && <span className={styles.error}>{error}</span>}
    </div>
  )
})

export default Input
