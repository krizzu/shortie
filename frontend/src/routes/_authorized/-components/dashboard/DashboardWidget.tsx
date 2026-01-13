import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { cn } from "@/lib/utils"
import { Link as LinkIcon } from "lucide-react"
import { Link, type LinkProps } from "@tanstack/react-router"

const iconMap = {
  links: LinkIcon,
}

interface DashboardWidgetProps {
  title: string
  description?: string
  linkTo: LinkProps["to"]
  icon?: keyof typeof iconMap
  className?: string
}

export function DashboardWidget({
  title,
  description,
  linkTo,
  icon,
  className,
}: DashboardWidgetProps) {
  const Icon = icon ? iconMap[icon] : null

  return (
    <Link to={linkTo} className="group">
      <Card
        className={cn(
          "h-full transition-all hover:-translate-y-1 hover:shadow-lg hover:text-primary focus-visible:ring-2 focus-visible:ring-ring",
          className
        )}
      >
        <CardHeader className="flex flex-row items-center space-y-0">
          {Icon && (
            <Icon className="transition-colors group-hover:text-primary" />
          )}
          <CardTitle className="text-base font-medium">{title}</CardTitle>
        </CardHeader>

        {description && (
          <CardContent className="space-y-1">
            <p className="text-xs text-muted-foreground">{description}</p>
          </CardContent>
        )}
      </Card>
    </Link>
  )
}
