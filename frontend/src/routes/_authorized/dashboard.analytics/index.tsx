import { createFileRoute } from "@tanstack/react-router"
import { useQuery } from "@tanstack/react-query"
import { linksQueryOptions } from "@/queries/links-query-options.ts"
import { linkAnalyticsOverviewQueryOptions } from "@/queries/links-analytics-overview-query-options.ts"
import { ValueSummaryCard } from "@/routes/_authorized/dashboard.analytics/-components/ValueSummaryCard.tsx"

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
      linksQueryOptions(deps.page, deps.limit)
    )
    context.queryClient.ensureQueryData(linkAnalyticsOverviewQueryOptions())
  },

  component: RouteComponent,
})

function RouteComponent() {
  const deps = Route.useLoaderDeps()
  const links = useQuery(linksQueryOptions(deps.page, deps.limit))
  const overview = useQuery(linkAnalyticsOverviewQueryOptions())

  return (
    <div>
      <div className="grid grid-cols-4 gap-x-4">
        <ValueSummaryCard
          value={overview.data?.totalLinks}
          name="total links"
          loading={overview.isLoading}
          updating={overview.isFetching}
        />
        <ValueSummaryCard
          value={overview.data?.totalClicks}
          name="total clicks"
          loading={overview.isLoading}
          updating={overview.isFetching}
        />
        <ValueSummaryCard
          value={overview.data?.activeLinks}
          name="active links"
          loading={overview.isLoading}
          updating={overview.isFetching}
        />
        <ValueSummaryCard
          value={overview.data?.expiredLinks}
          name="expired links"
          loading={overview.isLoading}
          updating={overview.isFetching}
        />
      </div>
    </div>
  )
}
