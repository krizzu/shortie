import { createFileRoute } from "@tanstack/react-router"

export const Route = createFileRoute("/_authorized/dashboard/analytics/")({
  component: RouteComponent,
})

function RouteComponent() {
  return (
    <div>
       main analytics page:
      - total links
      - total clicks
      - avg clicks
    </div>
  )
}
