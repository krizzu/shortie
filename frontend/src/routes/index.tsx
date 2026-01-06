import { createFileRoute, redirect } from "@tanstack/react-router"

export const Route = createFileRoute("/")({
  beforeLoad: ({ context }) => {
    if (!context.auth.authenticated) {
      throw redirect({
        to: "/login",
      })
    }

    throw redirect({ to: "/dashboard" })
  },
})
