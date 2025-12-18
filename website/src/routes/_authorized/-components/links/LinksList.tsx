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
import { Spinner } from "@/components/ui/spinner.tsx"

type Props = {
  links: ShortieLink[]
  hasMore: boolean
  fetchingNextPage: boolean
  onCreateLink: () => void
  fetchNextPage: () => void
}

export function LinksList({
  onCreateLink,
  links,
  hasMore,
  fetchingNextPage,
  fetchNextPage,
}: Props) {
  return (
    <div className="space-y-6 pb-6">
      <div>
        <Button onClick={onCreateLink} variant="default">
          <Plus /> Add new
        </Button>
        {fetchingNextPage ? <Spinner /> : null}
      </div>

      <div className="rounded-2xl border bg-background">
        <Table>
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
            {hasMore ? (
              <TableRow>
                <TableCell className="text-center" colSpan={4}>
                  {fetchingNextPage ? (
                    <Spinner />
                  ) : (
                    <Button onClick={() => fetchNextPage()} variant="link">
                      Load more
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ) : null}
          </TableBody>
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
