import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useShares } from '@/hooks/queries/use-shares'
import { Skeleton } from '@/components/ui/skeleton'
import { Folder, File, Users, Clock, Shield, Layout } from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'

export const Route = createFileRoute('/dashboard/shared-with-me')({
  component: SharedWithMePage,
})

function SharedWithMePage() {
  const { sharedWithMe, isLoading } = useShares()
  const navigate = useNavigate()

  const handleRowClick = (item: any) => {
    if (item.resourceType === 'PROJECT') {
      navigate({ to: `/dashboard/projects/${item.resourceId}` })
    } else if (item.resourceType === 'FOLDER') {
      if (item.projectId) {
        navigate({ 
          to: `/dashboard/projects/${item.projectId}`,
          search: { folderId: item.resourceId }
        })
      }
    } else if (item.resourceType === 'FILE') {
       if (item.projectId) {
        navigate({ 
          to: `/dashboard/projects/${item.projectId}`,
          search: { folderId: item.folderId || undefined } // Navigate to the parent folder if possible
        })
      }
    }
  }

  return (
    <div className="space-y-8">
      <div>
        <h1 className="display-title text-4xl font-bold text-[var(--sea-ink)]">
          Shared with me
        </h1>
        <p className="text-[var(--sea-ink-soft)] mt-1 font-medium">
          Resources shared by other members
        </p>
      </div>

      {isLoading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((i) => (
            <Skeleton key={i} className="h-16 rounded-2xl w-full" />
          ))}
        </div>
      ) : sharedWithMe.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 text-center">
          <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-3xl bg-[var(--lagoon)]/10 text-[var(--lagoon-deep)]">
            <Users size={40} />
          </div>
          <h2 className="text-2xl font-bold text-[var(--sea-ink)]">
            No shared resources
          </h2>
          <p className="mt-2 text-[var(--sea-ink-soft)] max-w-sm">
            Resources that others share with you will appear here.
          </p>
        </div>
      ) : (
        <div className="island-shell overflow-hidden rounded-[2rem] border-0 bg-white/50 backdrop-blur-sm">
          <Table>
            <TableHeader className="bg-[var(--lagoon)]/5">
              <TableRow className="hover:bg-transparent border-[var(--line)]">
                <TableHead className="font-bold text-[var(--sea-ink-soft)] uppercase text-[10px] tracking-wider py-5 pl-8">Name</TableHead>
                <TableHead className="font-bold text-[var(--sea-ink-soft)] uppercase text-[10px] tracking-wider">Shared By</TableHead>
                <TableHead className="font-bold text-[var(--sea-ink-soft)] uppercase text-[10px] tracking-wider">Shared At</TableHead>
                <TableHead className="font-bold text-[var(--sea-ink-soft)] uppercase text-[10px] tracking-wider pr-8">Permission</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {sharedWithMe.map((item) => (
                <TableRow
                  key={item.id}
                  onClick={() => handleRowClick(item)}
                  className="hover:bg-[var(--lagoon)]/5 border-[var(--line)] transition-colors cursor-pointer group"
                >
                  <TableCell className="py-5 pl-8">
                    <div className="flex items-center gap-3">
                      <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-[var(--lagoon)]/15 text-[var(--lagoon-deep)] group-hover:bg-[var(--lagoon)] group-hover:text-white transition-colors">
                        {item.resourceType === 'PROJECT' ? (
                          <Layout size={20} />
                        ) : item.resourceType === 'FOLDER' ? (
                          <Folder size={20} />
                        ) : (
                          <File size={20} />
                        )}
                      </div>
                      <div>
                        <p className="font-bold text-[var(--sea-ink)] group-hover:text-[var(--lagoon-deep)] transition-colors">{item.resourceName}</p>
                        <p className="text-[10px] text-[var(--sea-ink-soft)] font-bold uppercase tracking-tight">
                          {item.resourceType}
                        </p>
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2">
                      <Avatar className="h-7 w-7 border border-white shadow-sm">
                        <AvatarImage src={`https://api.dicebear.com/7.x/avataaars/svg?seed=${item.sharedBy}`} />
                        <AvatarFallback className="text-[8px] bg-[var(--lagoon)]/20 text-[var(--lagoon-deep)] font-bold">
                          {item.sharedBy.charAt(0).toUpperCase()}
                        </AvatarFallback>
                      </Avatar>
                      <span className="text-sm font-medium text-[var(--sea-ink)]">{item.sharedBy}</span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2 text-sm text-[var(--sea-ink-soft)]">
                      <Clock size={14} />
                      {formatDistanceToNow(new Date(item.createdAt), { addSuffix: true })}
                    </div>
                  </TableCell>
                  <TableCell className="pr-8">
                    <div className="flex items-center gap-2">
                      <Shield size={14} className="text-[var(--lagoon-deep)]" />
                      <span className={`text-xs font-bold px-2 py-1 rounded-lg ${
                        item.permission === 'EDIT'
                          ? 'bg-orange-100 text-orange-700'
                          : 'bg-blue-100 text-blue-700'
                      }`}>
                        {item.permission === 'EDIT' ? 'Editor' : 'Viewer'}
                      </span>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      )}
    </div>
  )
}
