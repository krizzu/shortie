import {
  Field,
  FieldDescription,
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

export function CreateURLForm() {
  const [useAlias, setUseAlias] = useState(false)
  const [useExpiry, setUseExpiry] = useState(false)
  const [usePassword, setUsePassword] = useState(false)

  return (
    <div className="w-full max-w-md">
      <form>
        <FieldGroup>
          <FieldSet>
            <FieldLegend>Create a short URL</FieldLegend>

            {/* Original URL */}
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="original-url">Original URL</FieldLabel>
                <Input
                  id="original-url"
                  type="url"
                  placeholder="https://example.com"
                  required
                />
              </Field>
            </FieldGroup>

            {/* Custom Alias */}
            <FieldGroup>
              <Field>
                <Field orientation="horizontal">
                  <Checkbox
                    id="alias-checkbox"
                    checked={useAlias}
                    onCheckedChange={(v) => setUseAlias(!!v)}
                  />
                  <FieldLabel htmlFor="alias-checkbox" className="font-normal">
                    Use custom alias
                  </FieldLabel>
                </Field>

                {useAlias && (
                  <>
                    <FieldLabel htmlFor="custom-alias">Custom alias</FieldLabel>
                    <Input
                      id="custom-alias"
                      placeholder="my-short-link"
                      required={useAlias}
                    />
                    <FieldDescription>
                      This will be used instead of a random short code
                    </FieldDescription>
                  </>
                )}
              </Field>
            </FieldGroup>

            {/* Expiry Date */}
            <FieldGroup>
              <Field>
                <Field orientation="horizontal">
                  <Checkbox
                    id="expiry-checkbox"
                    checked={useExpiry}
                    onCheckedChange={(v) => setUseExpiry(!!v)}
                  />
                  <FieldLabel htmlFor="expiry-checkbox" className="font-normal">
                    Set expiry date
                  </FieldLabel>
                </Field>

                {useExpiry && (
                  <div className="mt-3">
                    <FieldLabel className="mb-2 block">Expiry date</FieldLabel>
                    <div className="grid grid-cols-2 gap-4">
                      <Field>
                        <FieldLabel htmlFor="exp-month">Month</FieldLabel>
                        <Select>
                          <SelectTrigger id="exp-month">
                            <SelectValue placeholder="MM" />
                          </SelectTrigger>
                          <SelectContent>
                            {Array.from({ length: 12 }, (_, i) => {
                              const m = String(i + 1).padStart(2, "0")
                              return (
                                <SelectItem key={m} value={m}>
                                  {m}
                                </SelectItem>
                              )
                            })}
                          </SelectContent>
                        </Select>
                      </Field>

                      <Field>
                        <FieldLabel htmlFor="exp-year">Year</FieldLabel>
                        <Select>
                          <SelectTrigger id="exp-year">
                            <SelectValue placeholder="YYYY" />
                          </SelectTrigger>
                          <SelectContent>
                            {Array.from({ length: 6 }, (_, i) => {
                              const y = String(new Date().getFullYear() + i)
                              return (
                                <SelectItem key={y} value={y}>
                                  {y}
                                </SelectItem>
                              )
                            })}
                          </SelectContent>
                        </Select>
                      </Field>
                    </div>
                  </div>
                )}
              </Field>
            </FieldGroup>
          </FieldSet>

          <FieldSeparator />

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
                <FieldLabel htmlFor="password-checkbox" className="font-normal">
                  Protect link with password
                </FieldLabel>
              </Field>

              {usePassword && (
                <Field>
                  <FieldLabel htmlFor="link-password">Password</FieldLabel>
                  <Input
                    id="link-password"
                    type="password"
                    placeholder="Enter password"
                    required={usePassword}
                  />
                </Field>
              )}
            </FieldGroup>
          </FieldSet>

          <Field orientation="horizontal">
            <Button type="submit">Create short URL</Button>
          </Field>
        </FieldGroup>
      </form>
    </div>
  )
}
