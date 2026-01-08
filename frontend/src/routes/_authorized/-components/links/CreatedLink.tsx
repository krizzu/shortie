import { Button } from "@/components/ui/button.tsx"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { BadgeCheck } from "lucide-react"
import { EnvVars } from "@/services/env-vars.ts"

type Props = {
  shortCode: string
  onCreateNew: () => void
  onShowList: () => void
}

export function CreatedLink({ shortCode, onCreateNew, onShowList }: Props) {
  const url = EnvVars.redirectUrl(shortCode)

  return (
    <Card className="max-w-sm">
      <CardHeader className="text-center">
        <div className="flex justify-center mb-8">
          <BadgeCheck className="text-primary size-32" />
        </div>
        <CardTitle>
          URL created: <span className="text-primary">{shortCode}</span>
        </CardTitle>
        <CardDescription>
          You can now visit it at{" "}
          <a href={url} className="text-primary hover:underline">
            {url}
          </a>
        </CardDescription>
      </CardHeader>
      <CardContent></CardContent>
      <CardFooter className="flex-col gap-2">
        <Button variant="ghost" onClick={onCreateNew}>
          Create a new link
        </Button>
        <Button variant="link" onClick={onShowList}>
          Show all
        </Button>
      </CardFooter>
    </Card>
  )
}
