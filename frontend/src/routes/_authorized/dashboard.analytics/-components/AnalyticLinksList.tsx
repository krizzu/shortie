import type { ShortieLinkAnalytic } from "@/types/Link.ts"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table.tsx"
import TableLoadingSkeleton from "@/routes/_authorized/-components/links/TableLoadingSkeleton.tsx"
import { EnvVars } from "@/services/env-vars.ts"
import { Button } from "@/components/ui/button.tsx"
import { ChevronRight } from "lucide-react"
import { cn } from "@/lib/utils"

type Props = {
  loading: boolean
  data: ShortieLinkAnalytic[]
  onViewDetails: (link: ShortieLinkAnalytic) => void
  className?: string
}

export function AnalyticLinksList({
  loading,
  data,
  onViewDetails,
  className,
}: Props) {
  return (
    <Table className={cn("rounded-2xl border bg-background ", className)}>
      {loading ? null : (
        <>
          <TableHeader>
            <TableRow className="h-6">
              <TableHead>Short URL</TableHead>
              <TableHead>Total clicks</TableHead>
              <TableHead />
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? <TableLoadingSkeleton rows={10} columns={4} /> : null}

            {!loading
              ? data.map((link) => {
                  const url = EnvVars.redirectUrl(link.shortCode)

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
                        {link.totalClicks}
                      </TableCell>

                      <TableCell className="h-6">
                        <Button
                          onClick={() => {
                            onViewDetails(link)
                          }}
                          variant="ghost"
                          className="rounded-2xl"
                        >
                          details
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
  )
}
