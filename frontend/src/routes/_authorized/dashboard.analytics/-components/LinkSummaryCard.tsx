import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
} from "@/components/ui/card.tsx"
import type { ShortieLinkAnalytic } from "@/types/Link.ts"
import { Spinner } from "@/components/ui/spinner.tsx"
import { cn } from "@/lib/utils.ts"

export function LinkSummaryCard({
  link,
  loading,
  updating,
}: {
  link: ShortieLinkAnalytic | undefined
  loading: boolean
  updating: boolean
}) {
  const lastClickDate = link?.lastClick
    ? formatDate(new Date(link.lastClick))
    : "no data"

  return (
    <Card className="@container/card">
      <CardContent className="mx-auto my-auto">
        <CardDescription className="text-center">Total clicks overall</CardDescription>
        <CardTitle
          className={cn(
            "text-center text-6xl font-semibold",
            updating ? "opacity-50" : ""
          )}
        >
          {!link && loading ? <Spinner /> : (link?.totalClicks ?? "no data")}
        </CardTitle>
      </CardContent>
      <CardContent>
        <CardDescription className="text-center">Last click</CardDescription>
        <CardDescription className="text-center text-gray-950">
          {loading ? null : lastClickDate}
        </CardDescription>
      </CardContent>
    </Card>
  )
}

function formatDate(date: Date): string {
  return `${date.toDateString()} ${date.getHours()}:${date.getMinutes()}`
}
