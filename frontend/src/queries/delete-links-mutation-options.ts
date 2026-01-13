import { mutationOptions } from "@tanstack/react-query"
import { fetcher } from "@/services/fetcher.ts"
import { queryClient } from "@/queries/query-client.ts"

export const deleteLinkMutationOptions = mutationOptions({
  mutationFn: async (shortCode: string) => {
    const result = await fetcher<{ deleted: number }>("/urls", {
      method: "DELETE",
      body: {
        shortCodes: [shortCode],
      },
    })
    return result.data
  },

  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["links"] })
  },
})
