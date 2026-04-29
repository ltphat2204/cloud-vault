import {
  Folder,
  File,
  FileText,
  Image as ImageIcon,
  Music,
  Video,
} from 'lucide-react'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import type { FolderDto, FileDto } from '@/types'
import { format } from 'date-fns'
import { ItemActions } from './ItemActions'
import { useNavigationStore } from '@/store/use-navigation-store'

interface FileTableProps {
  projectId: string
  folders: FolderDto[]
  files: FileDto[]
  onDownloadFile: (file: FileDto) => void
  onRenameFolder: (folder: FolderDto) => void
  onRenameFile: (file: FileDto) => void
  onMoveFolder: (folder: FolderDto) => void
  onMoveFile: (file: FileDto) => void
  onDeleteFolder: (folder: FolderDto) => void
  onDeleteFile: (file: FileDto) => void
  onShareFolder: (folder: FolderDto) => void
  onShareFile: (file: FileDto) => void
  onViewHistoryFolder: (folder: FolderDto) => void
  onViewHistoryFile: (file: FileDto) => void
}

export function FileTable({
  folders,
  files,
  onDownloadFile,
  onRenameFolder,
  onRenameFile,
  onMoveFolder,
  onMoveFile,
  onDeleteFolder,
  onDeleteFile,
  onShareFolder,
  onShareFile,
  onViewHistoryFolder,
  onViewHistoryFile,
}: FileTableProps) {
  const { path, navigateTo } = useNavigationStore()

  const formatSize = (bytes: number) => {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  const getFileIcon = (mime: string) => {
    if (mime.startsWith('image/')) return ImageIcon
    if (mime.startsWith('video/')) return Video
    if (mime.startsWith('audio/')) return Music
    if (
      mime.includes('pdf') ||
      mime.includes('text') ||
      mime.includes('document')
    )
      return FileText
    return File
  }

  const handleFolderClick = (folder: FolderDto) => {
    navigateTo(folder.id, [...path, { id: folder.id, name: folder.name }])
  }

  return (
    <div className="island-shell mt-6 overflow-hidden rounded-[2rem] border-0">
      <Table>
        <TableHeader>
          <TableRow className="border-b border-[var(--line)] bg-[var(--lagoon)]/5 hover:bg-[var(--lagoon)]/5">
            <TableHead className="w-[50%] px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">
              Name
            </TableHead>
            <TableHead className="px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">
              Size
            </TableHead>
            <TableHead className="px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">
              Modified
            </TableHead>
            <TableHead className="w-16 px-6 py-4"></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {folders.length === 0 && files.length === 0 && (
            <TableRow>
              <TableCell
                colSpan={4}
                className="h-32 text-center text-[var(--sea-ink-soft)] font-medium"
              >
                This folder is empty
              </TableCell>
            </TableRow>
          )}

          {/* Folders */}
          {folders.map((folder) => (
            <TableRow
              key={folder.id}
              className="border-b border-[var(--line)] last:border-0 hover:bg-[var(--lagoon)]/5 transition-colors group cursor-pointer"
              onClick={() => handleFolderClick(folder)}
            >
              <TableCell className="px-6 py-4">
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-amber-100 text-amber-600">
                    <Folder size={20} fill="currentColor" />
                  </div>
                  <span className="font-bold text-[var(--sea-ink)] group-hover:text-[var(--lagoon-deep)] transition-colors">
                    {folder.name}
                  </span>
                </div>
              </TableCell>
              <TableCell className="px-6 py-4 text-[var(--sea-ink-soft)] text-sm font-medium">
                —
              </TableCell>
              <TableCell className="px-6 py-4 text-[var(--sea-ink-soft)] text-sm font-medium">
                {format(new Date(folder.updatedAt), 'MMM dd, yyyy')}
              </TableCell>
              <TableCell className="px-6 py-4 text-right" onClick={(e) => e.stopPropagation()}>
                <ItemActions
                  type="folder"
                  onRename={() => onRenameFolder(folder)}
                  onMove={() => onMoveFolder(folder)}
                  onDelete={() => onDeleteFolder(folder)}
                  onShare={() => onShareFolder(folder)}
                  onViewHistory={() => onViewHistoryFolder(folder)}
                />
              </TableCell>
            </TableRow>
          ))}

          {/* Files */}
          {files.map((file) => {
            const Icon = getFileIcon(file.mimeType)
            return (
              <TableRow
                key={file.id}
                className="border-b border-[var(--line)] last:border-0 hover:bg-[var(--lagoon)]/5 transition-colors group"
              >
                <TableCell className="px-6 py-4">
                  <div className="flex items-center gap-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-100 text-blue-600">
                      <Icon size={20} />
                    </div>
                    <span className="font-bold text-[var(--sea-ink)]">
                      {file.name}
                    </span>
                  </div>
                </TableCell>
                <TableCell className="px-6 py-4 text-[var(--sea-ink-soft)] text-sm font-medium">
                  {formatSize(file.size)}
                </TableCell>
                <TableCell className="px-6 py-4 text-[var(--sea-ink-soft)] text-sm font-medium">
                  {format(new Date(file.updatedAt), 'MMM dd, yyyy')}
                </TableCell>
                <TableCell className="px-6 py-4 text-right">
                  <ItemActions
                    type="file"
                    onDownload={() => onDownloadFile(file)}
                    onRename={() => onRenameFile(file)}
                    onMove={() => onMoveFile(file)}
                    onDelete={() => onDeleteFile(file)}
                    onShare={() => onShareFile(file)}
                    onViewHistory={() => onViewHistoryFile(file)}
                  />
                </TableCell>
              </TableRow>
            )
          })}
        </TableBody>
      </Table>
    </div>
  )
}
