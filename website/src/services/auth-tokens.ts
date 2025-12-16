export interface AuthTokens {
  accessToken: string
  refreshToken: string
}

const key = "shortie-auth-tokens"
type Listener = () => void
const listeners: Listener[] = []

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

export function onTokenRemoved(listener: Listener) {
  listeners.push(listener)

  return () => {
    const index = listeners.findIndex((v) => v === listener)
    if (index >= 0) {
      listeners.splice(index, 1)
    }
  }
}

export function clearTokens(notify: boolean = true) {
  localStorage.removeItem(key)
  if (!notify) {
    return
  }
  for (const listener of listeners) {
    console.log('calling listeners')
    listener()
  }
}
