import { createFileRoute, useRouter } from "@tanstack/react-router"
import { linkAnalyticsQueryOptions } from "@/queries/links-analytics-query-options.ts"
import { useQuery } from "@tanstack/react-query"
import { dateToUtcDateString } from "@/lib/utils.ts"
import { Error } from "@/components/Error.tsx"
import { Loading } from "@/components/Loading.tsx"

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
    const today = dateToUtcDateString(new Date())
    const next7Days = dateToUtcDateString(new Date(Date.now() + 604800))

    return {
      startDate: search?.startDate ?? today,
      endDate: search?.endDate ?? next7Days,
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
        reset={() => {
          router.invalidate()
        }}
      />
    )
  }

  return (
    <div>
      <p>hello to {shortCode} analytics</p>
    </div>
  )
}
