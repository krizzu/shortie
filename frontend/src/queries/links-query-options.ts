import { queryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import type { ShortieLink } from "@/types/Link.ts"

export type ResponsePaginatedLinks = {
  hasNext: boolean
  nextCursor: string | null
  data: Array<ShortieLink>
}

export const linksQueryOptions = (
  cursor: string | undefined,
  limit: number | undefined = 20
) =>
  queryOptions({
    queryKey: ["links", "paginated", cursor, limit ?? 20],
    queryFn: async () => {
      let url = `/urls?limit=${limit ?? 20}`
      if (cursor) {
        url += `&cursor=${encodeURIComponent(cursor)}`
      }
      const result = await fetcher<ResponsePaginatedLinks>(url, {
        method: "GET",
      })
      return result.data
    },
  })
