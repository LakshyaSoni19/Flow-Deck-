import styles from './architecture.module.css'

function FormLayout({ children, onSubmit, className = '', ...props }) { return <form className={`${styles.form} ${className}`.trim()} onSubmit={onSubmit} {...props}>{children}</form> }
export default FormLayout
