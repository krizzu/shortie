import { EmptyUrlList } from "@/routes/_authorized/-components/links/EmptyUrlList.tsx"

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge.tsx"
import { cn } from "@/lib/utils.ts"
import type { ShortieLink } from "@/types/Link.ts"
import { Plus } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
} from "@/components/ui/pagination.tsx"
import TableLoadingSkeleton from "@/routes/_authorized/-components/links/TableLoadingSkeleton.tsx"
import { EnvVars } from "@/services/env-vars.ts"

type Props = {
  links: ShortieLink[]
  loading: boolean
  hasMore: boolean
  onCreateLink: () => void
  fetchNextPage: () => void
}

export function LinksList({
  onCreateLink,
  links,
  hasMore,
  loading,
  fetchNextPage,
}: Props) {
  return (
    <div className="space-y-6 pb-6">
      <div>
        <Button onClick={onCreateLink} variant="default">
          <Plus /> Add new
        </Button>
      </div>

      <div>
        <Table className="rounded-2xl border bg-background">
          <TableHeader>
            <TableRow>
              <TableHead>Code</TableHead>
              <TableHead>Original URL</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Expires</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {loading ? <TableLoadingSkeleton rows={10} columns={4} /> : null}

            {!loading && !links.length ? (
              <TableRow>
                <TableCell className="h-6" colSpan={4}>
                  <EmptyUrlList onCreateLink={onCreateLink} />
                </TableCell>
              </TableRow>
            ) : null}

            {!loading
              ? links.map((link) => {
                  const url = EnvVars.redirectUrl(link.shortCode)

                  const isExpired =
                    link.expiryDate && new Date(link.expiryDate) < new Date()

                  return (
                    <TableRow key={link.shortCode}>
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

                      {/* Original URL */}
                      <TableCell className="max-w-[420px] h-6 truncate">
                        <a
                          href={link.originalUrl}
                          target="_blank"
                          rel="noreferrer"
                          className="hover:underline"
                          title={link.originalUrl}
                        >
                          {link.originalUrl}
                        </a>
                      </TableCell>

                      {/* Protection status */}
                      <TableCell className="h-6">
                        <Badge
                          variant={link.protected ? "secondary" : "outline"}
                        >
                          {link.protected ? "Protected" : "Public"}
                        </Badge>
                      </TableCell>

                      {/* Expiry */}
                      <TableCell className="h-6">
                        {link.expiryDate ? (
                          <span className={cn(isExpired && "text-destructive")}>
                            {formatDate(link.expiryDate)}
                          </span>
                        ) : (
                          <span className="text-muted-foreground">Never</span>
                        )}
                      </TableCell>
                    </TableRow>
                  )
                })
              : null}
          </TableBody>
        </Table>
        {hasMore ? (
          <div className="mt-8">
            <NextPage onClick={() => fetchNextPage()} />
          </div>
        ) : null}
      </div>
    </div>
  )
}

function NextPage({ onClick }: { onClick: () => void }) {
  return (
    <Pagination>
      <PaginationContent>
        <PaginationItem>
          <PaginationNext onClick={onClick} />
        </PaginationItem>
      </PaginationContent>
    </Pagination>
  )
}

function formatDate(utc: string) {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
  }).format(new Date(utc))
}
