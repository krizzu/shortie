/* eslint-disable react-refresh/only-export-components */
import { StrictMode } from "react"
import ReactDOM from "react-dom/client"
import { createRouter, RouterProvider } from "@tanstack/react-router"

import { routeTree } from "./routeTree.gen"
import { AuthProvider, useAuth } from "./auth/AuthProvider.tsx"

// Create a new router instance
const router = createRouter({
  routeTree,
  context: {
    auth: undefined!,
  },
})

// Register the router instance for type safety
declare module "@tanstack/react-router" {
  interface Register {
    router: typeof router
  }
}

function RouterWithAuth() {
  const auth = useAuth()
  return <RouterProvider router={router} context={{ auth }} />
}

// Render the app
const rootElement = document.getElementById("root")!
if (!rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement)
  root.render(
    <StrictMode>
      <AuthProvider>
        <RouterWithAuth />
      </AuthProvider>
    </StrictMode>
  )
}
