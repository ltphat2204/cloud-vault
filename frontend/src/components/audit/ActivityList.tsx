import { ActivityItem } from './ActivityItem'
import { useMyActivities } from '@/hooks/queries/use-audit'
import { Skeleton } from '@/components/ui/skeleton'
import { History, Inbox } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useState } from 'react'

export function ActivityList() {
  const [page, setPage] = useState(0)
  const { activities, isLoading, totalPages, totalElements } = useMyActivities({ page, size: 10 })

  if (isLoading) {
    return (
      <div className="space-y-4">
        {Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="flex items-start gap-4 p-4">
            <Skeleton className="h-10 w-10 rounded-xl" />
            <div className="flex-1 space-y-2">
              <Skeleton className="h-4 w-[60%]" />
              <Skeleton className="h-3 w-[40%]" />
            </div>
          </div>
        ))}
      </div>
    )
  }

  if (activities.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-center">
        <div className="mb-4 flex h-20 w-20 items-center justify-center rounded-[2rem] bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
          <Inbox size={40} />
        </div>
        <h3 className="text-xl font-bold text-[var(--sea-ink)]">No activities yet</h3>
        <p className="mt-2 text-[var(--sea-ink-soft)] max-w-xs">
          Actions you take on your files and folders will appear here.
        </p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2 text-[var(--sea-ink-soft)]">
          <History size={16} />
          <span className="text-xs font-bold uppercase tracking-wider">
            {totalElements} Activities Recorded
          </span>
        </div>
      </div>

      <div className="divide-y divide-[var(--line)] rounded-[2rem] border border-[var(--line)] bg-white/20 backdrop-blur-md overflow-hidden">
        {activities.map((activity) => (
          <ActivityItem key={activity.id} activity={activity} />
        ))}
      </div>

      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-4 py-4">
          <Button
            variant="outline"
            size="sm"
            disabled={page === 0}
            onClick={() => setPage(p => p - 1)}
            className="rounded-xl border-[var(--line)] bg-white/50"
          >
            Previous
          </Button>
          <span className="text-sm font-semibold text-[var(--sea-ink)]">
            Page {page + 1} of {totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            disabled={page >= totalPages - 1}
            onClick={() => setPage(p => p + 1)}
            className="rounded-xl border-[var(--line)] bg-white/50"
          >
            Next
          </Button>
        </div>
      )}
    </div>
  )
}
