import { createFileRoute, useRouter } from "@tanstack/react-router"
import { linkAnalyticsDetailsQueryOptions } from "@/queries/links-analytics-details-query-options.ts"
import { useQuery } from "@tanstack/react-query"
import { dateToUtcDateString } from "@/lib/utils.ts"
import { Error } from "@/components/Error.tsx"
import { Loading } from "@/components/Loading.tsx"
import {
  ClicksOverTimeChart,
  ClicksOverTimeSummary,
} from "@/routes/_authorized/dashboard.analytics/-components/ClicksOverTimeChart.tsx"
import { LinkSummaryCard } from "./-components/LinkSummaryCard"
import { DatePickerWithRange } from "@/routes/_authorized/dashboard.analytics/-components/DateRangePicker.tsx"

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
      linkAnalyticsDetailsQueryOptions(
        params.shortCode,
        deps.startDate,
        deps.endDate
      )
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
    linkAnalyticsDetailsQueryOptions(shortCode, startDate, endDate)
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

  return (
    <div className="grid grid-cols-4 gap-x-4 gap-y-4">
      <div className="col-span-4">
        <DatePickerWithRange
          initial={{ from: new Date(startDate), to: new Date(endDate) }}
          onDateSelected={updateDates}
        />
      </div>

      <div className="grid grid-rows-2 gap-4">
        <ClicksOverTimeSummary
          data={query.data?.details}
          startDate={startDate}
          endDate={endDate}
          loading={query.isLoading}
          updating={query.isFetching}
        />
        <LinkSummaryCard
          loading={query.isLoading}
          updating={query.isFetching}
          link={query.data}
        />
      </div>

      <ClicksOverTimeChart
        data={query.data?.details}
        loading={query.isLoading}
        updating={query.isFetching}
        startDate={startDate}
        endDate={endDate}
        className="col-span-2"
      />
    </div>
  )
}
