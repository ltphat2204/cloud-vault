import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import type { ReactNode } from 'react'
import api from '@/lib/axios'

interface User {
  id: string
  email: string
  name: string
}

interface AuthContextType {
  user: User | null
  isLoading: boolean
  login: (accessToken: string, user: User) => void
  logout: () => Promise<void>
  refetchUser: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  const logout = useCallback(async () => {
    try {
      const token = localStorage.getItem('access_token')
      if (token) {
        await api.post('/auth/logout')
      }
    } catch (error) {
      // Silently fail logout API call
    } finally {
      localStorage.removeItem('access_token')
      setUser(null)
      // Only redirect if we are not already on a public page to avoid loops
      const publicPaths = ['/', '/login', '/register', '/verify']
      if (!publicPaths.includes(window.location.pathname)) {
        window.location.href = '/'
      }
    }
  }, [])

  const refetchUser = useCallback(async () => {
    const token = localStorage.getItem('access_token')?.trim()
    
    // Check if token exists and isn't a stringified 'undefined' or 'null'
    if (!token || token === 'undefined' || token === 'null') {
      setUser(null)
      setIsLoading(false)
      return
    }

    setIsLoading(true)
    try {
      const response = await api.get('/auth/me')
      setUser(response.data.data)
    } catch (error: any) {
      await logout()
    } finally {
      setIsLoading(false)
    }
  }, [logout])

  useEffect(() => {
    refetchUser()
  }, [refetchUser])

  const login = (accessToken: string, newUser: User) => {
    localStorage.setItem('access_token', accessToken.trim())
    setUser(newUser)
  }

  return (
    <AuthContext.Provider value={{ user, isLoading, login, logout, refetchUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
