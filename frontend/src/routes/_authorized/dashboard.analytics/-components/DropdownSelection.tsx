import { Field, FieldLabel } from "@/components/ui/field"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { cn } from "@/lib/utils"

type Value = {
  name: string
  value: string
}

type Props = {
  selected?: Value
  placeholder?: string
  label: string
  onSelect: (value: Value) => void
  available: Value[]
  className?: string
}

export function DropdownSelection({
  selected,
  onSelect,
  label,
  placeholder,
  available,
  className,
}: Props) {
  function valueChange(value: string) {
    const index = available.findIndex((v) => v.value === value)
    const found = available[index]
    if (found) {
      onSelect(found)
    }
  }

  return (
    <Field
      orientation="horizontal"
      className={cn("flex w-fit gap-x-1 items-center", className)}
    >
      <FieldLabel className="flex-none!" htmlFor={`select-${label}`}>
        {label}
      </FieldLabel>

      <Select value={selected?.value} onValueChange={valueChange}>
        <SelectTrigger id={`select-${label}`}>
          <SelectValue placeholder={placeholder} />
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            <SelectLabel>{label}</SelectLabel>
            {available.map((v) => (
              <SelectItem key={v.value} value={v.value}>
                {v.name}
              </SelectItem>
            ))}
          </SelectGroup>
        </SelectContent>
      </Select>
    </Field>
  )
}
