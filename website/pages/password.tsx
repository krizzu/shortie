import ReactDOM from "react-dom/client"
import React, { useRef, useState } from "react"
import { Button } from "@/components/ui/button.tsx"
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
  FieldLegend,
  FieldSet,
} from "@/components/ui/field.tsx"
import { Input } from "@/components/ui/input.tsx"
import { Spinner } from "@/components/ui/spinner.tsx"
import { fetcher, HttpError } from "@/services/fetcher.ts"

// eslint-disable-next-line react-refresh/only-export-components
const PasswordForm = () => {
  const shortCode = readShortCode()
  const endpoint = new URL(`${shortCode}/password`, window.location.origin)
  const [pending, setPending] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState(false)
  const formRef = useRef<HTMLFormElement>(null)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    const data = new FormData(e.currentTarget as HTMLFormElement)
    setPending(true)
    setError(null)
    setSuccess(false)

    try {
      await fetcher(endpoint.href, {
        method: "POST",
        body: data,
        redirect: "follow",
        headers: {
          "Access-Control-Allow-Origin": "*", // allow to redirect
        },
      })
      setSuccess(true)
    } catch (e: unknown) {
      if (!(e instanceof HttpError)) {
        return alert(e)
      }
      if (e.statusCode === 404) {
        setError("Password incorrect or not required")
      } else {
        setError(`Request failed: ${e.message}`)
      }
    } finally {
      setPending(false)
      formRef.current?.reset()
    }
  }

  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">
      <form
        ref={formRef}
        onSubmit={handleSubmit}
        className="bg-white p-8 rounded shadow-md flex flex-col gap-4 w-full max-w-sm"
      >
        <FieldGroup>
          <FieldSet>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="password">Password required</FieldLabel>
                <Input id="password" name="password" type="password" required />
              </Field>
            </FieldGroup>
          </FieldSet>

          <Field orientation="vertical">
            {error ? <FieldError>{error}</FieldError> : null}
            {success ? (
              <FieldLegend className="text-green-600">
                Redirecting...
              </FieldLegend>
            ) : null}
            <Button disabled={pending || success} type="submit">
              {pending ? (
                <>
                  <Spinner />
                </>
              ) : (
                <>Submit</>
              )}
            </Button>
          </Field>
        </FieldGroup>
      </form>
    </div>
  )
}

function readShortCode(): string {
  const element = document.getElementById("root")!

  const code = element.getAttribute("data-shortCode")
  if (!code) {
    throw new Error("No shortcode injected")
  }
  return code
}

// Render the app
const rootElement = document.getElementById("root")!
if (!rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement)
  root.render(<PasswordForm />)
}
