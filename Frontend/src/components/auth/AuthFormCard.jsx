import styles from './AuthFormCard.module.css'

function AuthFormCard({ title, description, children, footer }) {
  return <section className={styles.card}><header><h1>{title}</h1>{description && <p>{description}</p>}</header>{children}{footer && <footer>{footer}</footer>}</section>
}
export default AuthFormCard
