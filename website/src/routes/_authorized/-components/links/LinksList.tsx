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

type Props = {
  links: ShortieLink[]
  hasMore: boolean
  onCreateLink: () => void
  fetchNextPage: () => void
}

export function LinksList({
  onCreateLink,
  links,
  hasMore,
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
            {!links.length && (
              <TableRow>
                <TableCell colSpan={4}>
                  <EmptyUrlList onCreateLink={onCreateLink} />
                </TableCell>
              </TableRow>
            )}

            {links.map((link) => {
              const isExpired =
                link.expiryDate && new Date(link.expiryDate) < new Date()

              return (
                <TableRow key={link.shortCode}>
                  {/* Short code */}
                  <TableCell className="font-medium">
                    <a
                      href={`http://localhost:8080/${link.shortCode}`}
                      target="_blank"
                      rel="noreferrer"
                      className="text-primary hover:underline"
                    >
                      {link.shortCode}
                    </a>
                  </TableCell>

                  {/* Original URL */}
                  <TableCell className="max-w-[420px] truncate">
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
                  <TableCell>
                    <Badge variant={link.protected ? "secondary" : "outline"}>
                      {link.protected ? "Protected" : "Public"}
                    </Badge>
                  </TableCell>

                  {/* Expiry */}
                  <TableCell>
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
            })}
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
