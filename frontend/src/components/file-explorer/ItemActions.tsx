import { MoreVertical, Download, Edit2, Move, Trash2, Share2, History } from 'lucide-react'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Button } from '@/components/ui/button'

interface ItemActionsProps {
  type: 'file' | 'folder'
  onDownload?: () => void
  onRename: () => void
  onMove: () => void
  onDelete: () => void
  onShare: () => void
  onViewHistory: () => void
}

export function ItemActions({
  type,
  onDownload,
  onRename,
  onMove,
  onDelete,
  onShare,
  onViewHistory,
}: ItemActionsProps) {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className="h-8 w-8 rounded-full hover:bg-[var(--lagoon)]/10"
        >
          <MoreVertical size={16} />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-44 rounded-xl">
        <DropdownMenuItem onClick={onShare} className="cursor-pointer gap-2">
          <Share2 size={14} />
          Share
        </DropdownMenuItem>
        {type === 'file' && (
          <DropdownMenuItem
            onClick={onDownload}
            className="cursor-pointer gap-2"
          >
            <Download size={14} />
            Download
          </DropdownMenuItem>
        )}
        <DropdownMenuItem onClick={onRename} className="cursor-pointer gap-2">
          <Edit2 size={14} />
          Rename
        </DropdownMenuItem>
        <DropdownMenuItem onClick={onMove} className="cursor-pointer gap-2">
          <Move size={14} />
          Move to...
        </DropdownMenuItem>
        <DropdownMenuItem onClick={onViewHistory} className="cursor-pointer gap-2">
          <History size={14} />
          View History
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem
          onClick={onDelete}
          className="cursor-pointer gap-2 text-red-600 focus:text-red-600 focus:bg-red-50"
        >
          <Trash2 size={14} />
          Delete
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
