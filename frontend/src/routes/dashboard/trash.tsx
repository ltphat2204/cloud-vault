import { createFileRoute } from '@tanstack/react-router'
import { useTrash } from '@/hooks/queries/use-trash'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { Trash2, RotateCcw, File, Folder, Trash, Layers } from 'lucide-react'
import { format } from 'date-fns'
import { Skeleton } from '@/components/ui/skeleton'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'

export const Route = createFileRoute('/dashboard/trash')({
  component: TrashPage,
})

function TrashPage() {
  const {
    trashItems,
    isLoading,
    restore,
    deletePermanently,
    emptyTrash,
    isEmptying,
  } = useTrash()

  return (
    <>
      <div className="flex flex-col gap-8 md:flex-row md:items-center md:justify-between mb-10">
        <div>
          <h1 className="display-title text-4xl font-bold text-[var(--sea-ink)]">
            Trash
          </h1>
          <p className="text-[var(--sea-ink-soft)] mt-1 font-medium">
            Manage deleted projects, files and folders
          </p>
        </div>
        <div className="flex items-center gap-3">
          <AlertDialog>
            <AlertDialogTrigger asChild>
              <Button
                variant="outline"
                disabled={trashItems.length === 0 || isEmptying}
                className="h-11 rounded-xl px-6 font-bold border-red-200 text-red-600 hover:bg-red-50 hover:text-red-700 gap-2"
              >
                <Trash size={18} />
                Empty Trash
              </Button>
            </AlertDialogTrigger>
            <AlertDialogContent className="rounded-[2rem]">
              <AlertDialogHeader>
                <AlertDialogTitle className="text-2xl font-bold text-[var(--sea-ink)]">
                  Are you absolutely sure?
                </AlertDialogTitle>
                <AlertDialogDescription className="text-lg">
                  This action cannot be undone. This will permanently delete all
                  items in your trash.
                </AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel className="rounded-xl">
                  Cancel
                </AlertDialogCancel>
                <AlertDialogAction
                  onClick={() => emptyTrash()}
                  className="rounded-xl bg-red-600 hover:bg-red-700"
                >
                  Empty Trash
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
      </div>

      <div className="island-shell overflow-hidden rounded-[2rem] border-0">
        {isLoading ? (
          <div className="p-8 space-y-4">
            {[1, 2, 3].map((i) => (
              <Skeleton key={i} className="h-12 w-full rounded-xl" />
            ))}
          </div>
        ) : trashItems.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-20 text-center">
            <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-3xl bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
              <Trash2 size={40} />
            </div>
            <h2 className="text-2xl font-bold text-[var(--sea-ink)]">
              Trash is empty
            </h2>
            <p className="mt-2 text-[var(--sea-ink-soft)] max-w-sm font-medium">
              Deleted items will appear here for 30 days before being
              permanently removed.
            </p>
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow className="border-b border-[var(--line)] bg-[var(--lagoon)]/5 hover:bg-[var(--lagoon)]/5">
                <TableHead className="w-[40%] px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">
                  Name
                </TableHead>
                <TableHead className="px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">
                  Deleted At
                </TableHead>
                <TableHead className="px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">
                  Original Path
                </TableHead>
                <TableHead className="w-32 px-6 py-4"></TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {trashItems.map((item) => (
                <TableRow
                  key={item.id}
                  className="border-b border-[var(--line)] last:border-0 hover:bg-[var(--lagoon)]/5 transition-colors group"
                >
                  <TableCell className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div
                        className={`flex h-10 w-10 items-center justify-center rounded-xl ${
                          item.type === 'PROJECT'
                            ? 'bg-indigo-100 text-indigo-600'
                            : item.type === 'FOLDER'
                              ? 'bg-amber-100 text-amber-600'
                              : 'bg-blue-100 text-blue-600'
                        }`}
                      >
                        {item.type === 'PROJECT' ? (
                          <Layers size={18} fill="currentColor" />
                        ) : item.type === 'FOLDER' ? (
                          <Folder size={18} fill="currentColor" />
                        ) : (
                          <File size={18} />
                        )}
                      </div>
                      <span className="font-bold text-[var(--sea-ink)]">
                        {item.name}
                      </span>
                    </div>
                  </TableCell>
                  <TableCell className="px-6 py-4 text-[var(--sea-ink-soft)] text-sm font-medium">
                    {format(new Date(item.deletedAt), 'MMM dd, yyyy HH:mm')}
                  </TableCell>
                  <TableCell className="px-6 py-4 text-[var(--sea-ink-soft)] text-sm font-medium italic">
                    {item.originalPath}
                  </TableCell>
                  <TableCell className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <AlertDialog>
                        <AlertDialogTrigger asChild>
                          <Button
                            variant="ghost"
                            size="icon"
                            title="Restore"
                            className="h-9 w-9 rounded-full hover:bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]"
                          >
                            <RotateCcw size={18} />
                          </Button>
                        </AlertDialogTrigger>
                        <AlertDialogContent className="rounded-[2rem]">
                          <AlertDialogHeader>
                            <AlertDialogTitle className="text-2xl font-bold text-[var(--sea-ink)]">
                              Restore Item?
                            </AlertDialogTitle>
                            <AlertDialogDescription className="text-lg">
                              This will move '{item.name}' back to its original
                              location.
                            </AlertDialogDescription>
                          </AlertDialogHeader>
                          <AlertDialogFooter>
                            <AlertDialogCancel className="rounded-xl">
                              Cancel
                            </AlertDialogCancel>
                            <AlertDialogAction
                              onClick={() => restore([item.id])}
                              className="rounded-xl bg-[var(--lagoon-deep)] hover:bg-[var(--lagoon-dark)]"
                            >
                              Restore
                            </AlertDialogAction>
                          </AlertDialogFooter>
                        </AlertDialogContent>
                      </AlertDialog>

                      <AlertDialog>
                        <AlertDialogTrigger asChild>
                          <Button
                            variant="ghost"
                            size="icon"
                            title="Delete Permanently"
                            className="h-9 w-9 rounded-full hover:bg-red-50 text-red-600 hover:text-red-700"
                          >
                            <Trash size={18} />
                          </Button>
                        </AlertDialogTrigger>
                        <AlertDialogContent className="rounded-[2rem]">
                          <AlertDialogHeader>
                            <AlertDialogTitle className="text-2xl font-bold text-[var(--sea-ink)]">
                              Delete Permanently?
                            </AlertDialogTitle>
                            <AlertDialogDescription className="text-lg">
                              This action cannot be undone. '{item.name}' will
                              be gone forever.
                            </AlertDialogDescription>
                          </AlertDialogHeader>
                          <AlertDialogFooter>
                            <AlertDialogCancel className="rounded-xl">
                              Cancel
                            </AlertDialogCancel>
                            <AlertDialogAction
                              onClick={() => deletePermanently([item.id])}
                              className="rounded-xl bg-red-600 hover:bg-red-700"
                            >
                              Delete
                            </AlertDialogAction>
                          </AlertDialogFooter>
                        </AlertDialogContent>
                      </AlertDialog>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </div>
    </>
  )
}
