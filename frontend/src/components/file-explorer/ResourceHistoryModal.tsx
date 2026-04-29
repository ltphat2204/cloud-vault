import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '#/components/ui/dialog'
import { ActivityItem } from '../audit/ActivityItem'
import { useResourceHistory } from '#/hooks/queries/use-audit'
import type { ResourceType } from '#/types'
import { History, Inbox, Loader2 } from 'lucide-react'
import { ScrollArea } from '#/components/ui/scroll-area'

interface ResourceHistoryModalProps {
  isOpen: boolean
  onClose: () => void
  resourceId: string
  resourceName: string
  resourceType: ResourceType
}

export function ResourceHistoryModal({
  isOpen,
  onClose,
  resourceId,
  resourceName,
  resourceType
}: ResourceHistoryModalProps) {
  const { history, isLoading } = useResourceHistory(resourceId, resourceType, { size: 50 })

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl rounded-[2.5rem] border-[var(--line)] bg-[var(--bg-base)]/95 p-0 backdrop-blur-xl">
        <DialogHeader className="p-8 pb-0">
          <div className="flex items-center gap-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-[var(--lagoon)]/20 text-[var(--lagoon-deep)]">
              <History size={24} />
            </div>
            <div>
              <DialogTitle className="text-xl font-bold text-[var(--sea-ink)]">
                History: {resourceName}
              </DialogTitle>
              <p className="text-sm text-[var(--sea-ink-soft)] font-medium">
                Showing all recent activity for this {resourceType.toLowerCase()}
              </p>
            </div>
          </div>
        </DialogHeader>

        <div className="p-4">
          <ScrollArea className="h-[400px] rounded-2xl border border-[var(--line)] bg-white/20 p-4">
            {isLoading ? (
              <div className="flex h-full flex-col items-center justify-center py-10">
                <Loader2 className="h-8 w-8 animate-spin text-[var(--lagoon-deep)]" />
                <p className="mt-4 text-sm font-medium text-[var(--sea-ink-soft)]">
                  Loading history...
                </p>
              </div>
            ) : history.length > 0 ? (
              <div className="space-y-2">
                {history.map((activity) => (
                  <ActivityItem key={activity.id} activity={activity} />
                ))}
              </div>
            ) : (
              <div className="flex h-full flex-col items-center justify-center py-10 text-center">
                <Inbox className="h-10 w-10 text-[var(--sea-ink-soft)]" />
                <p className="mt-4 text-sm font-semibold text-[var(--sea-ink)]">
                  No history found
                </p>
                <p className="mt-1 text-xs text-[var(--sea-ink-soft)]">
                  This item hasn't had any recorded actions yet.
                </p>
              </div>
            )}
          </ScrollArea>
        </div>
      </DialogContent>
    </Dialog>
  )
}
