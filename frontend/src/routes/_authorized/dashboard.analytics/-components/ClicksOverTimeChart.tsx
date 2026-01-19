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
import type { ShortieLinkAnalytic } from "@/types/Link.ts"

export function ClicksOverTimeChart({
  shortie,
  startDate,
  endDate,
  className,
}: {
  shortie: ShortieLinkAnalytic
  startDate: string
  endDate: string
  className?: string
}) {
  const chartData = getChartData(shortie)

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle>Clicks over time</CardTitle>
        <CardDescription>
          {startDate} - {endDate}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <ChartContainer config={chartConfig}>
          <BarChart  accessibilityLayer data={chartData}>
            <CartesianGrid vertical={false} />
            <XAxis
              dataKey="date"
              tickLine={false}
              tickMargin={10}
              axisLine={false}
              tickFormatter={(value) => value}
            />
            <ChartTooltip
              cursor={true}
              content={<ChartTooltipContent />}
            />
            <Bar dataKey="clicks" fill="var(--color-chart-1)" radius={8} />
          </BarChart>
        </ChartContainer>
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

function getChartData(
  shortie: ShortieLinkAnalytic
): { date: string; clicks: number }[] {
  const data: { date: string; clicks: number }[] = []

  shortie.details.forEach((_, date, clicks) => {
    data.push({ date, clicks: clicks.get(date) ?? 0 })
  })

  return data
}
