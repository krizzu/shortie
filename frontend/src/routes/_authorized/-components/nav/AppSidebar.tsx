import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/components/ui/sidebar"
import { NavUser } from "./UserNav.tsx"
import { NavMain } from "./MainNav.tsx"
import type { LucideIcon } from "lucide-react"
import type { LinkProps } from "@tanstack/react-router"

type NavLink = {
  title: string
  url: LinkProps["to"]
  icon: LucideIcon
  isActive: boolean
}

export function AppSidebar({
  userName,
  links,
  onLogOut,
}: {
  userName: string
  links: NavLink[]
  onLogOut: () => void
}) {
  return (
    <Sidebar>
      <SidebarHeader />
      <SidebarContent>
        <NavMain items={links} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser onLogOut={onLogOut} userName={userName} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}
