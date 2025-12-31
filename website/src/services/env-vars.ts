/* eslint-disable @typescript-eslint/no-explicit-any */

/**
 * env.js allows to inject custom env during runtime (vs compile time envs)
 */
type RuntimeEnv = {
  API_URL: string
  API_BASE_URL: string
}

class Vars {
  envs = (window as any).__ENV__ as RuntimeEnv | undefined

  readValue(name: string) {
    const buildFlag = import.meta.env[`VITE_${name}`] as string | undefined
    const final = buildFlag ?? this.envs?.API_URL

    if (!final) {
      throw new Error(`variable ${name} not set!`)
    }

    return final
  }

  get apiUrl(): string {
    return this.readValue("API_URL")
  }

  get apiBaseUrl(): string {
    return this.readValue("API_BASE_URL")
  }
}

export const EnvVars = new Vars()
