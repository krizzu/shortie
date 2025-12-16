import {
  createFileRoute,
  Link,
  Outlet,
  useRouter,
  useRouterState,
} from "@tanstack/react-router"
import { AppSidebar } from "@/routes/_authorized/-components/AppSidebar.tsx"
import { SidebarInset, SidebarProvider } from "@/components/ui/sidebar"
import { Link2 } from "lucide-react"
import { Separator } from "@/components/ui/separator"
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb.tsx"
import React from "react"

export const Route = createFileRoute("/_authorized/dashboard")({
  component: DashboardMain,
  beforeLoad: () => ({
    pageTitle: "Dashboard",
  }),
})

function DashboardMain() {
  const { auth } = Route.useRouteContext()
  const router = useRouter()
  const matches = useRouterState({ select: (s) => s.matches })

  const breadcrumbs = matches
    .filter(
      (match): match is typeof match & { context: { pageTitle: string } } =>
        "pageTitle" in match.context
    )
    .map(({ pathname, context }) => {
      return {
        title: context.pageTitle,
        path: pathname,
      }
    })

  function logOut() {
    auth.logout()
    router.invalidate()
  }

  return (
    <SidebarProvider>
      <AppSidebar
        userName="Admin"
        links={[
          {
            title: "links",
            icon: Link2,
            url: "/dashboard/links",
            isActive:
              matches[matches.length - 1]?.pathname === "/dashboard/links",
          },
        ]}
        onLogOut={logOut}
      />
      <SidebarInset>
        <header className="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-data-[collapsible=icon]/sidebar-wrapper:h-12">
          <div className="flex items-center gap-2 px-4">
            <Separator
              orientation="vertical"
              className="mr-2 data-[orientation=vertical]:h-4"
            />
            <Breadcrumb>
              <BreadcrumbList>
                {breadcrumbs.map((item, index, list) => {
                  return (
                    <React.Fragment key={item.path}>
                      <BreadcrumbItem className="hidden md:block">
                        <BreadcrumbLink asChild>
                          <Link to={item.path}>{item.title}</Link>
                        </BreadcrumbLink>
                      </BreadcrumbItem>
                      {index < list.length - 1 ? (
                        <BreadcrumbSeparator className="hidden md:block" />
                      ) : null}
                    </React.Fragment>
                  )
                })}
              </BreadcrumbList>
            </Breadcrumb>
          </div>
        </header>
        <Outlet />
      </SidebarInset>
    </SidebarProvider>
  )
}
