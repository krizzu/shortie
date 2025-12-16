import { Link2Off } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/components/ui/empty"

type Props = {
  onCreateLink: () => void
}

export function EmptyLinkPage({ onCreateLink }: Props) {
  return (
    <Empty>
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <Link2Off className="size-12 bg-transparent" />
        </EmptyMedia>
        <EmptyTitle>No links yet</EmptyTitle>
        <EmptyDescription>
          You haven&apos;t created any links yet. Get started by creating your
          first link.
        </EmptyDescription>
      </EmptyHeader>
      <EmptyContent>
        <div className="flex gap-2">
          <Button onClick={() => onCreateLink()}>Create Link</Button>
        </div>
      </EmptyContent>
    </Empty>
  )
}
