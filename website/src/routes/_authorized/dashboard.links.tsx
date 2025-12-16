import { createFileRoute } from "@tanstack/react-router"

export const Route = createFileRoute("/_authorized/dashboard/links")({
  component: LinksPage,
  beforeLoad: () => ({
    pageTitle: "Links",
  }),
})

function LinksPage() {
  return (
    <div>
      <p>Links!</p>
    </div>
  )
}
