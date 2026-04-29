import { createFileRoute } from '@tanstack/react-router'
import { ActivityList } from '@/components/audit/ActivityList'
import { History, Search, Filter } from 'lucide-react'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'

export const Route = createFileRoute('/dashboard/activities')({
  component: ActivitiesPage,
})

function ActivitiesPage() {
  return (
    <div className="flex flex-col gap-8">
      {/* Header Section */}
      <header className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
        <div className="flex items-center gap-4">
          <div className="flex h-14 w-14 items-center justify-center rounded-[1.5rem] bg-[var(--lagoon-deep)] text-white shadow-xl">
            <History size={32} />
          </div>
          <div>
            <h1 className="display-title text-3xl font-bold text-[var(--sea-ink)]">
              My Activities
            </h1>
            <p className="text-[var(--sea-ink-soft)] font-medium">
              Monitor and track all your actions in CloudVault
            </p>
          </div>
        </div>

        {/* Action Bar */}
        <div className="flex items-center gap-3">
          <div className="relative w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--sea-ink-soft)]" size={18} />
            <Input 
              placeholder="Search actions..." 
              className="pl-10 rounded-2xl border-[var(--line)] bg-white/50 backdrop-blur-sm focus:ring-[var(--lagoon)]"
            />
          </div>
          <Button variant="outline" className="rounded-2xl border-[var(--line)] bg-white/50">
            <Filter size={18} className="mr-2" />
            Filter
          </Button>
        </div>
      </header>

      {/* Activities Content */}
      <div className="flex-1 animate-in fade-in slide-in-from-bottom-4 duration-500">
        <ActivityList />
      </div>
    </div>
  )
}
