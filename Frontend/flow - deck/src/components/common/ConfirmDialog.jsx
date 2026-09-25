import Button from './Button'
import Modal from './Modal'

function ConfirmDialog({ isOpen, onClose, onConfirm, title = 'Confirm action', description, confirmLabel = 'Confirm', isLoading = false }) {
  return <Modal isOpen={isOpen} onClose={onClose} title={title} footer={<><Button variant="secondary" onClick={onClose}>Cancel</Button><Button onClick={onConfirm} isLoading={isLoading}>{confirmLabel}</Button></>}><p>{description}</p></Modal>
}
export default ConfirmDialog
