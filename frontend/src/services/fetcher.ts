/* eslint-disable @typescript-eslint/no-explicit-any */
import { type AuthTokens, clearTokens, getTokens, saveTokens, } from "./auth-tokens.ts"
import { EnvVars } from "./env-vars.ts"

async function _fetch(
  input: RequestInfo | URL,
  init?: RequestInit
): Promise<Response> {
  let result: Response
  try {
    const base = endpointUrl(EnvVars.apiUrl)
    let endpoint = input
    if (typeof endpoint === "string") {
      endpoint = new URL(relativePath(endpoint), base).href
    }

    result = await fetch(endpoint, init)
  } catch (e: any) {
    console.error(e)
    throw new HttpError(
      `request failed: ${e.message ?? e}`,
      -1,
      "request failed"
    )
  }

  if (result.redirected) {
    window.location.replace(result.url)
    return result
  }

  if (!result.ok) {
    throw new HttpError(await result.text(), result.status, result.statusText)
  }

  return result
}

let refreshPromise: Promise<AuthTokens | null> | null = null
async function refreshToken() {
  if (refreshPromise) return refreshPromise

  refreshPromise = (async (): Promise<AuthTokens | null> => {
    const tokens = getTokens()
    if (!tokens) {
      return null
    }

    const result = await _fetch("auth/refresh", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ refreshToken: tokens.refreshToken }),
    })

    if (!result.ok) {
      return null
    }

    const updatedTokens: AuthTokens = await result.json()
    saveTokens(updatedTokens)
    return updatedTokens
  })().finally(() => {
    refreshPromise = null
  })

  return refreshPromise
}

type RequestOptions = Pick<RequestInit, "method" | "headers" | "redirect"> & {
  body?: RequestInit["body"] | Record<string, unknown>
  authorize?: boolean // if request should add auth token in Authorized header, defaults to true
  _retry?: boolean // internal flag indicating the request is retry request and should avoid token refresh
}

type FetchResult<T> = Pick<Response, "headers"> & {
  data: T
  status: string
  statusCode: number
}
export async function fetcher<T>(
  url: string | URL,
  options: RequestOptions = {}
): Promise<FetchResult<T>> {
  const {
    authorize = true,
    body,
    _retry,
    method,
    headers: restHeaders,
    ...otherOptions
  } = options
  let bodyPayload: string | FormData | undefined

  let type: Record<string, string> = {}
  if (body) {
    // do not set content-type for form data
    if (body instanceof FormData) {
      bodyPayload = body
    } else {
      type = { "Content-Type": "application/json" }
      bodyPayload = JSON.stringify(body)
    }
  }

  const headers = {
    ...restHeaders,
    ...type,
    ...(authorize
      ? { Authorization: `Bearer ${getTokens()?.accessToken}` }
      : {}),
  }
  try {
    const result = await _fetch(url, {
      ...otherOptions,
      method,
      body: bodyPayload,
      headers: headers,
    })

    // todo: if required, check for returned content type
    // const type = result.headers.get("content-type")
    // if (type?.includes("application/json"))
    return {
      headers: result.headers,
      status: result.statusText,
      statusCode: result.status,
      data: (await result.json()) as T,
    }
  } catch (e: unknown) {
    if (!(e instanceof HttpError)) {
      const message = e instanceof Error ? e.message : String(e)
      throw new HttpError(`unknown http error: ${message}`, -1, message)
    }

    if (e.unauthorized && !_retry) {
      try {
        const updated = await refreshToken()
        if (updated) {
          return await fetcher(url, {
            ...options,
            _retry: true,
            body: bodyPayload,
          })
        } else {
          clearTokens()
        }
      } catch (e: unknown) {
        console.error(`failed to refresh tokens: ${e}`)
      }
    }

    throw e
  }
}

export class HttpError extends Error {
  public statusCode: number
  public status: string

  constructor(message: string, statusCode: number, status: string) {
    super(message)
    this.name = "HttpError"
    this.status = status
    this.statusCode = statusCode
  }

  get unauthorized(): boolean {
    return this.statusCode === 401
  }

  get networkError(): boolean {
    return this.statusCode < 0
  }
}

function relativePath(path: string): string {
  return path.startsWith("/") ? path.slice(1) : path
}

// path needs to end with trailing slash, otherwise it will be removed
function endpointUrl(path: string): string {
  return path.endsWith("/") ? path : `${path}/`
}
