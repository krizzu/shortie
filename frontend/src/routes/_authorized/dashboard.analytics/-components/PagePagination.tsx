import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"

type Props = {
  currentPage: number
  hasPreviousPage: boolean
  hasNextPage: boolean
  onNextPage: () => void
  onPreviousPage: () => void
}

export function PagePagination(props: Props) {
  return (
    <Pagination className="mx-0 w-auto">
      <PaginationContent>
        <PaginationItem>
          <PaginationPrevious
            className={
              props.hasPreviousPage
                ? ""
                : "opacity-50 cursor-default hover:bg-transparent"
            }
            onClick={props.hasPreviousPage ? props.onPreviousPage : undefined}
          />
        </PaginationItem>
        <PaginationItem>
          <PaginationLink isActive>{props.currentPage}</PaginationLink>
        </PaginationItem>

        <PaginationItem>
          <PaginationNext
            className={
              props.hasNextPage
                ? ""
                : "opacity-50 cursor-default hover:bg-transparent"
            }
            onClick={props.hasNextPage ? props.onNextPage : undefined}
          />
        </PaginationItem>
      </PaginationContent>
    </Pagination>
  )
}
