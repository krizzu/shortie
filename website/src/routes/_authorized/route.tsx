import { createFileRoute, Outlet, redirect } from "@tanstack/react-router"

export const Route = createFileRoute("/_authorized")({
  beforeLoad: ({ context, location }) => {
    console.log("auth?!", context.auth)
    if (!context.auth.authenticated) {
      throw redirect({
        to: "/login",
        search: {
          // Save current location for redirect after login
          redirect: location.href,
        },
      })
    }
  },
  component: () => <Outlet />,
})
