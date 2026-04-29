import { formatDistanceToNow } from 'date-fns'
import { 
  FileUp, 
  FileEdit, 
  FileMinus, 
  FolderPlus, 
  FolderEdit, 
  FolderMinus, 
  Download, 
  Share2,
  Move,
  PlusCircle,
  Pencil,
  Trash2
} from 'lucide-react'
import type { ActivityDto, AuditAction } from '#/types'
import { cn } from '#/lib/utils'

interface ActivityItemProps {
  activity: ActivityDto
}

const actionConfig: Record<AuditAction, { icon: any; color: string; label: string }> = {
  PROJECT_CREATED: { icon: PlusCircle, color: 'text-blue-500 bg-blue-50', label: 'Created project' },
  PROJECT_UPDATED: { icon: Pencil, color: 'text-blue-500 bg-blue-50', label: 'Updated project' },
  PROJECT_DELETED: { icon: Trash2, color: 'text-red-500 bg-red-50', label: 'Deleted project' },
  
  FOLDER_CREATED: { icon: FolderPlus, color: 'text-amber-500 bg-amber-50', label: 'Created folder' },
  FOLDER_RENAMED: { icon: FolderEdit, color: 'text-amber-500 bg-amber-50', label: 'Renamed folder' },
  FOLDER_MOVED: { icon: Move, color: 'text-amber-500 bg-amber-50', label: 'Moved folder' },
  FOLDER_DELETED: { icon: FolderMinus, color: 'text-red-500 bg-red-50', label: 'Deleted folder' },
  
  FILE_UPLOADED: { icon: FileUp, color: 'text-emerald-500 bg-emerald-50', label: 'Uploaded file' },
  FILE_RENAMED: { icon: FileEdit, color: 'text-emerald-500 bg-emerald-50', label: 'Renamed file' },
  FILE_MOVED: { icon: Move, color: 'text-emerald-500 bg-emerald-50', label: 'Moved file' },
  FILE_DELETED: { icon: FileMinus, color: 'text-red-500 bg-red-50', label: 'Deleted file' },
  FILE_DOWNLOADED: { icon: Download, color: 'text-indigo-500 bg-indigo-50', label: 'Downloaded file' },
  
  SHARE_CREATED: { icon: Share2, color: 'text-purple-500 bg-purple-50', label: 'Shared resource' },
  SHARE_UPDATED: { icon: Share2, color: 'text-purple-500 bg-purple-50', label: 'Updated share' },
  SHARE_DELETED: { icon: Share2, color: 'text-red-500 bg-red-50', label: 'Revoked share' },
}

export function ActivityItem({ activity }: ActivityItemProps) {
  const config = actionConfig[activity.action]
  const Icon = config.icon

  return (
    <div className="group flex items-start gap-4 rounded-2xl p-4 transition-all hover:bg-white/50">
      <div className={cn("flex h-10 w-10 shrink-0 items-center justify-center rounded-xl shadow-sm", config.color)}>
        <Icon size={20} />
      </div>
      <div className="flex-1 space-y-1">
        <div className="flex items-center justify-between">
          <p className="text-sm font-semibold text-[var(--sea-ink)]">
            {config.label}{' '}
            <span className="font-bold text-[var(--lagoon-deep)]">
              {activity.details.fileName || activity.details.folderName || activity.details.projectName || 'resource'}
            </span>
          </p>
          <span className="text-[11px] font-medium text-[var(--sea-ink-soft)]">
            {formatDistanceToNow(new Date(activity.createdAt), { addSuffix: true })}
          </span>
        </div>
        <div className="flex items-center gap-2">
          {activity.details.oldName && activity.details.newName && (
            <p className="text-xs text-[var(--sea-ink-soft)]">
              Changed from <span className="italic">{activity.details.oldName}</span> to <span className="italic">{activity.details.newName}</span>
            </p>
          )}
          {activity.details.fromFolder && activity.details.toFolder && (
            <p className="text-xs text-[var(--sea-ink-soft)]">
              Moved from <span className="italic">{activity.details.fromFolder}</span> to <span className="italic">{activity.details.toFolder}</span>
            </p>
          )}
          {activity.details.sharedWith && (
            <p className="text-xs text-[var(--sea-ink-soft)]">
              Shared with <span className="font-medium">{activity.details.sharedWith}</span>
            </p>
          )}
        </div>
      </div>
    </div>
  )
}
