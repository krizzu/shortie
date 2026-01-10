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
import { Link, type LinkProps } from "@tanstack/react-router"
import ShortieLogo from "@/../assets/shortie-logo.png"

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
      <SidebarHeader className="self-start px-3">
        <Link to="/dashboard">
          <img className="h-10 object-contain ..." src={ShortieLogo}  alt="shortie-logo"/>
        </Link>
      </SidebarHeader>
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
