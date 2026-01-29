import { createFileRoute } from "@tanstack/react-router"
import { useState } from "react"
import { useMutation } from "@tanstack/react-query"
import { updateLinkMutationOptions } from "@/queries/update-link-mutation-options.ts"
import { toast } from "sonner"
import { UpdatedLink } from "@/routes/_authorized/-components/links/UpdatedLink.tsx"
import { UpdateLinkForm } from "@/routes/_authorized/-components/links/UpdateLinkForm.tsx"

export const Route = createFileRoute(
  "/_authorized/dashboard/urls/edit/$shortCode"
)({
  component: EditLink,
  context: () => ({
    pageTitle: "Edit URL",
  }),
})

function EditLink() {
  const { shortCode } = Route.useParams()
  const navigate = Route.useNavigate()
  const [updated, setUpdated] = useState<string | null>(null)
  const updateMutation = useMutation(updateLinkMutationOptions)

  async function updateLink(payload: {
    expiry?: string | null // utc string
    password?: string | null
  }) {
    try {
      const result = await updateMutation.mutateAsync({
        shortCode,
        password: payload.password,
        expiry: payload.expiry,
      })
      setUpdated(result.shortCode)
    } catch (e) {
      console.error(e)
      toast.error(`failed to update: ${(e as Error).message}`)
    }
  }

  return (
    <div className="w-full max-w-md mx-auto">
      {updated ? (
        <UpdatedLink
          shortCode={updated}
          onShowList={() => navigate({ to: "/dashboard/urls" })}
        />
      ) : (
        <UpdateLinkForm onSubmit={updateLink} />
      )}
    </div>
  )
}
