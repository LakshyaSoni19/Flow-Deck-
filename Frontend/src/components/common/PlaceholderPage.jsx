import styles from './PlaceholderPage.module.css'

function PlaceholderPage({ title, description }) {
  return (
    <section className={styles.page} aria-labelledby="page-title">
      <p className={styles.eyebrow}>Flow Deck</p>
      <h1 id="page-title">{title}</h1>
      <p>{description}</p>
    </section>
  )
}

export default PlaceholderPage
