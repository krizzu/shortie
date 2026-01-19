import { createFileRoute, useRouter } from "@tanstack/react-router"
import { LinksList } from "@/routes/_authorized/-components/links/LinksList.tsx"
import { linksQueryOptions } from "@/queries/links-query-options.ts"
import { useMutation, useQuery } from "@tanstack/react-query"
import { Error } from "@/components/Error.tsx"
import type { ShortieLink } from "@/types/Link.ts"
import { deleteLinkMutationOptions } from "@/queries/delete-links-mutation-options.ts"

const DEFAULT_LIMIT = 10

export const Route = createFileRoute("/_authorized/dashboard/urls/")({
  component: LinksPage,
  beforeLoad: () => ({
    pageTitle: "Links",
  }),
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
  loaderDeps: ({ search }) => ({
    limit: search.limit,
    page: search.page,
  }),
  loader: ({ context, deps }) =>
    context.queryClient.ensureQueryData(
      linksQueryOptions(deps.page, deps.limit)
    ),
})

function LinksPage() {
  const deps = Route.useLoaderDeps()
  const search = Route.useSearch()
  const linksQuery = useQuery(linksQueryOptions(deps.page, deps.limit))
  const router = useRouter()
  const navigate = Route.useNavigate()
  const deleteLinkMutation = useMutation(deleteLinkMutationOptions)

  const hasPreviousPage = (search.previous?.length ?? 0) > 0
  const previousButtonActive =
    hasPreviousPage || (!hasPreviousPage && search.page)

  async function deleteLink(link: ShortieLink) {
    await deleteLinkMutation.mutateAsync(link.shortCode)
  }

  function navigateToCreate() {
    navigate({ to: "/dashboard/urls/create" })
  }

  function navigateToCodeAnalytics(shortie: ShortieLink) {
    navigate({
      to: "/dashboard/analytics/$shortCode",
      params: { shortCode: shortie.shortCode },
    })
  }

  function navigateToPreviousPage() {
    navigate({
      to: "/dashboard/urls",
      search: (cur) => {
        const [toPage, ...restPages] = cur.previous ?? []

        return {
          ...cur,
          page: toPage || undefined,
          previous: restPages.length ? restPages : undefined,
        }
      },
    })
  }

  function navigateToNextPage(page: string) {
    navigate({
      to: "/dashboard/urls",
      search: (cur) => {
        const history = cur.previous ?? []
        const newHistory = cur.page ? [cur.page, ...history] : history

        return {
          ...cur,
          previous: newHistory.length ? newHistory : undefined,
          page,
        }
      },
    })
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
      loading={linksQuery.isLoading}
      fetchNextPage={
        data?.nextCursor
          ? () => {
              navigateToNextPage(data.nextCursor!)
            }
          : null
      }
      goToPreviousPage={
        previousButtonActive
          ? () => {
              navigateToPreviousPage()
            }
          : null
      }
      onCreateLink={navigateToCreate}
      onDeleteLink={deleteLink}
      goToCodeAnalytics={navigateToCodeAnalytics}
    />
  )
}
