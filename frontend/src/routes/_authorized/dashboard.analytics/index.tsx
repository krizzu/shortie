import { createFileRoute } from "@tanstack/react-router"
import { linkAnalyticsQueryOptions } from "@/queries/links-analytics-query-options.ts"
import { useQuery } from "@tanstack/react-query"

const DEFAULT_LIMIT = 20

export const Route = createFileRoute("/_authorized/dashboard/analytics/")({
  validateSearch: (
    raw
  ): { limit?: number; page?: string; previous?: string[] } => {
    return {
      limit: isNaN(Number(raw["limit"])) ? DEFAULT_LIMIT : Number(raw["limit"]),
      page: raw["page"] ? String(raw["page"]) : undefined,
      previous: raw["previous"]
        ? String(raw["previous"]).split(",")
        : undefined,
    }
  },

  loaderDeps: ({ search }) => {
    return {
      limit: search.limit,
      page: search.page,
    }
  },

  loader: ({ context, deps }) => {
    context.queryClient.ensureQueryData(
      linkAnalyticsQueryOptions(deps.page, deps.limit)
    )
  },

  component: RouteComponent,
})

function RouteComponent() {
  const deps = Route.useLoaderDeps()
  const links = useQuery(linkAnalyticsQueryOptions(deps.page, deps.limit))

  // todo: build pageX
  return <div>{links.data?.data?.map((l) => l.shortCode)}</div>
}
