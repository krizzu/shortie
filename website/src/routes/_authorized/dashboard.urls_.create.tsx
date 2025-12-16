import { createFileRoute } from "@tanstack/react-router"
import { CreateURLForm } from "@/routes/_authorized/-components/links/CreateURLForm.tsx"

export const Route = createFileRoute("/_authorized/dashboard/urls_/create")({
  component: CreateLink,
  context: () => ({
    pageTitle: "Create URL",
  }),
})

function CreateLink() {
  return <CreateURLForm />
}
