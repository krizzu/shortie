import { createFileRoute, useRouter } from "@tanstack/react-router"
import { linkAnalyticsQueryOptions } from "@/queries/links-analytics-query-options.ts"
import { useQuery } from "@tanstack/react-query"
import { dateToUtcDateString } from "@/lib/utils.ts"
import { Error } from "@/components/Error.tsx"
import { Loading } from "@/components/Loading.tsx"
import { ClicksOverTimeChart } from "@/routes/_authorized/dashboard.analytics/-components/ClicksOverTimeChart.tsx"
import { LinkSummaryCard } from "./-components/LinkSummaryCard"

export const Route = createFileRoute(
  "/_authorized/dashboard/analytics/$shortCode"
)({
  validateSearch: (raw): { startDate?: string; endDate?: string } => {
    return {
      startDate: raw["startDate"] ? String(raw["startDate"]) : undefined,
      endDate: raw["endDate"] ? String(raw["endDate"]) : undefined,
    }
  },

  loaderDeps: ({ search }) => {
    const past7Days = dateToUtcDateString(new Date(Date.now() - 604800 * 1000))
    const today = dateToUtcDateString(new Date())

    return {
      startDate: search?.startDate ?? past7Days,
      endDate: search?.endDate ?? today,
    }
  },

  loader: ({ params, context, deps }) => {
    context.queryClient.ensureQueryData(
      linkAnalyticsQueryOptions(params.shortCode, deps.startDate, deps.endDate)
    )
  },

  component: RouteComponent,
})

function RouteComponent() {
  const { shortCode } = Route.useParams()
  const { startDate, endDate } = Route.useLoaderDeps()
  const router = useRouter()
  const query = useQuery(
    linkAnalyticsQueryOptions(shortCode, startDate, endDate)
  )

  if (query.isLoading) {
    return <Loading label="loading data..." />
  }

  if (query.error) {
    return (
      <Error
        error={query.error}
        onRetry={() => {
          router.invalidate()
        }}
      />
    )
  }

  const linkData = query.data

  if (!linkData) {
    return (
      <Error
        title="Link data not found"
        error={`Sorry, but link "${shortCode}" could not be found`}
        retryLabel="Go back"
        onRetry={() => {
          router.navigate({ to: "/dashboard/analytics" })
        }}
      />
    )
  }

  return (
    <div className="grid grid-cols-4 gap-x-4">
      <LinkSummaryCard link={linkData} />

      <ClicksOverTimeChart
        shortie={linkData}
        startDate={startDate}
        endDate={endDate}
        className="col-span-2"
      />
    </div>
  )
}
