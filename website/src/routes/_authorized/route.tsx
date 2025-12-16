import { createFileRoute, Outlet, redirect } from "@tanstack/react-router"

export const Route = createFileRoute("/_authorized")({
  beforeLoad: ({ context }) => {
    if (!context.auth.authenticated) {
      throw redirect({
        to: "/login",
      })
    }
  },
  component: () => <Outlet />,
})
