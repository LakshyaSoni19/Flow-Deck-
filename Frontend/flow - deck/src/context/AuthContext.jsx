import { useMemo, useState } from 'react'
import { AuthContext } from './authContextValue'
import { clearAuthSession, getAuthSession, setAuthSession } from '../utils/authStorage'

export function AuthProvider({ children }) {
  const [session, setSession] = useState(getAuthSession)

  const value = useMemo(
    () => ({
      user: session?.user || null,
      token: session?.token || null,
      isAuthenticated: Boolean(session?.token),
      roles: session?.user?.roles || [],
      startSession: (nextSession) => { setAuthSession(nextSession); setSession(nextSession) },
      endSession: () => { clearAuthSession(); setSession(null) },
    }),
    [session],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
