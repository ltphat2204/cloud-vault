import { createFileRoute } from '@tanstack/react-router'
import { useNotifications } from '@/hooks/queries/use-notifications'
import { Skeleton } from '@/components/ui/skeleton'
import { Bell, Check, Inbox, Clock, CheckCheck } from 'lucide-react'
import { format } from 'date-fns'
import { Button } from '@/components/ui/button'

export const Route = createFileRoute('/dashboard/notifications')({
  component: NotificationsPage,
})

function NotificationsPage() {
  const { notifications, isLoading, markAsRead, markAllAsRead, totalElements } = useNotifications()

  return (
    <div className="space-y-8 max-w-4xl mx-auto">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="display-title text-4xl font-bold text-[var(--sea-ink)]">
            Notifications
          </h1>
          <p className="text-[var(--sea-ink-soft)] mt-1 font-medium">
            Stay updated with your workspace activities
          </p>
        </div>
        {notifications.some(n => !n.read) && (
          <Button
            onClick={() => markAllAsRead()}
            className="rounded-xl font-bold gap-2 shadow-md bg-[var(--lagoon-deep)]"
          >
            <CheckCheck size={18} />
            Mark all as read
          </Button>
        )}
      </div>

      <div className="island-shell overflow-hidden rounded-[2.5rem] border-0 bg-white/40 backdrop-blur-md shadow-xl">
        {isLoading ? (
          <div className="p-8 space-y-6">
            {[1, 2, 3, 4].map((i) => (
              <div key={i} className="flex gap-4">
                <Skeleton className="h-12 w-12 rounded-2xl flex-shrink-0" />
                <div className="space-y-2 flex-1">
                  <Skeleton className="h-4 w-3/4 rounded-lg" />
                  <Skeleton className="h-3 w-1/4 rounded-lg" />
                </div>
              </div>
            ))}
          </div>
        ) : notifications.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-32 px-6 text-center">
            <div className="mb-8 flex h-24 w-24 items-center justify-center rounded-[2rem] bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
              <Inbox size={48} className="opacity-40" />
            </div>
            <h2 className="text-2xl font-bold text-[var(--sea-ink)]">No notifications</h2>
            <p className="mt-2 text-[var(--sea-ink-soft)] max-w-sm">
              When someone shares a file with you or mentions you, it will appear here.
            </p>
          </div>
        ) : (
          <div className="divide-y divide-[var(--line)]">
            {notifications.map((notification) => (
              <div
                key={notification.id}
                className={`group flex items-start gap-5 p-8 transition-all hover:bg-white/60 ${
                  !notification.read ? 'bg-[var(--lagoon)]/5' : ''
                }`}
              >
                <div className={`flex h-14 w-14 items-center justify-center rounded-2xl flex-shrink-0 transition-transform group-hover:scale-105 shadow-sm ${
                  !notification.read 
                    ? 'bg-[var(--lagoon-deep)] text-white' 
                    : 'bg-white text-[var(--sea-ink-soft)] border border-[var(--line)]'
                }`}>
                  <Bell size={24} />
                </div>
                
                <div className="flex-1 space-y-2 pt-1">
                  <div className="flex items-center justify-between">
                    <p className={`text-lg leading-snug ${
                      !notification.read ? 'font-extrabold text-[var(--sea-ink)]' : 'font-medium text-[var(--sea-ink-soft)]'
                    }`}>
                      {notification.message}
                    </p>
                    {!notification.read && (
                      <Button
                        size="sm"
                        variant="ghost"
                        onClick={() => markAsRead(notification.id)}
                        className="h-8 w-8 rounded-xl p-0 hover:bg-[var(--lagoon-deep)] hover:text-white transition-all shadow-sm bg-white"
                        title="Mark as read"
                      >
                        <Check size={16} />
                      </Button>
                    )}
                  </div>
                  
                  <div className="flex items-center gap-4 text-xs font-bold text-[var(--sea-ink-soft)] uppercase tracking-wider">
                    <div className="flex items-center gap-1.5">
                      <Clock size={14} className="opacity-60" />
                      {format(new Date(notification.createdAt), 'MMM dd, yyyy · hh:mm a')}
                    </div>
                    {notification.read && (
                      <span className="flex items-center gap-1 text-[var(--lagoon-deep)]/60">
                        <Check size={12} />
                        Read
                      </span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
      
      {totalElements > notifications.length && (
        <div className="flex justify-center py-4">
          <p className="text-sm text-[var(--sea-ink-soft)] font-medium">
            Showing {notifications.length} of {totalElements} notifications
          </p>
        </div>
      )}
    </div>
  )
}
