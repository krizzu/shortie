class Vars {
  private readInjected(name: string): string | null {
    const rootElement = document.getElementById("root")!

    // react normalizes custom data props to all lowercase
    const value = rootElement.dataset[name.toLowerCase()]
    if (!value || value === `$${name}`) {
      return null
    }

    return value
  }

  redirectUrl(shortUrl: string): string {
    const url = new URL(shortUrl, window.location.origin)
    url.port =
      import.meta.env.VITE_APP_API_PROXY_PORT ??
      this.readInjected("APP_API_PROXY_PORT") ??
      ""
    return url.href
  }

  get apiUrl(): string {
    const path = import.meta.env.VITE_API_BASE_URL ?? "/api/"
    const url = new URL(path, window.location.origin)
    return url.href
  }
}

export const EnvVars = new Vars()
