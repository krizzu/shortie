import { createFileRoute } from "@tanstack/react-router"

export const Route = createFileRoute(
  "/_authorized/dashboard/analytics/$shortCode"
)({
  component: RouteComponent,
})

function RouteComponent() {
  const { shortCode } = Route.useParams()

  return (
    <div>
      <p>hello to {shortCode} analytics</p>
    </div>
  )
}
