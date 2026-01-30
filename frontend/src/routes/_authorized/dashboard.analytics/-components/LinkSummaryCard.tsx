import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
} from "@/components/ui/card.tsx"
import type { ShortieLinkAnalyticDetails } from "@/types/Link.ts"
import { Spinner } from "@/components/ui/spinner.tsx"
import { cn, formatNumber } from "@/lib/utils.ts"

export function LinkSummaryCard({
  link,
  loading,
  updating,
}: {
  link: ShortieLinkAnalyticDetails | undefined
  loading: boolean
  updating: boolean
}) {
  const lastClickDate = link?.lastClick
    ? formatDate(new Date(link.lastClick))
    : "no data"

  return (
    <Card className="@container/card">
      <CardContent className="mx-auto my-auto">
        <CardDescription className="text-center">
          Total clicks overall
        </CardDescription>
        <CardTitle
          className={cn(
            "text-center text-6xl font-semibold",
            updating ? "opacity-50" : ""
          )}
        >
          {!link && loading ? (
            <Spinner />
          ) : link?.totalClicks ? (
            formatNumber(link.totalClicks)
          ) : (
            "no data"
          )}
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
  let h: string | number = date.getHours()
  let m: string | number = date.getMinutes()
  if (h < 10) {
    h = `0${h}`
  }
  if (m < 10) {
    m = `0${m}`
  }

  return `${date.toDateString()} ${h}:${m}`
}
