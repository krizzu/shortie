import { dateToUtcDateString } from "@/lib/utils.ts"

/**
 * creates a list of dates, inclusive, from start to end
 * this assumes end date did not happen before end date
 * @param startDateString
 * @param endDateRange
 */
export function createDateRange(
  startDateString: string,
  endDateRange: string
): string[] {
  const current = new Date(startDateString + "T00:00:00Z")
  const end = new Date(endDateRange + "T00:00:00Z")
  const range: string[] = []

  while (current <= end) {
    range.push(dateToUtcDateString(current))
    current.setUTCDate(current.getUTCDate() + 1)
  }

  return range
}
