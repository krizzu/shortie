import { mutationOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import { queryClient } from "@/queries/query-client.ts"
import type { ShortieLink } from "@/types/Link.ts"

export const updateLinkMutationOptions = mutationOptions({
  mutationFn: async (payload: {
    shortCode: string
    password?: string | null
    expiry?: string | null
  }) => {
    const body: Record<string, string | null> = {}
    if (payload.password !== undefined) {
      body.password = payload.password
    }
    if (payload.expiry !== undefined) {
      body.expiryDate = payload.expiry
    }

    const result = await fetcher<ShortieLink>(`/urls/${payload.shortCode}`, {
      method: "PATCH",
      body,
    })
    return result.data
  },

  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["links"] })
  },
})
