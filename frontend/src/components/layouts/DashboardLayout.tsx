import type { ReactNode } from 'react'
import { Link } from '@tanstack/react-router'
import { useAuth } from '@/hooks/use-auth'
import { useWebSocket } from '@/hooks/use-websocket'
import {
  Cloud,
  Trash2,
  Settings,
  LogOut,
  User,
  ChevronRight,
  Users,
  History,
} from 'lucide-react'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Toaster } from '@/components/ui/sonner'
import { TooltipProvider } from '@/components/ui/tooltip'
import { NotificationBell } from '@/components/notifications/NotificationBell'

interface DashboardLayoutProps {
  children: ReactNode
}

export function DashboardLayout({ children }: DashboardLayoutProps) {
  const { user, logout } = useAuth()
  
  // Initialize global WebSocket connection for real-time notifications
  useWebSocket()

  const navItems = [
    { icon: Cloud, label: 'Projects', href: '/dashboard/projects' },
    { icon: Users, label: 'Shared with me', href: '/dashboard/shared-with-me' },
    { icon: History, label: 'Activities', href: '/dashboard/activities' },
    { icon: Trash2, label: 'Trash', href: '/dashboard/trash' },
  ]

  const bottomItems = [
    { icon: Settings, label: 'Settings', href: '/dashboard/settings' },
  ]

  return (
    <div className="flex h-screen overflow-hidden bg-[var(--bg-base)]">
      {/* Sidebar */}
      <aside className="island-shell m-4 mr-0 flex w-72 flex-col rounded-[2rem] border-r-0">
        <div className="p-8 flex items-center justify-between">
          <Link
            to="/dashboard"
            className="flex items-center gap-3 no-underline"
          >
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-[var(--lagoon-deep)] text-white shadow-lg">
              <Cloud size={24} />
            </div>
            <span className="display-title text-xl font-bold text-[var(--sea-ink)]">
              CloudVault
            </span>
          </Link>
          <NotificationBell />
        </div>


        <nav className="flex-1 space-y-2 px-4">
          <p className="px-4 text-[10px] font-bold tracking-[0.2em] text-[var(--sea-ink-soft)] uppercase">
            Main Menu
          </p>
          {navItems.map((item) => (
            <Link
              key={item.label}
              to={item.href}
              className="flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-semibold text-[var(--sea-ink-soft)] no-underline transition-all hover:bg-[var(--lagoon)]/10 hover:text-[var(--sea-ink)]"
              activeProps={{
                className:
                  'bg-[var(--lagoon)]/15 text-[var(--sea-ink)] shadow-sm',
              }}
            >
              <item.icon size={20} />
              {item.label}
            </Link>
          ))}

          <div className="my-6 border-t border-[var(--line)]" />

          <p className="px-4 text-[10px] font-bold tracking-[0.2em] text-[var(--sea-ink-soft)] uppercase">
            Preferences
          </p>
          {bottomItems.map((item) => (
            <Link
              key={item.label}
              to={item.href}
              className="flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-semibold text-[var(--sea-ink-soft)] no-underline transition-all hover:bg-[var(--lagoon)]/10 hover:text-[var(--sea-ink)]"
            >
              <item.icon size={20} />
              {item.label}
            </Link>
          ))}
        </nav>

        {/* User Card */}
        <div className="p-4">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <button className="flex w-full items-center gap-3 rounded-2xl bg-white/40 p-3 text-left transition-all hover:bg-white/60 hover:shadow-md focus:outline-none">
                <Avatar className="h-10 w-10 border-2 border-white shadow-sm">
                  <AvatarImage
                    src={`https://api.dicebear.com/7.x/avataaars/svg?seed=${user?.email}`}
                  />
                  <AvatarFallback className="bg-[var(--lagoon)]/20 text-[var(--lagoon-deep)]">
                    {user?.name.charAt(0) || <User size={20} />}
                  </AvatarFallback>
                </Avatar>
                <div className="flex-1 overflow-hidden">
                  <p className="truncate text-sm font-bold text-[var(--sea-ink)]">
                    {user?.name || 'User'}
                  </p>
                  <p className="truncate text-[11px] text-[var(--sea-ink-soft)]">
                    {user?.email || 'user@example.com'}
                  </p>
                </div>
                <ChevronRight
                  size={16}
                  className="text-[var(--sea-ink-soft)]"
                />
              </button>
            </DropdownMenuTrigger>
            <DropdownMenuContent
              align="end"
              className="w-56 rounded-2xl border-[var(--line)] p-2 shadow-xl"
              side="right"
              sideOffset={12}
            >
              <DropdownMenuLabel className="px-3 py-2 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">
                My Account
              </DropdownMenuLabel>
              <DropdownMenuSeparator className="bg-[var(--line)]" />
              <DropdownMenuItem className="cursor-pointer rounded-xl px-3 py-2 text-sm font-medium transition-colors hover:bg-[var(--lagoon)]/10">
                <User className="mr-2 h-4 w-4" />
                Profile
              </DropdownMenuItem>
              <DropdownMenuItem className="cursor-pointer rounded-xl px-3 py-2 text-sm font-medium transition-colors hover:bg-[var(--lagoon)]/10">
                <Settings className="mr-2 h-4 w-4" />
                Settings
              </DropdownMenuItem>
              <DropdownMenuSeparator className="bg-[var(--line)]" />
              <DropdownMenuItem
                onClick={logout}
                className="cursor-pointer rounded-xl px-3 py-2 text-sm font-medium text-red-600 transition-colors hover:bg-red-50 focus:text-red-600"
              >
                <LogOut className="mr-2 h-4 w-4" />
                Log out
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 overflow-y-auto p-8">
        <div className="h-full rounded-[2.5rem] bg-white/30 p-8 shadow-inner backdrop-blur-sm">
          <TooltipProvider>{children}</TooltipProvider>
        </div>
      </main>
      <Toaster richColors position="top-right" closeButton />
    </div>
  )
}
