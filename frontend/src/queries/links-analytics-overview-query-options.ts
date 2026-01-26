import { queryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"

export type ResponseAnalyticsOverview = {
  totalClicks: number
  totalLinks: number
  activeLinks: number
  expiredLinks: number
}

export const linkAnalyticsOverviewQueryOptions = () =>
  queryOptions({
    queryKey: ["links", "analytics", "overview"],
    queryFn: async () => {
      const apiResult = await fetcher<ResponseAnalyticsOverview>(
        "/urls/analytics/overview",
        {
          method: "GET",
        }
      )

      return apiResult.data
    },
  })
