import { queryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import type { ShortieLinkAnalytic } from "@/types/Link.ts"

export const linkAnalyticsQueryOptions = (
  shortCode: string,
  startDate: string, // iso date string
  endDate: string // iso date string
) =>
  queryOptions({
    queryKey: ["links", "analytics", { shortCode, startDate, endDate }],
    queryFn: async () => {
      const url = `/urls/analytics/${shortCode}?startDate=${startDate}&endDate=${endDate}`
      const result = await fetcher<ShortieLinkAnalytic>(url, {
        method: "GET",
      })

      return result.data
    },
  })
