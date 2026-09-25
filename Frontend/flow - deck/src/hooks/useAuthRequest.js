import { useState } from 'react'
import toast from 'react-hot-toast'
import { getApiError } from '../utils/apiError'

export function useAuthRequest() {
  const [isSubmitting, setIsSubmitting] = useState(false)

  const execute = async (request, { onSuccess, setErrors }) => {
    setIsSubmitting(true)
    try {
      const response = await request()
      if (!response?.success) throw new Error(response?.message || 'The request could not be completed.')
      toast.success(response.message)
      onSuccess?.(response.data)
      return response.data
    } catch (error) {
      const { message, fields } = getApiError(error)
      setErrors?.(fields)
      toast.error(message)
      return null
    } finally {
      setIsSubmitting(false)
    }
  }

  return { execute, isSubmitting }
}
