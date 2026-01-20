import { createFileRoute, Outlet } from "@tanstack/react-router"

export const Route = createFileRoute("/_authorized/dashboard/analytics")({
  component: LinksPage,
  beforeLoad: () => ({
    pageTitle: "Links",
  }),
})

function LinksPage() {
  return <Outlet />
}
