export interface ShortieLink {
  originalUrl: string
  shortCode: string
  protected: boolean
  expiryDate?: string // utc string
}
