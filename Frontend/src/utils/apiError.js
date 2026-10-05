export function getApiError(error) {
  const payload = error.response?.data
  return {
    message: payload?.message || error.message || 'Something went wrong. Please try again.',
    fields: payload?.data && typeof payload.data === 'object' ? payload.data : {},
  }
}
