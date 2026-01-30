import { createFileRoute } from "@tanstack/react-router"
import { useQuery } from "@tanstack/react-query"
import { linkAnalyticsOverviewQueryOptions } from "@/queries/links-analytics-overview-query-options.ts"
import { ValueSummaryCard } from "@/routes/_authorized/dashboard.analytics/-components/ValueSummaryCard.tsx"
import { linkAnalyticsQueryOptions } from "@/queries/links-analytics-query-options.ts"
import { AnalyticLinksList } from "@/routes/_authorized/dashboard.analytics/-components/AnalyticLinksList.tsx"

import { WeeklyLinearChart } from "@/routes/_authorized/dashboard.analytics/-components/WeeklyLinearChart.tsx"
import { linksAnalyticsWeeklyQueryOptions } from "@/queries/links-analytics-weekly-query-options.ts"
import { PagePagination } from "@/routes/_authorized/dashboard.analytics/-components/PagePagination.tsx"
import { DropdownSelection } from "../-components/DropdownSelection"

const DEFAULT_LIMIT = 10
const AVAILABLE_LIMITS = [5, 10, 15, 20]

export const Route = createFileRoute("/_authorized/dashboard/analytics/")({
  validateSearch: (raw): { limit: number; page?: number } => {
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
    context.queryClient.ensureQueryData(linksAnalyticsWeeklyQueryOptions())
  },

  component: RouteComponent,
})

function RouteComponent() {
  const deps = Route.useLoaderDeps()
  const links = useQuery(linkAnalyticsQueryOptions(deps.page, deps.limit))
  const overview = useQuery(linkAnalyticsOverviewQueryOptions())
  const weekly = useQuery(linksAnalyticsWeeklyQueryOptions())
  const navigate = Route.useNavigate()

  function updateLimit(limit: number) {
    // reset page as well, so pagination is not broken
    navigate({ search: (curr) => ({ ...curr, limit, page: undefined }) })
  }

  function viewLinkDetails(shortCode: string) {
    navigate({
      to: "/dashboard/analytics/$shortCode",
      params: { shortCode: shortCode },
    })
  }

  const currentPage = deps.page ?? 0
  const nextPage = links.data?.nextPage ?? null
  function goToNextPage() {
    if (nextPage !== null) {
      navigate({ search: (s) => ({ ...s, page: nextPage }) })
    }
  }

  function goToPreviousPage() {
    const previous = currentPage - 1
    if (previous <= 0) {
      navigate({ search: (s) => ({ ...s, page: undefined }) })
    } else {
      navigate({ search: (s) => ({ ...s, page: previous }) })
    }
  }

  return (
    <div className="space-y-8 mb-4">
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
      <div className="grid grid-cols-1 gap-4 xl:grid-cols-2">
        <WeeklyLinearChart
          data={weekly.data}
          loading={weekly.isLoading}
          updating={weekly.isFetching}
        />
        <div className="space-y-2">
          <div className="flex items-center gap-x-4">
            <DropdownSelection
              selected={toValue(deps.limit)}
              label="per page"
              onSelect={(v) => updateLimit(Number(v.value))}
              available={AVAILABLE_LIMITS.map(toValue)}
            />
            {!nextPage && currentPage === 0 ? null : (
              <PagePagination
                currentPage={currentPage}
                hasPreviousPage={currentPage > 0}
                hasNextPage={!!nextPage}
                onNextPage={goToNextPage}
                onPreviousPage={goToPreviousPage}
              />
            )}
          </div>
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
