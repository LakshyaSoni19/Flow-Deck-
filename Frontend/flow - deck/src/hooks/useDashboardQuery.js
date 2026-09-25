import { useCallback, useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { getApiError } from '../utils/apiError'

export function useDashboardQuery(query, enabled = true) {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(enabled)

  const refresh = useCallback(async () => {
    if (!enabled) { setData(null); setError(''); setIsLoading(false); return }
    setIsLoading(true); setError('')
    try {
      const response = await query()
      if (!response?.success) throw new Error(response?.message || 'Unable to load dashboard data.')
      setData(response.data)
    } catch (requestError) {
      const apiError = getApiError(requestError)
      setError(apiError.message)
      toast.error(apiError.message)
    } finally { setIsLoading(false) }
  }, [enabled, query])

  useEffect(() => { void Promise.resolve().then(refresh) }, [refresh])
  return { data, error, isLoading, refresh }
}
