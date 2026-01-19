export interface ShortieLink {
  originalUrl: string
  shortCode: string
  protected: boolean
  expiryDate?: string // iso 8601 utc datetime string
}

export interface ShortieLinkAnalytic extends ShortieLink {
  totalClicks: number
  lastClick: string | null // iso 8601 utc datetime string
  details: Map<Date, number> // iso 8601 utc date string mapping to number of clicks
}
