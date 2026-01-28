import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { CartesianGrid, Line, LineChart, XAxis } from "recharts"
import {
  type ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart"
import { Spinner } from "@/components/ui/spinner.tsx"
import type { ShortieAnalyticPeriodDetails } from "@/types/Link.ts"
import { createDateRange } from "@/routes/_authorized/dashboard.analytics/-utils/createDateRange.ts"

export function WeeklyLinearChart({
  data,
  loading,
  updating,
  className,
}: {
  data: ShortieAnalyticPeriodDetails | undefined
  loading: boolean
  updating: boolean
  className?: string
}) {
  const startDate = data?.startDate
  const endDate = data?.endDate
  const chartData = getChartData(data)

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle>Weekly overview</CardTitle>
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
            {chartData ? (
              <LineChart
                accessibilityLayer
                data={chartData}
                margin={{
                  left: 10,
                  right: 10,
                  top: 10,
                  bottom: 10,
                }}
              >
                <CartesianGrid vertical={false} />
                <XAxis
                  dataKey="date"
                  tickLine={false}
                  axisLine={false}
                  tickMargin={1}
                />
                <ChartTooltip
                  cursor={false}
                  content={<ChartTooltipContent hideLabel />}
                />
                <Line
                  dataKey="clicks"
                  type="linear"
                  stroke="var(--color-desktop)"
                  strokeWidth={2}
                  dot={true}
                />
              </LineChart>
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

function getChartData(
  details: ShortieAnalyticPeriodDetails | undefined
): { date: string; clicks: number }[] | undefined {
  if (!details) {
    return undefined
  }
  const content = details.clicksPerDate
  const data: { date: string; clicks: number }[] = []
  const range = createDateRange(details.startDate, details.endDate)

  for (const date of range) {
    data.push({
      date,
      clicks: content.find((d) => d.date === date)?.clicks ?? 0,
    })
  }

  return data
}

const chartConfig = {
  desktop: {
    label: "clicks",
    color: "var(--chart-1)",
  },
} satisfies ChartConfig
