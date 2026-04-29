import { useMemo } from 'react'
import { Bell, Check, Inbox, ChevronRight } from 'lucide-react'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { useNotifications } from '@/hooks/queries/use-notifications'
import { formatDistanceToNow } from 'date-fns'
import { Link } from '@tanstack/react-router'

export function NotificationBell() {
  const params = useMemo(() => ({ size: 5 }), [])
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications(params)

  const handleMarkAllRead = (e: React.MouseEvent) => {
    e.preventDefault()
    e.stopPropagation()
    markAllAsRead()
  }

  const handleMarkRead = (e: React.MouseEvent, id: string) => {
    e.preventDefault()
    e.stopPropagation()
    markAsRead(id)
  }

  return (
    <Popover>
      <PopoverTrigger asChild>
        <button className="group relative flex h-10 w-10 items-center justify-center rounded-xl transition-all hover:bg-[var(--lagoon)]/10 text-[var(--sea-ink-soft)] hover:text-[var(--lagoon-deep)] focus:outline-none">
          <Bell size={20} />
          {unreadCount > 0 && (
            <span className="absolute top-1.5 right-1.5 flex h-4 w-4 items-center justify-center rounded-full bg-[var(--lagoon-deep)] text-[8px] font-bold text-white border-2 border-[var(--sand)]">
              {unreadCount > 9 ? '9+' : unreadCount}
            </span>
          )}
        </button>
      </PopoverTrigger>
      <PopoverContent
        align="end"
        sideOffset={12}
        className="w-[300px] p-0 border-0 bg-transparent shadow-none"
      >
        <div className="island-shell overflow-hidden rounded-[2rem] shadow-2xl backdrop-blur-lg">
          <div className="bg-[var(--lagoon-deep)] p-4 text-white">
            <div className="flex items-center justify-between mb-0.5">
              <h3 className="font-bold text-base flex items-center gap-2">
                <Inbox size={16} className="opacity-80" />
                Notifications
              </h3>
              {unreadCount > 0 && (
                <button
                  type="button"
                  onClick={handleMarkAllRead}
                  className="text-[9px] uppercase tracking-wider font-bold text-white/70 hover:text-white transition-colors focus:outline-none"
                >
                  Mark all read
                </button>
              )}
            </div>
            <p className="text-white/60 text-[10px] font-medium">
              {unreadCount} unread alerts
            </p>
          </div>

          <div className="max-h-[320px] overflow-y-auto bg-[var(--surface-strong)]/90">
            {notifications.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-10 px-6 text-center">
                <div className="h-12 w-12 rounded-xl bg-[var(--lagoon)]/10 flex items-center justify-center text-[var(--lagoon-deep)] mb-3">
                  <Bell size={24} className="opacity-30" />
                </div>
                <p className="text-sm font-bold text-[var(--sea-ink)]">All caught up!</p>
              </div>
            ) : (
              <div className="divide-y divide-[var(--line)]/10">
                {notifications.map((notification) => (
                  <div
                    key={notification.id}
                    className={`group relative flex items-start gap-3 p-3 transition-all hover:bg-[var(--lagoon)]/5 ${
                      !notification.read ? 'bg-white/50' : 'bg-transparent'
                    }`}
                  >
                    <div className="relative mt-0.5">
                      <div className={`h-8 w-8 rounded-lg flex items-center justify-center ${
                        !notification.read 
                          ? 'bg-[var(--lagoon)]/15 text-[var(--lagoon-deep)]' 
                          : 'bg-[var(--line)]/5 text-[var(--sea-ink-soft)]'
                      }`}>
                        <Bell size={14} />
                      </div>
                      {!notification.read && (
                        <span className="absolute -top-0.5 -right-0.5 h-2 w-2 rounded-full bg-[var(--lagoon-deep)] border border-white" />
                      )}
                    </div>

                    <div className="flex-1 space-y-1 min-w-0">
                      <p className={`text-[11px] leading-relaxed break-words ${
                        !notification.read ? 'font-bold text-[var(--sea-ink)]' : 'text-[var(--sea-ink-soft)] font-medium'
                      }`}>
                        {notification.message}
                      </p>
                      <span className="text-[9px] text-[var(--sea-ink-soft)]/60 font-bold uppercase tracking-tight">
                        {formatDistanceToNow(new Date(notification.createdAt), {
                          addSuffix: true,
                        })}
                      </span>
                    </div>

                    {!notification.read && (
                      <button
                        type="button"
                        onClick={(e) => handleMarkRead(e, notification.id)}
                        className="opacity-0 group-hover:opacity-100 transition-all h-6 w-6 rounded-lg bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)] flex items-center justify-center hover:bg-[var(--lagoon-deep)] hover:text-white focus:outline-none"
                        title="Mark as read"
                      >
                        <Check size={12} />
                      </button>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>

          <Link
            to="/dashboard/notifications"
            className="flex w-full items-center justify-center gap-2 p-3 text-[10px] font-bold text-[var(--lagoon-deep)] uppercase tracking-wider hover:bg-[var(--lagoon)]/10 border-t border-[var(--line)] transition-all no-underline"
          >
            View History
            <ChevronRight size={12} />
          </Link>
        </div>
      </PopoverContent>
    </Popover>
  )
}
