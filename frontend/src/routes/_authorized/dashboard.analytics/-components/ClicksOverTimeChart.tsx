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
  linkClicks: { date: string; clicks: number }[] | undefined
  dateDataKey?: string
  clicksDataKey?: string
  loading: boolean
  updating: boolean
  startDate: string
  endDate: string
  className?: string
}) {
  // const chartData = getChartData(linkDetails)

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

const chartConfig = {
  desktop: {
    label: "clicks",
    color: "var(--chart-1)",
  },
} satisfies ChartConfig
