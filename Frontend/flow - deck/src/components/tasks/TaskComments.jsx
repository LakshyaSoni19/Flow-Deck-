import { useState } from 'react'
import Button from '../common/Button'
import ConfirmDialog from '../common/ConfirmDialog'
import EmptyState from '../common/EmptyState'
import Loader from '../common/Loader'
import { getUserDisplayName } from '../../utils/taskNormalization'
import styles from './TaskComments.module.css'

const isOwnComment = (comment, user) => {
  if (!comment || !user) return false
  if (comment.isOwn !== undefined) return Boolean(comment.isOwn)
  if (comment.own !== undefined) return Boolean(comment.own)
  if (user.id && (comment.userId === user.id || comment.authorId === user.id || comment.user?.id === user.id)) return true
  if (user.email && (comment.userEmail === user.email || comment.email === user.email || comment.createdBy === user.email || comment.user?.email === user.email)) return true
  if (user.username && (comment.userName === user.username || comment.username === user.username || comment.createdBy === user.username)) return true
  if (user.name && (comment.userName === user.name || comment.authorName === user.name)) return true
  return false
}

const formatAuthor = (comment) =>
  comment?.userName || comment?.authorName || getUserDisplayName(comment?.user) || comment?.userEmail || comment?.email || 'Team Member'

const formatTimestamp = (comment) => {
  const raw = comment?.commentDate || comment?.createdAt || comment?.timestamp || comment?.date
  if (!raw) return ''
  try {
    return new Date(raw).toLocaleString()
  } catch {
    return String(raw)
  }
}

export default function TaskComments({
  comments = [],
  isLoading = false,
  error = '',
  currentUser = null,
  onRefresh,
  onAddComment,
  onUpdateComment,
  onDeleteComment,
}) {
  const [newComment, setNewComment] = useState('')
  const [isAdding, setIsAdding] = useState(false)

  const [editingId, setEditingId] = useState(null)
  const [editingText, setEditingText] = useState('')
  const [isSavingEdit, setIsSavingEdit] = useState(false)

  const [pendingDelete, setPendingDelete] = useState(null)
  const [isDeleting, setIsDeleting] = useState(false)

  const handleAddSubmit = async (event) => {
    event.preventDefault()
    const trimmed = newComment.trim()
    if (!trimmed) return

    setIsAdding(true)
    try {
      await onAddComment(trimmed)
      setNewComment('')
    } finally {
      setIsAdding(false)
    }
  }

  const startEditing = (comment) => {
    setEditingId(comment.id)
    setEditingText(comment.comment || comment.content || comment.text || '')
  }

  const handleEditSubmit = async (event) => {
    event.preventDefault()
    const trimmed = editingText.trim()
    if (!trimmed || !editingId) return

    setIsSavingEdit(true)
    try {
      await onUpdateComment(editingId, trimmed)
      setEditingId(null)
      setEditingText('')
    } finally {
      setIsSavingEdit(false)
    }
  }

  const handleDeleteConfirm = async () => {
    if (!pendingDelete) return
    setIsDeleting(true)
    try {
      await onDeleteComment(pendingDelete.id)
      setPendingDelete(null)
    } finally {
      setIsDeleting(false)
    }
  }

  return (
    <div className={styles.container}>
      <h3 className={styles.title}>Task Discussion ({comments.length})</h3>

      <form onSubmit={handleAddSubmit} className={styles.addForm}>
        <textarea
          className={styles.textarea}
          placeholder="Write a comment or post an update on this assigned task..."
          value={newComment}
          onChange={(event) => setNewComment(event.target.value)}
          disabled={isAdding}
        />
        <div className={styles.addActions}>
          <Button type="submit" isLoading={isAdding} disabled={!newComment.trim() || isAdding}>
            Post Comment
          </Button>
        </div>
      </form>

      {isLoading && (
        <div className={styles.loading}>
          <Loader label="Loading task comments" />
        </div>
      )}

      {error && !isLoading && (
        <EmptyState
          title="Comments Unavailable"
          description={error}
          action={
            onRefresh ? <Button onClick={onRefresh}>Try again</Button> : null
          }
        />
      )}

      {!isLoading && !error && (
        <div className={styles.commentList}>
          {comments.length === 0 ? (
            <EmptyState
              title="No Comments Yet"
              description="There are no discussion comments on this task. Be the first to comment!"
            />
          ) : (
            comments.map((comment) => {
              const isOwn = isOwnComment(comment, currentUser)
              const isEditing = editingId === comment.id
              const author = formatAuthor(comment)
              const initial = author.charAt(0).toUpperCase() || 'M'
              const time = formatTimestamp(comment)

              return (
                <div key={comment.id} className={styles.commentItem}>
                  <div className={styles.commentHeader}>
                    <div className={styles.authorMeta}>
                      <span className={styles.authorAvatar}>{initial}</span>
                      <span className={styles.authorName}>{author}</span>
                      {isOwn && <span className={styles.badgeOwn}>You</span>}
                      {time && <span className={styles.timestamp}>• {time}</span>}
                    </div>
                    {isOwn && !isEditing && (
                      <div className={styles.itemActions}>
                        <Button
                          variant="ghost"
                          onClick={() => startEditing(comment)}
                          aria-label="Edit comment"
                        >
                          Edit
                        </Button>
                        <Button
                          variant="ghost"
                          onClick={() => setPendingDelete(comment)}
                          aria-label="Delete comment"
                        >
                          Delete
                        </Button>
                      </div>
                    )}
                  </div>

                  {isEditing ? (
                    <form onSubmit={handleEditSubmit} className={styles.editForm}>
                      <textarea
                        className={styles.textarea}
                        value={editingText}
                        onChange={(event) => setEditingText(event.target.value)}
                        disabled={isSavingEdit}
                      />
                      <div className={styles.editActions}>
                        <Button
                          type="button"
                          variant="ghost"
                          onClick={() => setEditingId(null)}
                          disabled={isSavingEdit}
                        >
                          Cancel
                        </Button>
                        <Button
                          type="submit"
                          isLoading={isSavingEdit}
                          disabled={!editingText.trim() || isSavingEdit}
                        >
                          Save Changes
                        </Button>
                      </div>
                    </form>
                  ) : (
                    <p className={styles.commentBody}>
                      {comment.comment || comment.content || comment.text || ''}
                    </p>
                  )}
                </div>
              )
            })
          )}
        </div>
      )}

      <ConfirmDialog
        isOpen={Boolean(pendingDelete)}
        onClose={() => !isDeleting && setPendingDelete(null)}
        onConfirm={handleDeleteConfirm}
        title="Delete Comment"
        description="Are you sure you want to delete this comment? This action cannot be undone."
        confirmLabel="Delete Comment"
        isLoading={isDeleting}
      />
    </div>
  )
}
