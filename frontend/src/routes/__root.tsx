// noinspection JSUnusedGlobalSymbols

import {
  createRootRouteWithContext,
  HeadContent,
  Outlet,
} from "@tanstack/react-router"

import { type AuthState } from "../auth/AuthProvider.tsx"
import type { QueryClient } from "@tanstack/react-query"

const RootLayout = () => (
  <>
    <HeadContent />
    <Outlet />
    {/*<TanStackRouterDevtools />*/}
  </>
)

interface RootRouteContext {
  auth: AuthState
  queryClient: QueryClient
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
