import React, { createContext, useContext, useEffect, useState } from "react"
import {
  type AuthTokens,
  clearTokens,
  onTokenRemoved,
  saveTokens,
} from "../services/auth-tokens.ts"
import { fetcher } from "../services/fetcher.ts"

export interface AuthState {
  authenticated: boolean
  login: (username: string, password: string) => Promise<void>
  logout: () => Promise<void>
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
      } else {
        logout()
      }
    } catch {
      logout()
    } finally {
      setIsLoading(false)
    }
  }

  // Restore auth state on app load
  useEffect(() => {
    checkAuth()

    return onTokenRemoved(logout)
  }, [])

  const login = async (user: string, password: string) => {
    const result = await fetcher<AuthTokens>("/auth/login", {
      method: "POST",
      body: {
        user,
        password,
      },
    })

    saveTokens(result.data)
    setAuthenticated(true)
  }

  const logout = async () => {
    setAuthenticated(false)
    clearTokens(false)
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
