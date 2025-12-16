import { createFileRoute } from "@tanstack/react-router"
import { EmptyUrlList } from "./-components/links/EmptyUrlList.tsx"

export const Route = createFileRoute("/_authorized/dashboard/urls")({
  component: LinksPage,
  beforeLoad: () => ({
    pageTitle: "Links",
  }),
})

function LinksPage() {
  const navigate = Route.useNavigate()

  function navigateToCreate() {
    navigate({ to: "/dashboard/urls/create" })
  }

  return (
    <div>
      <EmptyUrlList onCreateLink={navigateToCreate} />
    </div>
  )
}
