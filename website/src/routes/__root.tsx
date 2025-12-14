// noinspection JSUnusedGlobalSymbols

import {
  createRootRouteWithContext,
  HeadContent,
  Outlet,
} from "@tanstack/react-router"
import { TanStackRouterDevtools } from "@tanstack/react-router-devtools"
import { type AuthState } from "../auth/AuthProvider.tsx"

const RootLayout = () => (
  <>
    <HeadContent />
    <Outlet />
    <TanStackRouterDevtools />
  </>
)

interface RootRouteContext {
  auth: AuthState
}

export const Route = createRootRouteWithContext<RootRouteContext>()({
  component: RootLayout,
  notFoundComponent: () => <p>Page not found</p>,
  head: () => ({
    meta: [
      { title: "Shortie dashboard" },
      {
        name: "description",
        content: "dashboard",
      },
    ],
  }),
})
