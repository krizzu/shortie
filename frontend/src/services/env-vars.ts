class Vars {
  redirectUrl(shortUrl: string): string {
    const url = new URL(shortUrl, window.location.origin)
    url.port = import.meta.env.VITE_REDIRECT_PORT ?? ""
    return url.href
  }

  get apiUrl(): string {
    const path = import.meta.env.VITE_API_BASE_URL ?? "/api/"
    const url = new URL(path, window.location.origin)
    return url.href
  }
}

export const EnvVars = new Vars()
