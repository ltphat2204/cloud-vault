import type { ProjectDto } from '@/types'
import { Card } from '@/components/ui/card'
import { MoreVertical, Folder, Calendar, Trash2, Edit2, Share2 } from 'lucide-react'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Button } from '@/components/ui/button'
import { Link } from '@tanstack/react-router'
import { format } from 'date-fns'

interface ProjectCardProps {
  project: ProjectDto
  onRename: (id: string, name: string) => void
  onDelete: (id: string) => void
  onShare: (project: ProjectDto) => void
}

export function ProjectCard({ project, onRename, onDelete, onShare }: ProjectCardProps) {
  return (
    <Card className="island-shell group relative overflow-hidden rounded-[2rem] border-0 transition-all hover:scale-[1.02] hover:shadow-xl">
      <Link
        to="/dashboard/projects/$projectId"
        params={{ projectId: project.id }}
        className="block p-6 no-underline"
      >
        <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-[var(--lagoon)]/20 text-[var(--lagoon-deep)]">
          <Folder size={24} />
        </div>
        <h3 className="mb-2 truncate text-xl font-bold text-[var(--sea-ink)] group-hover:text-[var(--lagoon-deep)]">
          {project.name}
        </h3>
        <div className="flex items-center gap-2 text-xs font-medium text-[var(--sea-ink-soft)]">
          <Calendar size={14} />
          <span>
            Created {format(new Date(project.createdAt), 'MMM dd, yyyy')}
          </span>
        </div>
      </Link>

      <div className="absolute top-6 right-6">
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
          <DropdownMenuContent align="end" className="w-40 rounded-xl">
            <DropdownMenuItem
              onClick={() => onShare(project)}
              className="cursor-pointer gap-2"
            >
              <Share2 size={14} />
              Share
            </DropdownMenuItem>
            <DropdownMenuItem
              onClick={() => onRename(project.id, project.name)}
              className="cursor-pointer gap-2"
            >
              <Edit2 size={14} />
              Rename
            </DropdownMenuItem>
            <DropdownMenuItem
              onClick={() => onDelete(project.id)}
              className="cursor-pointer gap-2 text-red-600 focus:text-red-600 focus:bg-red-50"
            >
              <Trash2 size={14} />
              Delete
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </Card>
  )
}
