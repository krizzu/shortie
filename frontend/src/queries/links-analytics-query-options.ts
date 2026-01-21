import { queryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import type { ShortieLinkAnalytic } from "@/types/Link.ts"

export type ResponsePaginatedLinksAnalytics = {
  hasNext: boolean
  nextCursor: string | null
  data: Array<ShortieLinkAnalytic>
}

export const linkAnalyticsQueryOptions = (
  cursor: string | undefined,
  limit: number | undefined = 20
) =>
  queryOptions({
    queryKey: ["links", "analytics", { cursor, limit }],
    queryFn: async () => {
      let url = `/urls/analytics?limit=${limit ?? 20}`
      if (cursor) {
        url += `&cursor=${encodeURIComponent(cursor)}`
      }

      const apiResult = await fetcher<ResponsePaginatedLinksAnalytics>(url, {
        method: "GET",
      })

      return apiResult.data
    },
  })
