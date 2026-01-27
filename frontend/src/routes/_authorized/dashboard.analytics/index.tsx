import { createFileRoute } from "@tanstack/react-router"
import { useQuery } from "@tanstack/react-query"
import { linkAnalyticsOverviewQueryOptions } from "@/queries/links-analytics-overview-query-options.ts"
import { ValueSummaryCard } from "@/routes/_authorized/dashboard.analytics/-components/ValueSummaryCard.tsx"
import { linkAnalyticsQueryOptions } from "@/queries/links-analytics-query-options.ts"
import { AnalyticLinksList } from "@/routes/_authorized/dashboard.analytics/-components/AnalyticLinksList.tsx"
import { DropdownSelection } from "@/routes/_authorized/dashboard.analytics/-components/DropdownSelection.tsx"

const DEFAULT_LIMIT = 10
const AVAILABLE_LIMITS = [1, 5, 10, 20, 30, 50]

export const Route = createFileRoute("/_authorized/dashboard/analytics/")({
  validateSearch: (
    raw
  ): { limit: number; page?: number; previous?: string[] } => {
    return {
      limit: isNaN(Number(raw["limit"])) ? DEFAULT_LIMIT : Number(raw["limit"]),
      page: isNaN(Number(raw["page"])) ? undefined : Number(raw["page"]),
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
    context.queryClient.ensureQueryData(linkAnalyticsOverviewQueryOptions())
  },

  component: RouteComponent,
})

function RouteComponent() {
  const deps = Route.useLoaderDeps()
  const links = useQuery(linkAnalyticsQueryOptions(deps.page, deps.limit))
  const overview = useQuery(linkAnalyticsOverviewQueryOptions())
  const navigate = Route.useNavigate()

  function updateLimit(limit: number) {
    navigate({ search: (curr) => ({ ...curr, limit }) })
  }

  function viewLinkDetails(shortCode: string) {
    navigate({
      to: "/dashboard/analytics/$shortCode",
      params: { shortCode: shortCode },
    })
  }

  return (
    <div className="space-y-8">
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
      <div className="grid grid-cols-1 xl:grid-cols-2">
        <div className="space-y-2">
          <DropdownSelection
            selected={toValue(deps.limit)}
            label="per page"
            onSelect={(v) => updateLimit(Number(v.value))}
            available={AVAILABLE_LIMITS.map(toValue)}
          />
          <AnalyticLinksList
            loading={links.isLoading}
            data={links.data?.data ?? []}
            onViewDetails={(link) => viewLinkDetails(link.shortCode)}
            className="xl:col-span-2"
          />
        </div>
      </div>
    </div>
  )
}

function toValue(value: number): { value: string; name: string } {
  return {
    value: String(value),
    name: String(value),
  }
}
