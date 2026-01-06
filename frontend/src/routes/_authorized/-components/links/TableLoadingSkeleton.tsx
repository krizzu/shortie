import React from "react"
import { TableCell, TableRow } from "@/components/ui/table.tsx"
import { cn } from "@/lib/utils.ts"
import { Skeleton } from "@/components/ui/skeleton.tsx"

type Props = { rows: number; columns: number; className?: string }

const TableLoadingSkeleton: React.FC<Props> = (props) => {
  const rows = new Array(props.rows).fill(true)
  const columns = new Array(props.columns).fill(true)

  return rows.map((_, rowIndex) => (
    <TableRow className={cn(props.className)} key={rowIndex}>
      {columns.map((_, cellIndex) => (
        <TableCell key={cellIndex}>
          <Skeleton className="h-6 w-full" />
        </TableCell>
      ))}
    </TableRow>
  ))
}

export default TableLoadingSkeleton
