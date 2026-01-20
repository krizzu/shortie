import { Button } from "@/components/ui/button.tsx"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card.tsx"

type ErrorProps = {
  title?: string
  error?: Error | string
  retryLabel?: string
  onRetry?: () => void
}

export function Error({
  error,
  retryLabel = "try again",
  onRetry,
  title = "Something went wrong",
}: ErrorProps) {
  let message = "An unexpected error occurred"

  if (error instanceof Error) {
    message = (error as Error).message
  } else if (typeof error === "string") {
    message = error
  }

  return (
    <Card className="p-2 max-w-md">
      <CardHeader>
        <CardTitle className="text-red-500 ">{title}</CardTitle>
      </CardHeader>
      <CardContent className="space-y-2">
        <CardDescription>
          <p className="text-sm text-red-400">{message}</p>
        </CardDescription>

        {onRetry && (
          <Button onClick={onRetry} variant="secondary">
            {retryLabel}
          </Button>
        )}
      </CardContent>
    </Card>
  )
}
