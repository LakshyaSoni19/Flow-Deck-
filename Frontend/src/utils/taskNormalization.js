const nonEmptyString = (value) =>
  typeof value === 'string' && value.trim() ? value.trim() : ''

export const getUserDisplayName = (user) => {
  if (!user || typeof user !== 'object') return ''

  const fullName = [user.firstName, user.lastName]
    .map(nonEmptyString)
    .filter(Boolean)
    .join(' ')

  return fullName || nonEmptyString(user.name) || nonEmptyString(user.userName) || nonEmptyString(user.email)
}

export const getProjectMemberUser = (member) => member?.user || member || null

export const getProjectMemberId = (member) => {
  const user = getProjectMemberUser(member)
  return user?.id ?? member?.userId ?? null
}

export const normalizeTask = (task) => {
  if (!task || typeof task !== 'object') return task

  // TaskResponse exposes the assignee as `assignedTo` (UserResponse).
  // Older aliases remain supported for previously loaded/API-versioned records.
  const assignedUser = task.assignedTo ?? task.assignedUser ?? task.assignee ?? null
  const assignedUserId = task.assignedUserId ?? assignedUser?.id ?? null
  const assignedUserName = nonEmptyString(task.assignedUserName) || getUserDisplayName(assignedUser)

  return { ...task, assignedUser, assignedUserId, assignedUserName }
}

export const normalizeTaskData = (data) => {
  if (Array.isArray(data)) return data.map(normalizeTask)
  if (data?.content && Array.isArray(data.content)) {
    return { ...data, content: data.content.map(normalizeTask) }
  }
  return normalizeTask(data)
}

export const normalizeTaskResponse = (response) => (
  response && typeof response === 'object'
    ? { ...response, data: normalizeTaskData(response.data) }
    : response
)

export const getAssigneeLabel = (task) => {
  const normalized = normalizeTask(task)
  if (normalized?.assignedUserName) return normalized.assignedUserName
  return normalized?.assignedUserId !== null && normalized?.assignedUserId !== undefined
    ? 'Assigned user unavailable'
    : 'Unassigned'
}
