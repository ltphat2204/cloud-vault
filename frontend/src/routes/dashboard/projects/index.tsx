import { createFileRoute } from '@tanstack/react-router'
import { useProjects } from '@/hooks/queries/use-projects'
import { ProjectCard } from '@/components/projects/ProjectCard'
import { ProjectModal } from '@/components/projects/ProjectModal'
import { ShareDialog } from '@/components/shares/ShareDialog'
import { Button } from '@/components/ui/button'
import { Plus, Search } from 'lucide-react'
import { useState } from 'react'
import { Skeleton } from '@/components/ui/skeleton'
import { Input } from '@/components/ui/input'
import type { ProjectDto } from '@/types'

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

export const Route = createFileRoute('/dashboard/projects/')({
  component: ProjectsPage,
})

function ProjectsPage() {
  const {
    projects,
    isLoading,
    createProject,
    updateProject,
    deleteProject,
    isCreating,
  } = useProjects()
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [search, setSearch] = useState('')
  const [editingProject, setEditingProject] = useState<{
    id: string
    name: string
  } | null>(null)
  const [deletingProject, setDeletingProject] = useState<{
    id: string
    name: string
  } | null>(null)
  const [sharingProject, setSharingProject] = useState<ProjectDto | null>(null)

  const filteredProjects = projects.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase()),
  )

  const handleCreate = (name: string) => {
    createProject(name)
    setIsModalOpen(false)
  }

  const handleUpdate = (name: string) => {
    if (editingProject) {
      updateProject({ id: editingProject.id, name })
      setEditingProject(null)
    }
  }

  return (
    <>
      <div className="flex flex-col gap-8 md:flex-row md:items-center md:justify-between mb-10">
        <div>
          <h1 className="display-title text-4xl font-bold text-[var(--sea-ink)]">
            Projects
          </h1>
          <p className="text-[var(--sea-ink-soft)] mt-1 font-medium">
            Manage your storage workspaces
          </p>
        </div>
        <div className="flex items-center gap-4">
          <div className="relative w-64">
            <Search
              size={18}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--sea-ink-soft)]"
            />
            <Input
              placeholder="Search projects..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="h-11 rounded-xl pl-10 border-[var(--line)] bg-white/50 focus:bg-white"
            />
          </div>
          <Button
            onClick={() => setIsModalOpen(true)}
            className="h-11 rounded-xl px-6 font-bold shadow-lg gap-2"
          >
            <Plus size={20} />
            New Project
          </Button>
        </div>
      </div>

      {isLoading ? (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {[1, 2, 3].map((i) => (
            <Skeleton key={i} className="h-48 rounded-[2rem]" />
          ))}
        </div>
      ) : filteredProjects.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 text-center">
          <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-3xl bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
            <Plus size={40} />
          </div>
          <h2 className="text-2xl font-bold text-[var(--sea-ink)]">
            No projects found
          </h2>
          <p className="mt-2 text-[var(--sea-ink-soft)] max-w-sm">
            {search
              ? "We couldn't find any projects matching your search."
              : 'Start by creating your first project to organize your files.'}
          </p>
          {!search && (
            <Button
              onClick={() => setIsModalOpen(true)}
              variant="link"
              className="mt-4 font-bold text-[var(--lagoon-deep)]"
            >
              Create one now
            </Button>
          )}
        </div>
      ) : (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {filteredProjects.map((project) => (
            <ProjectCard
              key={project.id}
              project={project}
              onRename={(id, name) => setEditingProject({ id, name })}
              onDelete={() => setDeletingProject({ id: project.id, name: project.name })}
              onShare={(p) => setSharingProject(p)}
            />
          ))}
        </div>
      )}

      <ProjectModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onConfirm={handleCreate}
        title="Create New Project"
        isLoading={isCreating}
      />

      <ProjectModal
        isOpen={!!editingProject}
        onClose={() => setEditingProject(null)}
        onConfirm={handleUpdate}
        title="Rename Project"
        initialName={editingProject?.name}
      />

      {sharingProject && (
        <ShareDialog
          isOpen={!!sharingProject}
          onClose={() => setSharingProject(null)}
          resourceId={sharingProject.id}
          resourceType="PROJECT"
          resourceName={sharingProject.name}
        />
      )}

      <AlertDialog
        open={!!deletingProject}
        onOpenChange={(open) => !open && setDeletingProject(null)}
      >
        <AlertDialogContent className="rounded-[2rem]">
          <AlertDialogHeader>
            <AlertDialogTitle className="text-2xl font-bold text-[var(--sea-ink)]">
              Move Project to Trash?
            </AlertDialogTitle>
            <AlertDialogDescription className="text-lg">
              Are you sure you want to move project '{deletingProject?.name}' to
              trash? All folders and files inside will also be moved to trash.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel className="rounded-xl">Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={() => {
                if (deletingProject) {
                  deleteProject(deletingProject.id)
                  setDeletingProject(null)
                }
              }}
              className="rounded-xl bg-red-600 hover:bg-red-700"
            >
              Move to Trash
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}
