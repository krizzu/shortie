import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Field, FieldGroup, FieldLabel } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { useFormStatus } from "react-dom"

interface Props {
  className?: string
  onLogin: (user: string, password: string) => Promise<void>
}

const DEFAULT_USER = "admin"
export function LoginForm({ className, onLogin }: Props) {
  const { pending } = useFormStatus()

  async function submit(form: FormData) {
    const password = form.get("password")
    await onLogin(DEFAULT_USER, String(password))
  }

  return (
    <div className={cn("flex flex-col gap-6", className)}>
      <Card>
        <CardHeader>
          <CardTitle>Sign in to Shortie</CardTitle>
        </CardHeader>
        <CardContent>
          <form action={submit}>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="user">Username</FieldLabel>
                <Input
                  name="user"
                  id="user"
                  value={DEFAULT_USER}
                  type="text"
                  disabled
                />
              </Field>
              <Field>
                <FieldLabel htmlFor="password">Password</FieldLabel>
                <Input name="password" id="password" type="password" required />
              </Field>
              <Field>
                <Button disabled={pending} type="submit">
                  {pending ? "Submitting..." : "Login"}
                </Button>
              </Field>
            </FieldGroup>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
