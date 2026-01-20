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
import { LucideChartColumnIncreasing, LucideTrash, Plus } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination.tsx"
import TableLoadingSkeleton from "@/routes/_authorized/-components/links/TableLoadingSkeleton.tsx"
import { EnvVars } from "@/services/env-vars.ts"
import { ConfirmationAlert } from "@/routes/_authorized/-components/dashboard/ConfirmationAlert.tsx"
import { useState } from "react"

type Props = {
  links: ShortieLink[]
  loading: boolean
  onCreateLink: () => void
  onDeleteLink: (link: ShortieLink) => Promise<void>
  fetchNextPage: (() => void) | null
  goToPreviousPage: (() => void) | null
  goToCodeAnalytics: (code: ShortieLink) => void
}

export function LinksList({
  onCreateLink,
  links,
  loading,
  fetchNextPage,
  onDeleteLink,
  goToCodeAnalytics,
  goToPreviousPage,
}: Props) {
  const [toDelete, setToDelete] = useState<ShortieLink | null>(null)
  const [deleting, setDeleting] = useState(false)

  async function deleteLink(link: ShortieLink) {
    setDeleting(true)
    try {
      await onDeleteLink(link)
      setToDelete(null)
    } catch (e) {
      const message =
        (e as Error)?.message ?? "failed to delete link - see console"
      alert(message)
    } finally {
      setDeleting(false)
    }
  }

  return (
    <div className="space-y-6 pb-6">
      <div className="flex flex-row gap-x-16">
        <Button onClick={onCreateLink} variant="default">
          <Plus /> Add new
        </Button>

        <div className="flex flex-row gap-x-6">
          {goToPreviousPage ? (
            <div>
              <PageButton type="previous" onClick={goToPreviousPage} />
            </div>
          ) : null}

          {fetchNextPage ? (
            <div>
              <PageButton type="next" onClick={fetchNextPage} />
            </div>
          ) : null}
        </div>
      </div>

      <div>
        <Table className="rounded-2xl border bg-background">
          <TableHeader>
            <TableRow>
              <TableHead>Code</TableHead>
              <TableHead>Original URL</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Expires</TableHead>
              <TableHead />
            </TableRow>
          </TableHeader>

          <TableBody>
            {loading ? <TableLoadingSkeleton rows={10} columns={5} /> : null}

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
                      <TableCell className="max-w-[150px] h-6 truncate">
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
                      <TableCell className="h-6 max-w-[60px]">
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
                      <TableCell className="h-6 space-x-2">
                        <Button
                          onClick={() => goToCodeAnalytics(link)}
                          variant="secondary"
                          className="rounded-2xl"
                        >
                          <LucideChartColumnIncreasing />
                        </Button>

                        <Button
                          onClick={() => setToDelete(link)}
                          variant="destructive"
                          className="rounded-2xl"
                        >
                          <LucideTrash className="text-red-500" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  )
                })
              : null}
          </TableBody>
        </Table>
      </div>
      <ConfirmationAlert
        confirmLoading={deleting}
        title="Delete shortcode"
        description={`Are you sure you want to delete this short link?`}
        visible={!!toDelete}
        onConfirm={() => {
          if (toDelete) {
            deleteLink(toDelete)
          }
        }}
        onClose={() => setToDelete(null)}
      />
    </div>
  )
}

function PageButton({
  onClick,
  type,
}: {
  type: "next" | "previous"
  onClick: () => void
}) {
  const Element = type === "previous" ? PaginationPrevious : PaginationNext
  return (
    <Pagination>
      <PaginationContent>
        <PaginationItem>
          <Element onClick={onClick} />
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
