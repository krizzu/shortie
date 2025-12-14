import React, { createContext, useContext, useEffect, useState } from "react"
import { clearTokens } from "../services/auth-tokens.ts"
import { fetcher } from "../services/fetcher.ts"

export interface AuthState {
  authenticated: boolean
  login: (username: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthState | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [authenticated, setAuthenticated] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

  async function checkAuth() {
    try {
      const result = await fetcher<{ valid: boolean }>("/auth/validate")
      if (result.data.valid) {
        setAuthenticated(true)
      }
      clearTokens()
      setAuthenticated(true)
    } catch (e: unknown) {
      clearTokens()
      setAuthenticated(false)
    } finally {
      setIsLoading(false)
    }
  }

  // Restore auth state on app load
  useEffect(() => {
    checkAuth()
  }, [])

  const login = async (username: string, password: string) => {
    console.error(`TODO: login with ${username} and ${password}`)
  }

  const logout = () => {
    setAuthenticated(false)
    clearTokens()
  }

  // Show loading state while checking auth
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        Loading...
      </div>
    )
  }

  return (
    <AuthContext.Provider value={{ authenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
