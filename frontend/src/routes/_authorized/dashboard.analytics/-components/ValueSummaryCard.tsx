import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
} from "@/components/ui/card.tsx"
import { Spinner } from "@/components/ui/spinner.tsx"
import { cn, formatNumber } from "@/lib/utils.ts"

export function ValueSummaryCard({
  value,
  name,
  loading,
  updating,
  className,
}: {
  value: number | undefined
  name: string
  loading: boolean
  updating: boolean
  className?: string
}) {
  const valueText = value !== undefined ? formatNumber(value) : "no data"

  return (
    <Card className={cn("@container/card", className)}>
      <CardContent className="mx-auto my-auto">
        <CardDescription className="text-center">{name}</CardDescription>
        <CardTitle
          className={cn(
            "text-center text-6xl font-semibold",
            updating ? "opacity-50" : ""
          )}
        >
          {value === undefined && loading ? <Spinner /> : valueText}
        </CardTitle>
      </CardContent>
    </Card>
  )
}
