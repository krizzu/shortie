import { createFileRoute } from "@tanstack/react-router"
import { EmptyLinkPage } from "./-components/links/EmptyLinkPage"

export const Route = createFileRoute("/_authorized/dashboard/links")({
  component: LinksPage,
  beforeLoad: () => ({
    pageTitle: "Links",
  }),
})

function LinksPage() {
  const navigate = Route.useNavigate()

  function navigateToCreate() {
    navigate({ to: "/dashboard/links/create" })
  }

  return (
    <div>
      <EmptyLinkPage onCreateLink={navigateToCreate} />
    </div>
  )
}
