import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Field } from "@/components/ui/field"
import { Popover, PopoverAnchor, PopoverContent } from "@/components/ui/popover"
import { format } from "date-fns"
import { CalendarIcon } from "lucide-react"
import { type DateRange } from "react-day-picker"
import { useState } from "react"

type Props = {
  initial: { from: Date; to: Date }
  onDateSelected: (dates: { from: Date; to: Date }) => void
}

export function DatePickerWithRange(props: Props) {
  const [open, setOpen] = useState(false)
  const [date, setDate] = useState<DateRange | undefined>(props.initial)
  const [lastAcceptedDate, setLastAccepted] = useState(date)

  const confirmDates = () => {
    const { from, to } = date ?? {}
    if (from && to) {
      props.onDateSelected({ from, to })
      setLastAccepted({ from, to })
    }
  }

  return (
    <Field className="w-60">
      <Popover
        open={open}
        onOpenChange={(isOpen) => {
          if (!isOpen) {
            setOpen(false)
            setDate(lastAcceptedDate)
          }
        }}
      >
        <Button
          variant="outline"
          id="date-picker-range"
          className="justify-start px-2.5 font-normal"
          onClick={() => {
            setOpen((o) => !o)
          }}
        >
          <CalendarIcon />
          {date?.from ? (
            date?.to ? (
              <>
                {format(date.from, "LLL dd, y")} -{" "}
                {format(date.to, "LLL dd, y")}
              </>
            ) : (
              format(date.from, "LLL dd, y")
            )
          ) : (
            <span>Pick a date</span>
          )}
        </Button>
        <PopoverAnchor />

        <PopoverContent className="w-auto p-0" align="start">
          <Calendar
            mode="range"
            defaultMonth={date?.from}
            selected={date}
            onSelect={setDate}
            numberOfMonths={2}
            timeZone="UTC"
          />
          <div className="flex self-end pb-2 pr-4">
            <Button disabled={!date?.from || !date?.to} onClick={confirmDates}>
              Select dates
            </Button>
          </div>
        </PopoverContent>
      </Popover>
    </Field>
  )
}
