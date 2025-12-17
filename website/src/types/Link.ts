export interface ShortieLink {
  originalUrl: string
  alias: string | null
  expiry: string | null // utc string
  password: string | null
}
