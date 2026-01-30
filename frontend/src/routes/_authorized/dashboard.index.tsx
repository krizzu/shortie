import { createFileRoute } from "@tanstack/react-router"
import { DashboardWidget } from "@/routes/_authorized/-components/dashboard/DashboardWidget.tsx"

export const Route = createFileRoute("/_authorized/dashboard/")({
  component: DashboardIndex,
  beforeLoad: () => ({
    pageTitle: "Dashboard",
  }),
})

function DashboardIndex() {
  return (
    <div className="grid grid-cols-4 gap-4">
      <DashboardWidget
        title="URLs"
        icon="links"
        description="Manage links"
        linkTo={"/dashboard/urls"}
      />
      <DashboardWidget
        title="Analytics"
        icon="analytics"
        description="See analytic data"
        linkTo={"/dashboard/analytics"}
      />
    </div>
  )
}
