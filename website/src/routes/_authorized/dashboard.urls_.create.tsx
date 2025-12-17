import { createFileRoute } from "@tanstack/react-router"
import { CreateURLForm } from "@/routes/_authorized/-components/links/CreateURLForm.tsx"
import { fetcher, HttpError } from "@/services/fetcher.ts"

export const Route = createFileRoute("/_authorized/dashboard/urls_/create")({
  component: CreateLink,
  context: () => ({
    pageTitle: "Create URL",
  }),
})

function CreateLink() {
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
      console.log("good: ", result)
    } catch (e) {
      alert((e as HttpError).message)
      console.error(e)
    }
  }

  return <CreateURLForm onSubmit={createLink} />
}
