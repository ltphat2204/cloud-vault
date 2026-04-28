import { useState, useEffect } from 'react'
import type { FileDto, ProjectDto } from '@/types'
import {
  useFolderContents,
  useFileSystemActions,
} from '@/hooks/queries/use-file-system'
import { ActionToolbar } from './ActionToolbar'
import { Breadcrumbs } from './Breadcrumbs'
import { FileTable } from './FileTable'
import { ProjectModal } from '../projects/ProjectModal'
import { MoveItemModal } from './MoveItemModal'
import { foldersApi } from '@/api/folders'
import { filesApi } from '@/api/files'
import { Skeleton } from '@/components/ui/skeleton'
import { useQueryClient, useQuery } from '@tanstack/react-query'
import { toast } from 'sonner'
import { useNavigationStore } from '@/store/use-navigation-store'
import { useNavigate } from '@tanstack/react-router'

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'

interface FileExplorerProps {
  projectId: string
  project: ProjectDto
}

export function FileExplorer({ projectId, project }: FileExplorerProps) {
  const { currentFolderId, path, setPath } = useNavigationStore()
  const navigate = useNavigate()

  const {
    folders,
    files,
    isLoading: isContentsLoading,
    createFolder,
    uploadFile,
    isCreatingFolder,
    isUploadingFile,
  } = useFolderContents(projectId, currentFolderId)

  // Fetch full breadcrumb path if we have a folderId but no path (e.g. on direct link)
  const { data: folderPath, isLoading: isFolderDetailsLoading } = useQuery({
    queryKey: ['folders', currentFolderId, 'path'],
    queryFn: () => foldersApi.getPath(currentFolderId!).then((res) => res.data.data),
    enabled: !!currentFolderId && path.length === 0,
  })

  useEffect(() => {
    if (folderPath && path.length === 0) {
      setPath(folderPath.map(f => ({ id: f.id, name: f.name, parentFolderId: f.parentFolderId })))
    }
  }, [folderPath, path.length, setPath])

  // Sync store to URL
  useEffect(() => {
    navigate({
      search: { folderId: currentFolderId } as any,
      replace: false,
    })
  }, [currentFolderId, navigate])

  const queryClient = useQueryClient()
  const { deleteFolder, deleteFile, renameFolder, renameFile } =
    useFileSystemActions()

  const [isNewFolderModalOpen, setIsNewFolderModalOpen] = useState(false)
  const [editingItem, setEditingItem] = useState<{
    type: 'file' | 'folder'
    id: string
    name: string
  } | null>(null)
  const [movingItem, setMovingItem] = useState<{
    type: 'file' | 'folder'
    id: string
    name: string
  } | null>(null)
  const [deletingItem, setDeletingItem] = useState<{
    type: 'file' | 'folder'
    id: string
    name: string
  } | null>(null)

  const handleDownload = (file: FileDto) => {
    filesApi.download(file.id, file.name)
  }

  const isLoading = isContentsLoading || isFolderDetailsLoading

  return (
    <div className="flex flex-col h-full">
      <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
        <div className="space-y-1">
          <Breadcrumbs
            projectId={projectId}
            projectName={project.name}
            path={path}
          />
          <h1 className="display-title text-3xl font-bold text-[var(--sea-ink)]">
            {path.length > 0 && path[path.length - 1].parentFolderId
              ? path[path.length - 1].name
              : project.name}
          </h1>
        </div>
        <ActionToolbar
          onNewFolder={() => setIsNewFolderModalOpen(true)}
          onUploadFile={uploadFile}
          isUploading={isUploadingFile}
        />
      </div>

      {isLoading ? (
        <div className="mt-8 space-y-4">
          <Skeleton className="h-12 w-full rounded-2xl" />
          <Skeleton className="h-12 w-full rounded-2xl" />
          <Skeleton className="h-12 w-full rounded-2xl" />
        </div>
      ) : (
        <FileTable
          projectId={projectId}
          folders={folders}
          files={files}
          onDownloadFile={handleDownload}
          onRenameFolder={(f) =>
            setEditingItem({ type: 'folder', id: f.id, name: f.name })
          }
          onRenameFile={(f) =>
            setEditingItem({ type: 'file', id: f.id, name: f.name })
          }
          onMoveFolder={(f) =>
            setMovingItem({ type: 'folder', id: f.id, name: f.name })
          }
          onMoveFile={(f) =>
            setMovingItem({ type: 'file', id: f.id, name: f.name })
          }
          onDeleteFolder={(f) =>
            setDeletingItem({ type: 'folder', id: f.id, name: f.name })
          }
          onDeleteFile={(f) =>
            setDeletingItem({ type: 'file', id: f.id, name: f.name })
          }
        />
      )}

      {/* Modals */}
      <ProjectModal
        isOpen={isNewFolderModalOpen}
        onClose={() => setIsNewFolderModalOpen(false)}
        onConfirm={(name) => {
          createFolder(name)
          setIsNewFolderModalOpen(false)
        }}
        title="New Folder"
        isLoading={isCreatingFolder}
      />

      <ProjectModal
        isOpen={!!editingItem}
        onClose={() => setEditingItem(null)}
        onConfirm={(name) => {
          if (editingItem?.type === 'folder') {
            renameFolder({ id: editingItem.id, name })
          } else if (editingItem?.type === 'file') {
            renameFile({ id: editingItem.id, name })
          }
          setEditingItem(null)
        }}
        title={`Rename ${editingItem?.type === 'folder' ? 'Folder' : 'File'}`}
        initialName={editingItem?.name}
      />

      <MoveItemModal
        isOpen={!!movingItem}
        onClose={() => setMovingItem(null)}
        projectId={projectId}
        projectName={project.name}
        itemName={movingItem?.name || ''}
        onConfirm={(targetId) => {
          if (movingItem?.type === 'folder') {
            foldersApi.move(movingItem.id, targetId).then(() => {
              toast.success('Folder moved successfully')
              queryClient.invalidateQueries({ queryKey: ['folders'] })
            })
          } else if (movingItem?.type === 'file') {
            filesApi.move(movingItem.id, targetId).then(() => {
              toast.success('File moved successfully')
              queryClient.invalidateQueries({ queryKey: ['files'] })
            })
          }
          setMovingItem(null)
        }}
      />

      <AlertDialog
        open={!!deletingItem}
        onOpenChange={(open) => !open && setDeletingItem(null)}
      >
        <AlertDialogContent className="rounded-[2rem]">
          <AlertDialogHeader>
            <AlertDialogTitle className="text-2xl font-bold text-[var(--sea-ink)]">
              Move to Trash?
            </AlertDialogTitle>
            <AlertDialogDescription className="text-lg">
              Are you sure you want to move '{deletingItem?.name}' to trash?
              You can restore it later from the Trash page.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel className="rounded-xl">Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={() => {
                if (deletingItem) {
                  if (deletingItem.type === 'folder') {
                    deleteFolder(deletingItem.id)
                  } else {
                    deleteFile(deletingItem.id)
                  }
                }
                setDeletingItem(null)
              }}
              className="rounded-xl bg-red-600 hover:bg-red-700"
            >
              Move to Trash
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
