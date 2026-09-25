import styles from './ui.module.css'

function Button({ children, className = '', variant = 'primary', isLoading = false, type = 'button', disabled = false, ...props }) {
  const classes = `${styles.button} ${styles[variant]} ${className}`.trim()

  return (
    <button className={classes} type={type} disabled={isLoading || disabled} {...props}>
      {isLoading && <span className={styles.buttonLoader} aria-hidden="true" />}
      {children}
    </button>
  )
}

export default Button
