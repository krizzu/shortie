import { createFileRoute, useRouter } from "@tanstack/react-router"
import { LinksList } from "@/routes/_authorized/-components/links/LinksList.tsx"
import { linksQueryOption } from "@/queries/links-query-options.ts"
import { useSuspenseInfiniteQuery } from "@tanstack/react-query"
import { Loading } from "@/components/Loading.tsx"
import { Error } from "@/components/Error.tsx"

export const Route = createFileRoute("/_authorized/dashboard/urls")({
  component: LinksPage,
  beforeLoad: () => ({
    pageTitle: "Links",
  }),
  validateSearch: (raw) => {
    return {
      limit: isNaN(Number(raw["limit"])) ? 10 : Number(raw["limit"]),
    }
  },
  loaderDeps: ({ search }) => ({ limit: search.limit }),
  loader: ({ context, deps }) =>
    context.queryClient.ensureInfiniteQueryData(linksQueryOption(deps.limit)),
})

function LinksPage() {
  const { limit } = Route.useLoaderDeps()
  const router = useRouter()
  const linksQuery = useSuspenseInfiniteQuery(linksQueryOption(limit))
  const navigate = Route.useNavigate()

  function navigateToCreate() {
    navigate({ to: "/dashboard/urls/create" })
  }

  if (linksQuery.isLoading) {
    return <Loading />
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

  const links = linksQuery.data.pages.flatMap((p) => p.data) ?? []

  return (
    <LinksList
      links={links}
      fetchingNextPage={linksQuery.isFetchingNextPage}
      hasMore={linksQuery.hasNextPage}
      fetchNextPage={linksQuery.fetchNextPage}
      onCreateLink={navigateToCreate}
    />
  )
}
