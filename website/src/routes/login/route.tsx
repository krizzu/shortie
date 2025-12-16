import React from "react"
import { createFileRoute, redirect } from "@tanstack/react-router"
import { useAuth } from "@/auth/AuthProvider.tsx"
import type { HttpError } from "@/services/fetcher.ts"
import { LoginForm } from "@/routes/login/-components/LoginForm.tsx"

export const Route = createFileRoute("/login")({
  component: LoginPage,

  beforeLoad: ({ context }) => {
    // Redirect if already authenticated
    if (context.auth.authenticated) {
      throw redirect({ to: "/dashboard" })
    }
  },
})

export function LoginPage(props: React.ComponentProps<"div">) {
  const auth = useAuth()
  const navigate = Route.useNavigate()

  async function login(user: string, password: string) {
    try {
      await auth.login(user, password)
      navigate({ to: "/dashboard" })
    } catch (e: unknown) {
      alert((e as HttpError).message)
    }
  }

  return (
    <div
      className="flex min-h-svh w-full items-center justify-center p-6 md:p-10"
      {...props}
    >
      <div className="w-full max-w-sm">
        <LoginForm onLogin={login} />
      </div>
    </div>
  )
}
