import { createPortal } from 'react-dom'
import Button from './Button'
import styles from './ui.module.css'

function Modal({ isOpen, onClose, title, children, footer }) {
  if (!isOpen) return null

  return createPortal(
    <div className={styles.modalBackdrop} role="presentation" onMouseDown={onClose}>
      <section className={styles.modal} role="dialog" aria-modal="true" aria-labelledby="modal-title" onMouseDown={(event) => event.stopPropagation()}>
        <header className={styles.modalHeader}>
          <h2 id="modal-title">{title}</h2>
          <Button variant="ghost" aria-label="Close modal" onClick={onClose}>Close</Button>
        </header>
        <div className={styles.modalBody}>{children}</div>
        {footer && <footer className={styles.modalFooter}>{footer}</footer>}
      </section>
    </div>,
    document.body,
  )
}

export default Modal
