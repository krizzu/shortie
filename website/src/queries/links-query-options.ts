import { infiniteQueryOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import type { ShortieLink } from "@/types/Link.ts"

export type ResponsePaginatedLinks = {
  hasNext: boolean
  nextCursor: string | null
  data: Array<ShortieLink>
}

export const linksQueryOption = (limit: number = 25) =>
  infiniteQueryOptions({
    queryKey: ["all-links-paginated", limit],
    queryFn: async ({ pageParam }) => {
      let url = `/urls?limit=${limit}`
      if (pageParam) {
        url += `&cursor=${encodeURIComponent(pageParam)}`
      }
      const result = await fetcher<ResponsePaginatedLinks>(url, {
        method: "GET",
      })
      return result.data
    },

    initialPageParam: undefined as string | undefined,

    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
  })
