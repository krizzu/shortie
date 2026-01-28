import { queryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import type { ShortieLinkAnalytic } from "@/types/Link.ts"

export type ResponsePaginatedLinksAnalytics = {
  hasNext: boolean
  nextPage: number | null
  data: Array<ShortieLinkAnalytic>
}

export const linkAnalyticsQueryOptions = (
  page: number | undefined,
  limit: number | undefined = 20
) =>
  queryOptions({
    queryKey: ["links", "analytics", { page, limit }],
    queryFn: async () => {
      let url = `/urls/analytics/links?limit=${limit ?? 20}`
      if (page) {
        url += `&page=${page}`
      }

      const apiResult = await fetcher<ResponsePaginatedLinksAnalytics>(url, {
        method: "GET",
      })

      return apiResult.data
    },
  })
