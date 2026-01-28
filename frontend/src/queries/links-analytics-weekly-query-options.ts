import { queryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import type { ShortieAnalyticPeriodDetails } from "@/types/Link.ts"

type ResponsePaginatedLinksAnalytics = {
  startDate: string
  endDate: string
  totalClicksInPeriod: number
  clicksPerDate: Record<string, number> // iso 8601 utc date string mapping to number of clicks
}

export const linksAnalyticsWeeklyQueryOptions = () =>
  queryOptions({
    queryKey: ["links", "analytics", "weekly"],
    queryFn: async () => {
      const apiResult = await fetcher<ResponsePaginatedLinksAnalytics>(
        "/urls/analytics/weekly",
        {
          method: "GET",
        }
      )

      const result: ShortieAnalyticPeriodDetails = {
        ...apiResult.data,
        clicksPerDate: Object.entries(apiResult.data.clicksPerDate).map(
          (entry) => ({ date: entry[0], clicks: entry[1] })
        ),
      }

      return result
    },
  })
