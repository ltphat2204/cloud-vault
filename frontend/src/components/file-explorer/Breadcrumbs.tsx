import { ChevronRight, Home } from 'lucide-react'
import { Link } from '@tanstack/react-router'
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb'
import { useNavigationStore } from '@/store/use-navigation-store'

interface BreadcrumbPath {
  id: string
  name: string
  parentFolderId?: string | null
}

interface BreadcrumbsProps {
  projectId: string
  projectName: string
  path: BreadcrumbPath[]
}

export function Breadcrumbs({
  projectName,
  path,
}: BreadcrumbsProps) {
  const { navigateTo, reset } = useNavigationStore()

  // Filter out the root folder (the one with no parent) from display
  const displayPath = path.filter(folder => folder.parentFolderId)

  const handleProjectClick = () => {
    reset()
  }

  const handleFolderClick = (index: number) => {
    // We need to find the original index in the full path
    const folder = displayPath[index]
    const fullIndex = path.findIndex(f => f.id === folder.id)
    const newPath = path.slice(0, fullIndex + 1)
    const folderId = newPath[newPath.length - 1].id
    navigateTo(folderId, newPath)
  }

  return (
    <Breadcrumb>
      <BreadcrumbList className="text-[var(--sea-ink-soft)] font-medium">
        <BreadcrumbItem>
          <BreadcrumbLink asChild>
            <Link
              to="/dashboard/projects"
              className="flex items-center gap-1 hover:text-[var(--lagoon-deep)]"
            >
              <Home size={14} />
              Projects
            </Link>
          </BreadcrumbLink>
        </BreadcrumbItem>
        <BreadcrumbSeparator>
          <ChevronRight size={14} />
        </BreadcrumbSeparator>
        <BreadcrumbItem>
          <button
            onClick={handleProjectClick}
            className="hover:text-[var(--lagoon-deep)] cursor-pointer bg-transparent border-0 p-0 font-medium"
          >
            {projectName}
          </button>
        </BreadcrumbItem>

        {displayPath.map((folder, index) => (
          <div key={folder.id} className="flex items-center">
            <BreadcrumbSeparator>
              <ChevronRight size={14} />
            </BreadcrumbSeparator>
            <BreadcrumbItem>
              {index === displayPath.length - 1 ? (
                <BreadcrumbPage className="font-bold text-[var(--sea-ink)]">
                  {folder.name}
                </BreadcrumbPage>
              ) : (
                <button
                  onClick={() => handleFolderClick(index)}
                  className="hover:text-[var(--lagoon-deep)] cursor-pointer bg-transparent border-0 p-0 font-medium"
                >
                  {folder.name}
                </button>
              )}
            </BreadcrumbItem>
          </div>
        ))}
      </BreadcrumbList>
    </Breadcrumb>
  )
}
