import { createFileRoute, useRouter } from "@tanstack/react-router"
import { linkAnalyticsQueryOptions } from "@/queries/links-analytics-query-options.ts"
import { useQuery } from "@tanstack/react-query"
import { dateToUtcDateString } from "@/lib/utils.ts"
import { Error } from "@/components/Error.tsx"
import { Loading } from "@/components/Loading.tsx"
import { ClicksOverTimeChart } from "@/routes/_authorized/dashboard.analytics/-components/ClicksOverTimeChart.tsx"
import { LinkSummaryCard } from "./-components/LinkSummaryCard"
import { DatePickerWithRange } from "@/routes/_authorized/dashboard.analytics/-components/DateRangePicker.tsx"
import type { ShortieLinkAnalytic } from "@/types/Link.ts"

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
  const navigate = Route.useNavigate()
  const router = useRouter()
  const query = useQuery(
    linkAnalyticsQueryOptions(shortCode, startDate, endDate)
  )

  function updateDates(dates: { from: Date; to: Date }) {
    navigate({
      search: (prev) => ({
        ...prev,
        startDate: dateToUtcDateString(dates.from),
        endDate: dateToUtcDateString(dates.to),
      }),
    })
  }

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

  const chartData = query.data ? getChartData(query.data.details) : undefined

  return (
    <div className="grid grid-cols-4 gap-x-4 gap-y-4">
      <div className="col-span-4">
        <DatePickerWithRange
          initial={{ from: new Date(startDate), to: new Date(endDate) }}
          onDateSelected={updateDates}
        />
      </div>

      <LinkSummaryCard
        loading={query.isLoading}
        updating={query.isFetching}
        link={query.data}
      />

      <ClicksOverTimeChart
        linkClicks={chartData}
        loading={query.isLoading}
        updating={query.isFetching}
        startDate={startDate}
        endDate={endDate}
        className="col-span-2"
      />
    </div>
  )
}

function getChartData(
  details: ShortieLinkAnalytic["details"] | undefined
): { date: string; clicks: number }[] | undefined {
  if (!details) {
    return undefined
  }

  const data: { date: string; clicks: number }[] = []

  details.forEach((_, date, clicks) => {
    data.push({ date, clicks: clicks.get(date) ?? 0 })
  })

  return data.length ? data : undefined
}
