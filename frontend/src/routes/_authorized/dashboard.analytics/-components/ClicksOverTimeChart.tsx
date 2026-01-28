import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Bar, BarChart, CartesianGrid, XAxis } from "recharts"
import {
  type ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart"
import { Spinner } from "@/components/ui/spinner.tsx"
import { cn } from "@/lib/utils.ts"

type LinksClick = { date: string; clicks: number }

export function ClicksOverTimeChart({
  linkClicks,
  loading,
  updating,
  startDate,
  endDate,
  className,
  dateDataKey = "date",
  clicksDataKey = "clicks",
}: {
  linkClicks: LinksClick[] | undefined
  dateDataKey?: string
  clicksDataKey?: string
  loading: boolean
  updating: boolean
  startDate: string
  endDate: string
  className?: string
}) {
  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle>Clicks over time</CardTitle>
        <CardDescription>
          {startDate} - {endDate}
        </CardDescription>
      </CardHeader>
      <CardContent>
        {loading || updating ? (
          <div className="flex justify-center h-24 items-center">
            <Spinner className="size-12 text-primary" />
          </div>
        ) : (
          <ChartContainer config={chartConfig}>
            {linkClicks ? (
              <BarChart accessibilityLayer data={linkClicks}>
                <CartesianGrid vertical={false} />
                <XAxis
                  dataKey={dateDataKey}
                  tickLine={false}
                  tickMargin={10}
                  axisLine={false}
                  tickFormatter={(value) => value}
                />
                <ChartTooltip cursor={true} content={<ChartTooltipContent />} />
                <Bar
                  dataKey={clicksDataKey}
                  fill="var(--color-chart-1)"
                  radius={8}
                />
              </BarChart>
            ) : (
              <div className="flex justify-center h-24 items-center">
                <h3 className="text-red-500 text-2xl">no data</h3>
              </div>
            )}
          </ChartContainer>
        )}
      </CardContent>
    </Card>
  )
}

export function ClicksOverTimeSummary({
  clicksInPeriod,
  linkClicks,
  loading,
  updating,
}: {
  clicksInPeriod: number | undefined
  linkClicks: LinksClick[] | undefined
  loading: boolean
  updating: boolean
}) {
  return (
    <Card className="@container/card">
      <CardContent className="mx-auto my-auto">
        <CardDescription className="text-center">
          Total clicks in period
        </CardDescription>
        <CardTitle
          className={cn(
            "text-center text-6xl font-semibold",
            updating ? "opacity-50" : ""
          )}
        >
          {!clicksInPeriod && loading ? (
            <Spinner />
          ) : (
            (clicksInPeriod ?? "no-data")
          )}
        </CardTitle>
      </CardContent>
      <CardContent>
        <CardDescription className="text-center">Total days</CardDescription>
        <CardDescription className="text-center text-gray-950">
          {linkClicks?.length ?? "no-data"}
        </CardDescription>
      </CardContent>
    </Card>
  )
}

const chartConfig = {
  desktop: {
    label: "clicks",
    color: "var(--chart-1)",
  },
} satisfies ChartConfig
