/* eslint-disable @typescript-eslint/no-explicit-any */

/**
 * env.js allows to inject custom env during runtime (vs compile time envs)
 */
type RuntimeEnv = {
  API_URL: string
}

class Vars {
  envs = (window as any).__ENV__ as RuntimeEnv | undefined

  get apiUrl(): string {
    const buildFlag = import.meta.env.VITE_API_URL as string | undefined

    const final = buildFlag ?? this.envs?.API_URL
    if (!final) {
      throw new Error("API variable set!")
    }
    return final
  }
}

export const EnvVars = new Vars()
