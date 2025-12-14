export interface AuthTokens {
  accessToken: string
  refreshToken: string
}

const key = "shortie-auth-tokens"

export function getTokens() {
  const stored = localStorage.getItem(key)
  if (stored) {
    return JSON.parse(stored) as AuthTokens
  }
  return null
}

export function saveTokens(tokens: AuthTokens) {
  localStorage.setItem(key, JSON.stringify(tokens))
}

export function clearTokens() {
  localStorage.removeItem(key)
}
