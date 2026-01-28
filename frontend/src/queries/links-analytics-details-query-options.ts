import { queryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import type {
  ShortieLinkAnalytic,
  ShortieLinkAnalyticDetails,
} from "@/types/Link.ts"

type ShortieLinkAnalyticResponse = {
  info: ShortieLinkAnalytic
  details: {
    startDate: string
    endDate: string
    totalClicksInPeriod: number
    clicksPerDate: Record<string, number>
  }
}

export const linkAnalyticsDetailsQueryOptions = (
  shortCode: string,
  startDate: string, // iso date string
  endDate: string // iso date string
) =>
  queryOptions({
    queryKey: ["links", "analytics", { shortCode, startDate, endDate }],
    queryFn: async () => {
      const url = `/urls/analytics/${shortCode}?startDate=${startDate}&endDate=${endDate}`
      const apiResult = await fetcher<ShortieLinkAnalyticResponse>(url, {
        method: "GET",
      })

      const data = apiResult.data

      const result: ShortieLinkAnalyticDetails = {
        ...data.info,
        details: {
          ...data.details,
          clicksPerDate: Object.entries(data.details.clicksPerDate).map(
            (entry) => ({ date: entry[0], clicks: entry[1] })
          ),
        },
      }

      return result
    },
  })
