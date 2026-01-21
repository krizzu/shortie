import { queryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import type {
  ShortieLinkAnalytic,
  ShortieLinkAnalyticDetails,
} from "@/types/Link.ts"

type ShortieLinkAnalyticResponse = {
  info: ShortieLinkAnalytic
  details: Record<string, number>
}

export const linkAnalyticsQueryOptions = (
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
        details: new Map(Object.entries(data.details)),
      }

      return result
    },
  })
