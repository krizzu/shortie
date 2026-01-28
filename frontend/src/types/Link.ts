export interface ShortieLink {
  originalUrl: string
  shortCode: string
  protected: boolean
  expiryDate?: string // iso 8601 utc datetime string
}

export interface ShortieLinkAnalytic {
  shortCode: string
  totalClicks: number
  lastClick: string | null // iso 8601 utc datetime string
}

export interface ShortieLinkAnalyticDetails extends ShortieLinkAnalytic {
  details: {
    totalClicksInPeriod: number
    clicksPerDate: Map<string, number> // iso 8601 utc date string mapping to number of clicks
  }
}
