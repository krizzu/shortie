import { createFileRoute } from "@tanstack/react-router"
import { CreateURLForm } from "@/routes/_authorized/-components/links/CreateURLForm.tsx"
import { fetcher, HttpError } from "@/services/fetcher.ts"
import { useState } from "react"
import { CreatedLink } from "@/routes/_authorized/-components/links/CreatedLink.tsx"

export const Route = createFileRoute("/_authorized/dashboard/urls/create")({
  component: CreateLink,
  context: () => ({
    pageTitle: "Create URL",
  }),
})

function CreateLink() {
  const navigate = Route.useNavigate()
  const [created, setCreated] = useState<string | null>(null)

  async function createLink(link: {
    originalUrl: string
    alias: string | null
    expiry: string | null // utc string
    password: string | null
  }) {
    try {
      const result = await fetcher<{ shortCode: string }>("/urls", {
        method: "POST",
        body: {
          url: link.originalUrl,
          expiryDate: link.expiry,
          password: link.password,
          alias: link.alias,
        },
      })
      setCreated(result.data.shortCode)
    } catch (e) {
      alert((e as HttpError).message)
      console.error(e)
    }
  }

  return (
    <div className="w-full max-w-md mx-auto">
      {created ? (
        <CreatedLink
          shortCode={created}
          onCreateNew={() => setCreated(null)}
          onShowList={() => navigate({ to: "/dashboard/urls" })}
        />
      ) : (
        <CreateURLForm onSubmit={createLink} />
      )}
    </div>
  )
}
