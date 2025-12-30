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
  link: string
  onCreateNew: () => void
  onShowList: () => void
}

export function CreatedLink({ link, onCreateNew, onShowList }: Props) {
  const url = new URL(link, EnvVars.apiUrl)

  return (
    <Card className="max-w-sm">
      <CardHeader className="text-center">
        <div className="flex justify-center mb-8">
          <BadgeCheck className="text-primary size-32" />
        </div>
        <CardTitle>
          URL created: <span className="text-primary">{link}</span>
        </CardTitle>
        <CardDescription>
          You can now visit it at{" "}
          <a href={url.href} className="text-primary hover:underline">
            {url.href}
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
