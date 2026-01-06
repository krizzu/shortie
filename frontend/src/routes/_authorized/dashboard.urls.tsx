import { createFileRoute, useRouter } from "@tanstack/react-router"
import { LinksList } from "@/routes/_authorized/-components/links/LinksList.tsx"
import { linksQueryOptions } from "@/queries/links-query-options.ts"
import { useQuery } from "@tanstack/react-query"
import { Error } from "@/components/Error.tsx"

export const Route = createFileRoute("/_authorized/dashboard/urls")({
  component: LinksPage,
  beforeLoad: () => ({
    pageTitle: "Links",
  }),
  validateSearch: (raw): { limit?: number; page?: string } => {
    return {
      limit: isNaN(Number(raw["limit"])) ? undefined : Number(raw["limit"]),
      page: raw["page"] ? String(raw["page"]) : undefined,
    }
  },
  loaderDeps: ({ search }) => ({ limit: search.limit, page: search.page }),
  loader: ({ context, deps }) =>
    context.queryClient.ensureQueryData(
      linksQueryOptions(deps.page, deps.limit)
    ),
})

function LinksPage() {
  const deps = Route.useLoaderDeps()
  const router = useRouter()
  const linksQuery = useQuery(linksQueryOptions(deps.page, deps.limit))
  const navigate = Route.useNavigate()

  function navigateToCreate() {
    navigate({ to: "/dashboard/urls/create" })
  }

  function navigateToNextPage(page: string) {
    navigate({ to: "/dashboard/urls", search: (cur) => ({ ...cur, page }) })
  }

  if (linksQuery.error) {
    return (
      <Error
        error={linksQuery.error}
        reset={() => {
          router.invalidate()
        }}
      />
    )
  }

  const data = linksQuery.data

  return (
    <LinksList
      links={data?.data ?? []}
      hasMore={data?.hasNext ?? false}
      loading={linksQuery.isLoading}
      fetchNextPage={() => {
        if (data?.nextCursor) {
          navigateToNextPage(data.nextCursor)
        }
      }}
      onCreateLink={navigateToCreate}
    />
  )
}
