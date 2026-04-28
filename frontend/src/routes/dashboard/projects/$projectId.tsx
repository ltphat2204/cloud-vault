import { createFileRoute } from '@tanstack/react-router'
import { FileExplorer } from '@/components/file-explorer/FileExplorer'
import { useQuery } from '@tanstack/react-query'
import { projectsApi } from '@/api/projects'
import { foldersApi } from '@/api/folders'
import { Skeleton } from '@/components/ui/skeleton'
import { z } from 'zod'
import { useEffect } from 'react'
import { useNavigationStore } from '@/store/use-navigation-store'
import type { FolderDto } from '@/types'

const projectSearchSchema = z.object({
  folderId: z.string().optional(),
})

export const Route = createFileRoute('/dashboard/projects/$projectId')({
  validateSearch: (search) => projectSearchSchema.parse(search),
  component: ProjectDetailPage,
})

function ProjectDetailPage() {
  const { projectId } = Route.useParams()
  const { folderId } = Route.useSearch()
  const { navigateTo, reset } = useNavigationStore()

  useEffect(() => {
    const initializeProject = async () => {
      if (folderId) {
        navigateTo(folderId)
      } else {
        // Find the Root folder for this project
        try {
          const res = await foldersApi.list(projectId)
          const folders = res.data.data
          // The root folder is the one with no parent
          const rootFolder = folders.find((f: FolderDto) => !f.parentFolderId)
          if (rootFolder) {
            navigateTo(rootFolder.id, [])
          } else {
            reset()
          }
        } catch (error) {
          console.error('Failed to fetch root folder', error)
          reset()
        }
      }
    }

    initializeProject()
  }, [projectId, folderId, navigateTo, reset])

  const { data: project, isLoading } = useQuery({
    queryKey: ['projects', projectId],
    queryFn: () => projectsApi.get(projectId).then((res) => res.data.data),
  })

  return (
    <>
      {isLoading ? (
        <div className="space-y-6">
          <div className="flex justify-between items-center">
            <Skeleton className="h-10 w-48 rounded-xl" />
            <Skeleton className="h-10 w-32 rounded-xl" />
          </div>
          <Skeleton className="h-64 w-full rounded-[2rem]" />
        </div>
      ) : project ? (
        <FileExplorer projectId={projectId} project={project} />
      ) : (
        <div className="flex items-center justify-center h-64 text-[var(--sea-ink-soft)] font-medium">
          Project not found
        </div>
      )}
    </>
  )
}
