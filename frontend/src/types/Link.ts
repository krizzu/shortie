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

export interface ShortieAnalyticPeriodDetails {
  startDate: string // iso 8601 utc date
  endDate: string // iso 8601 utc date
  totalClicksInPeriod: number
  clicksPerDate: { date: string; clicks: number }[] // list of iso 8601 utc date string mapping to number of clicks
}

export interface ShortieLinkAnalyticDetails extends ShortieLinkAnalytic {
  details: ShortieAnalyticPeriodDetails
}
