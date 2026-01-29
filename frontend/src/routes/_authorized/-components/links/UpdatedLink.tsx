import { Button } from "@/components/ui/button.tsx"
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { BadgeCheck } from "lucide-react"

type Props = {
  shortCode: string
  onShowList: () => void
}

export function UpdatedLink({ shortCode, onShowList }: Props) {
  return (
    <Card className="max-w-sm">
      <CardHeader className="text-center">
        <div className="flex justify-center mb-8">
          <BadgeCheck className="text-primary size-32" />
        </div>
        <CardTitle>
          URL updated: <span className="text-primary">{shortCode}</span>
        </CardTitle>
      </CardHeader>
      <CardContent></CardContent>
      <CardFooter className="flex-col gap-2">
        <Button variant="link" onClick={onShowList}>
          Show all
        </Button>
      </CardFooter>
    </Card>
  )
}
