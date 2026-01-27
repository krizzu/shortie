import { createFileRoute } from "@tanstack/react-router"
import { useQuery } from "@tanstack/react-query"
import { linksQueryOptions } from "@/queries/links-query-options.ts"
import { linkAnalyticsOverviewQueryOptions } from "@/queries/links-analytics-overview-query-options.ts"
import { ValueSummaryCard } from "@/routes/_authorized/dashboard.analytics/-components/ValueSummaryCard.tsx"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table.tsx"
import { cn } from "@/lib/utils.ts"
import TableLoadingSkeleton from "@/routes/_authorized/-components/links/TableLoadingSkeleton.tsx"
import { EnvVars } from "@/services/env-vars.ts"
import { Badge } from "@/components/ui/badge.tsx"
import { Button } from "@/components/ui/button.tsx"
import { ChevronRight } from "lucide-react"
import type { ShortieLink } from "@/types/Link.ts"

const DEFAULT_LIMIT = 20

export const Route = createFileRoute("/_authorized/dashboard/analytics/")({
  validateSearch: (
    raw
  ): { limit?: number; page?: string; previous?: string[] } => {
    return {
      limit: isNaN(Number(raw["limit"])) ? DEFAULT_LIMIT : Number(raw["limit"]),
      page: raw["page"] ? String(raw["page"]) : undefined,
      previous: raw["previous"]
        ? String(raw["previous"]).split(",")
        : undefined,
    }
  },

  loaderDeps: ({ search }) => {
    return {
      limit: search.limit,
      page: search.page,
    }
  },

  loader: ({ context, deps }) => {
    context.queryClient.ensureQueryData(
      linksQueryOptions(deps.page, deps.limit)
    )
    context.queryClient.ensureQueryData(linkAnalyticsOverviewQueryOptions())
  },

  component: RouteComponent,
})

function RouteComponent() {
  const deps = Route.useLoaderDeps()
  const links = useQuery(linksQueryOptions(deps.page, deps.limit))
  const overview = useQuery(linkAnalyticsOverviewQueryOptions())
  const navigate = Route.useNavigate()

  function viewLinkDetails(link: ShortieLink) {
    navigate({
      to: "/dashboard/analytics/$shortCode",
      params: { shortCode: link.shortCode },
    })
  }

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-4 gap-x-4">
        <ValueSummaryCard
          value={overview.data?.totalLinks}
          name="total links"
          loading={overview.isLoading}
          updating={overview.isFetching}
        />
        <ValueSummaryCard
          value={overview.data?.totalClicks}
          name="total clicks"
          loading={overview.isLoading}
          updating={overview.isFetching}
        />
        <ValueSummaryCard
          value={overview.data?.activeLinks}
          name="active links"
          loading={overview.isLoading}
          updating={overview.isFetching}
        />
        <ValueSummaryCard
          value={overview.data?.expiredLinks}
          name="expired links"
          loading={overview.isLoading}
          updating={overview.isFetching}
        />
      </div>
      <div className="grid grid-cols-3">
        <Table className="rounded-2xl border bg-background col-span-2">
          {links.isLoading ? null : (
            <>
              <TableHeader>
                <TableRow className="h-6">
                  <TableHead>Short URL</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Expires</TableHead>
                  <TableHead />
                </TableRow>
              </TableHeader>
              <TableBody>
                {links.isLoading ? (
                  <TableLoadingSkeleton rows={10} columns={4} />
                ) : null}

                {!links.isLoading
                  ? (links.data?.data ?? []).map((link) => {
                      const url = EnvVars.redirectUrl(link.shortCode)

                      const isExpired =
                        link.expiryDate &&
                        new Date(link.expiryDate) < new Date()

                      return (
                        <TableRow className="h-6" key={link.shortCode}>
                          {/* Short code */}
                          <TableCell className="h-6 font-medium">
                            <a
                              href={url}
                              target="_blank"
                              rel="noreferrer"
                              className="text-primary hover:underline"
                            >
                              {link.shortCode}
                            </a>
                          </TableCell>

                          <TableCell className="h-6 max-w-[60px]">
                            <Badge
                              variant={link.protected ? "secondary" : "outline"}
                            >
                              {link.protected ? "Protected" : "Public"}
                            </Badge>
                          </TableCell>

                          <TableCell className="h-6">
                            {link.expiryDate ? (
                              <span
                                className={cn(isExpired && "text-destructive")}
                              >
                                {formatDate(link.expiryDate)}
                              </span>
                            ) : (
                              <span className="text-muted-foreground">
                                Never
                              </span>
                            )}
                          </TableCell>
                          <TableCell className="h-6">
                            <Button
                              onClick={() => {
                                viewLinkDetails(link)
                              }}
                              variant="secondary"
                              className="rounded-2xl"
                            >
                              see details
                              <ChevronRight />
                            </Button>
                          </TableCell>
                        </TableRow>
                      )
                    })
                  : null}
              </TableBody>
            </>
          )}
        </Table>
      </div>
    </div>
  )
}

function formatDate(utc: string) {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
  }).format(new Date(utc))
}
