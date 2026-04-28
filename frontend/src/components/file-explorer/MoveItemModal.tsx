import { useState, useEffect } from 'react'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Folder } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { foldersApi } from '@/api/folders'
import { Skeleton } from '@/components/ui/skeleton'

interface MoveItemModalProps {
  isOpen: boolean
  onClose: () => void
  onConfirm: (targetFolderId?: string) => void
  projectId: string
  projectName: string
  itemName: string
}

export function MoveItemModal({
  isOpen,
  onClose,
  onConfirm,
  projectId,
  projectName,
  itemName,
}: MoveItemModalProps) {
  const [selectedFolderId, setSelectedFolderId] = useState<string | undefined>(
    undefined,
  )

  const { data: allFolders, isLoading } = useQuery({
    queryKey: ['folders', projectId, 'all'],
    queryFn: () => foldersApi.listAll(projectId).then((res) => res.data.data),
    enabled: isOpen,
  })

  const rootFolder = allFolders?.find((f) => !f.parentFolderId)
  const displayFolders = allFolders?.filter((f) => f.parentFolderId) || []

  // Initialize selectedFolderId to rootFolder.id when data is loaded
  useEffect(() => {
    if (rootFolder && selectedFolderId === undefined) {
      setSelectedFolderId(rootFolder.id)
    }
  }, [rootFolder, selectedFolderId])

  const handleConfirm = () => {
    // If no Root found (shouldn't happen), use undefined
    onConfirm(selectedFolderId || rootFolder?.id)
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="rounded-[2rem] sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold text-[var(--sea-ink)]">
            Move "{itemName}"
          </DialogTitle>
        </DialogHeader>

        <div className="mt-4 max-h-[300px] overflow-y-auto pr-2">
          <p className="mb-4 text-sm font-bold text-[var(--sea-ink-soft)] uppercase tracking-wider">
            Select Destination
          </p>

          <div className="space-y-2">
            <button
              onClick={() => setSelectedFolderId(rootFolder?.id)}
              className={`flex w-full items-center gap-3 rounded-xl px-4 py-3 text-left transition-all ${
                selectedFolderId === rootFolder?.id
                  ? 'bg-[var(--lagoon)]/20 text-[var(--lagoon-deep)] shadow-sm'
                  : 'hover:bg-[var(--lagoon)]/10 text-[var(--sea-ink)]'
              }`}
            >
              <Folder
                size={18}
                className={selectedFolderId === rootFolder?.id ? 'fill-current' : ''}
              />
              <span className="font-bold">{projectName}</span>
            </button>

            {isLoading ? (
              <div className="space-y-2">
                <Skeleton className="h-12 w-full rounded-xl" />
                <Skeleton className="h-12 w-full rounded-xl" />
              </div>
            ) : (
              displayFolders.map((folder) => (
                <button
                  key={folder.id}
                  onClick={() => setSelectedFolderId(folder.id)}
                  className={`flex w-full items-center gap-3 rounded-xl px-4 py-3 text-left transition-all ${
                    selectedFolderId === folder.id
                      ? 'bg-[var(--lagoon)]/20 text-[var(--lagoon-deep)] shadow-sm'
                      : 'hover:bg-[var(--lagoon)]/10 text-[var(--sea-ink)]'
                  }`}
                >
                  <Folder
                    size={18}
                    className={
                      selectedFolderId === folder.id ? 'fill-current' : ''
                    }
                  />
                  <span className="font-bold">{folder.name}</span>
                </button>
              ))
            )}
          </div>
        </div>

        <DialogFooter className="mt-6">
          <Button variant="ghost" onClick={onClose} className="rounded-xl">
            Cancel
          </Button>
          <Button
            onClick={handleConfirm}
            className="rounded-xl px-6 font-bold shadow-lg"
          >
            Move Here
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
