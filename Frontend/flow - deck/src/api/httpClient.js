import axios from 'axios'
import { BASE_URL } from './apiConfig'
import { AUTH_STORAGE_KEY } from '../constants/authConstants'
import { clearAuthSession } from '../utils/authStorage'

// Network calls and interceptors will be added during API integration.
export const httpClient = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

httpClient.interceptors.request.use((config) => {
  const storedSession = localStorage.getItem(AUTH_STORAGE_KEY)
  let session = null

  try {
    session = storedSession ? JSON.parse(storedSession) : null
  } catch {
    clearAuthSession()
  }

  if (session?.token) config.headers.Authorization = `Bearer ${session.token}`
  return config
})

httpClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearAuthSession()
      if (window.location.pathname !== '/login') {
        window.location.assign('/login')
      }
    }
    return Promise.reject(error)
  },
)
