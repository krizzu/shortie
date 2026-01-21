import { queryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import type { ShortieLinkAnalytic } from "@/types/Link.ts"

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

      const apiResult = await fetcher<ShortieLinkAnalytic>(url, {
        method: "GET",
      })

      return apiResult.data
    },
  })
