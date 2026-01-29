import {
  Field,
  FieldGroup,
  FieldLabel,
  FieldLegend,
  FieldSeparator,
  FieldSet,
} from "@/components/ui/field.tsx"
import { Input } from "@/components/ui/input.tsx"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select.tsx"
import { Checkbox } from "@/components/ui/checkbox.tsx"
import { Button } from "@/components/ui/button.tsx"
import { useState } from "react"
import { Spinner } from "@/components/ui/spinner.tsx"
import { Switch } from "@/components/ui/switch.tsx"

type Props = {
  onSubmit: (payload: {
    expiry: string | undefined | null // undefined = ignore, null = remove, string = set
    password: string | undefined | null // undefined = ignore, null = remove, string = set
  }) => Promise<void>
}

export function UpdateLinkForm({ onSubmit }: Props) {
  const [useExpiry, setUseExpiry] = useState(false)
  const [setExpiryValue, setSetExpiryValue] = useState(true) // true = set value, false = remove
  const [usePassword, setUsePassword] = useState(false)
  const [setPasswordValue, setSetPasswordValue] = useState(true) // true = set value, false = remove
  const [pending, setPending] = useState(false)

  const today = new Date(new Date().toUTCString())
  const defaultDate = new Date(
    Date.UTC(today.getFullYear(), today.getMonth(), today.getDate(), 0, 0, 0, 0)
  )
  defaultDate.setDate(today.getDate() + 1)

  const defaultYear = defaultDate.getFullYear()
  const defaultMonth = defaultDate.getMonth() + 1
  const defaultDay = defaultDate.getDate()

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()

    const form = e.currentTarget as HTMLFormElement
    const data = new FormData(form)

    // Determine expiry value: undefined = ignore, null = remove, string = set
    let expiry: string | undefined | null = undefined
    if (useExpiry) {
      if (setExpiryValue) {
        const year = Number(data.get("exp-year"))
        const month = Number(data.get("exp-month"))
        const day = Number(data.get("exp-day"))
        expiry = new Date(
          Date.UTC(year, month - 1, day, 0, 0, 0, 0)
        ).toISOString()
      } else {
        expiry = null // remove
      }
    }

    // Determine password value: undefined = ignore, null = remove, string = set
    let password: string | undefined | null = undefined
    if (usePassword) {
      if (setPasswordValue) {
        password = data.get("link-password") as string
      } else {
        password = null // remove
      }
    }

    setPending(true)
    try {
      await onSubmit({ password, expiry })
    } finally {
      setPending(false)
    }
  }

  return (
    <div className="w-full max-w-md">
      <form onSubmit={handleSubmit}>
        <FieldGroup className="flex gap-y-6">
          <div>
            <FieldSet>
              <FieldLegend className="mb-4">Update link</FieldLegend>

              {/* Expiry Date */}
              <FieldGroup>
                <Field>
                  <Field orientation="horizontal">
                    <Checkbox
                      id="expiry-checkbox"
                      checked={useExpiry}
                      onCheckedChange={(v) => setUseExpiry(!!v)}
                    />
                    <FieldLabel
                      htmlFor="expiry-checkbox"
                      className="font-normal"
                    >
                      Modify expiry date
                    </FieldLabel>
                  </Field>

                  {useExpiry && (
                    <div className="mt-3 space-y-3">
                      <Field orientation="horizontal" className="items-center">
                        <Switch
                          id="expiry-set-remove"
                          checked={setExpiryValue}
                          onCheckedChange={setSetExpiryValue}
                        />
                        <FieldLabel
                          htmlFor="expiry-set-remove"
                          className="font-normal"
                        >
                          {setExpiryValue
                            ? "Set new expiry date"
                            : "Remove expiry date"}
                        </FieldLabel>
                      </Field>

                      {setExpiryValue && (
                        <>
                          <FieldLabel className="mb-2 block">
                            Expiry date
                          </FieldLabel>
                          <div className="grid grid-cols-3 gap-4">
                            <Field>
                              <FieldLabel htmlFor="exp-day">Day</FieldLabel>
                              <Select
                                defaultValue={String(defaultDay).padStart(
                                  2,
                                  "0"
                                )}
                                name="exp-day"
                              >
                                <SelectTrigger id="exp-day">
                                  <SelectValue placeholder="DD" />
                                </SelectTrigger>
                                <SelectContent>
                                  {Array.from({ length: 31 }, (_, i) => {
                                    const d = i + 1
                                    const dd = String(d).padStart(2, "0")
                                    return (
                                      <SelectItem key={dd} value={dd}>
                                        {dd}
                                      </SelectItem>
                                    )
                                  })}
                                </SelectContent>
                              </Select>
                            </Field>

                            <Field>
                              <FieldLabel htmlFor="exp-month">Month</FieldLabel>
                              <Select
                                name="exp-month"
                                defaultValue={String(defaultMonth).padStart(
                                  2,
                                  "0"
                                )}
                              >
                                <SelectTrigger id="exp-month">
                                  <SelectValue placeholder="MM" />
                                </SelectTrigger>
                                <SelectContent>
                                  {Array.from({ length: 12 }, (_, i) => {
                                    const m = i + 1
                                    const mm = String(m).padStart(2, "0")
                                    return (
                                      <SelectItem key={mm} value={mm}>
                                        {mm}
                                      </SelectItem>
                                    )
                                  })}
                                </SelectContent>
                              </Select>
                            </Field>

                            <Field>
                              <FieldLabel htmlFor="exp-year">Year</FieldLabel>
                              <Select
                                defaultValue={String(defaultYear)}
                                name="exp-year"
                              >
                                <SelectTrigger id="exp-year">
                                  <SelectValue placeholder="YYYY" />
                                </SelectTrigger>
                                <SelectContent>
                                  {Array.from({ length: 6 }, (_, i) => {
                                    const y = defaultYear + i
                                    return (
                                      <SelectItem key={y} value={String(y)}>
                                        {y}
                                      </SelectItem>
                                    )
                                  })}
                                </SelectContent>
                              </Select>
                            </Field>
                          </div>
                        </>
                      )}
                    </div>
                  )}
                </Field>
              </FieldGroup>
            </FieldSet>
          </div>
          <div>
            <FieldSeparator className="mb-4" />
            {/* Password Protection */}
            <FieldSet>
              <FieldLegend>Password protection</FieldLegend>

              <FieldGroup>
                <Field orientation="horizontal">
                  <Checkbox
                    id="password-checkbox"
                    checked={usePassword}
                    onCheckedChange={(v) => setUsePassword(!!v)}
                  />
                  <FieldLabel
                    htmlFor="password-checkbox"
                    className="font-normal"
                  >
                    Modify password protection
                  </FieldLabel>
                </Field>

                {usePassword && (
                  <div className="mt-3 space-y-3">
                    <Field orientation="horizontal" className="items-center">
                      <Switch
                        id="password-set-remove"
                        checked={setPasswordValue}
                        onCheckedChange={setSetPasswordValue}
                      />
                      <FieldLabel
                        htmlFor="password-set-remove"
                        className="font-normal"
                      >
                        {setPasswordValue
                          ? "Set new password"
                          : "Remove password"}
                      </FieldLabel>
                    </Field>

                    {setPasswordValue && (
                      <Field>
                        <FieldLabel htmlFor="link-password">
                          Password
                        </FieldLabel>
                        <Input
                          id="link-password"
                          name="link-password"
                          type="password"
                          placeholder="Enter password"
                          required
                        />
                      </Field>
                    )}
                  </div>
                )}
              </FieldGroup>
            </FieldSet>
          </div>
          <div>
            <Field orientation="horizontal">
              <Button disabled={pending || (!useExpiry && !usePassword)} type="submit">
                {pending ? (
                  <>
                    <Spinner /> Updating
                  </>
                ) : (
                  <>Update</>
                )}
              </Button>
            </Field>
          </div>
        </FieldGroup>
      </form>
    </div>
  )
}
