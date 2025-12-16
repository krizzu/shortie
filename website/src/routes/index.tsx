import { createFileRoute, Link, useRouter } from "@tanstack/react-router"
import { Button } from "@/components/ui/button.tsx"

export const Route = createFileRoute("/")({
  component: Index,
})

function Index() {
  const { auth } = Route.useRouteContext()
  const r = useRouter()
  return (
    <div className="p-2 flex flex-col">
      <Link to="/login">login</Link>

      <Link to="/dashboard">dashboard</Link>

      {auth.authenticated ? (
        <Button
          variant="destructive"
          onClick={() => {
            auth.logout()
            r.invalidate()
          }}
        >
          Log out
        </Button>
      ) : null}
    </div>
  )
}
