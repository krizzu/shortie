import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
} from "@/components/ui/card.tsx"
import type { ShortieLinkAnalytic } from "@/types/Link.ts"

export function LinkSummaryCard({ link }: { link: ShortieLinkAnalytic }) {
  return (
    <Card className="@container/card">
      <CardContent className="mx-auto my-auto">
        <CardDescription className="text-center">Total clicks</CardDescription>
        <CardTitle className="text-center text-6xl font-semibold">
          {link.totalClicks}
        </CardTitle>
      </CardContent>
      <CardContent>
        <CardDescription className="text-center">Last click</CardDescription>
        <CardDescription className="text-center text-gray-950">
          {link.lastClick ? formatDate(new Date(link.lastClick)) : "-"}
        </CardDescription>
      </CardContent>
    </Card>
  )
}

function formatDate(date: Date): string {
  return `${date.toDateString()} ${date.getHours()}:${date.getMinutes()}`
}
