import { createFileRoute } from '@tanstack/react-router'
import { DashboardLayout } from '@/components/layouts/DashboardLayout'
import { Plus, FileText, Image as ImageIcon, Music, Video } from 'lucide-react'
import { Button } from '@/components/ui/button'

export const Route = createFileRoute('/dashboard')({
  component: DashboardPage,
})

function DashboardPage() {
  return (
    <DashboardLayout>
      <div className="flex items-center justify-between mb-10">
        <div>
          <h1 className="display-title text-3xl font-bold text-[var(--sea-ink)]">My Files</h1>
          <p className="text-[var(--sea-ink-soft)] mt-1">Manage and access your secured files</p>
        </div>
        <Button className="h-11 rounded-xl px-6 font-bold shadow-lg gap-2">
          <Plus size={20} />
          Upload New
        </Button>
      </div>

      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {[
          { label: 'Documents', count: '12 files', size: '24.5 MB', icon: FileText, color: 'bg-blue-500/10 text-blue-600' },
          { label: 'Images', count: '85 files', size: '156.2 MB', icon: ImageIcon, color: 'bg-purple-500/10 text-purple-600' },
          { label: 'Audio', count: '5 files', size: '12.8 MB', icon: Music, color: 'bg-pink-500/10 text-pink-600' },
          { label: 'Videos', count: '2 files', size: '420.0 MB', icon: Video, color: 'bg-orange-500/10 text-orange-600' },
        ].map((category) => (
          <div key={category.label} className="island-shell rounded-3xl p-6 transition-all hover:scale-[1.02] cursor-pointer">
            <div className={`w-12 h-12 rounded-2xl ${category.color} flex items-center justify-center mb-4`}>
              <category.icon size={24} />
            </div>
            <h3 className="font-bold text-[var(--sea-ink)] text-lg">{category.label}</h3>
            <div className="flex items-center justify-between mt-1 text-sm text-[var(--sea-ink-soft)] font-medium">
              <span>{category.count}</span>
              <span>{category.size}</span>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-12">
        <h2 className="text-xl font-bold text-[var(--sea-ink)] mb-6">Recent Activity</h2>
        <div className="island-shell rounded-[2rem] overflow-hidden">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-[var(--line)] bg-[var(--lagoon)]/5">
                <th className="px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">Name</th>
                <th className="px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">Type</th>
                <th className="px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">Size</th>
                <th className="px-6 py-4 text-xs font-bold tracking-wider text-[var(--sea-ink-soft)] uppercase">Last Modified</th>
              </tr>
            </thead>
            <tbody>
              {[
                { name: 'Project Proposal.pdf', type: 'PDF', size: '2.4 MB', date: '2 hours ago' },
                { name: 'Holiday Photos.zip', type: 'ZIP', size: '124.5 MB', date: 'Yesterday' },
                { name: 'Financial Report Q1.xlsx', type: 'XLSX', size: '1.1 MB', date: '3 days ago' },
                { name: 'Company Logo.svg', type: 'SVG', size: '45 KB', date: '1 week ago' },
              ].map((file) => (
                <tr key={file.name} className="border-b border-[var(--line)] last:border-0 hover:bg-[var(--lagoon)]/5 transition-colors cursor-pointer">
                  <td className="px-6 py-4 font-semibold text-[var(--sea-ink)]">{file.name}</td>
                  <td className="px-6 py-4 text-[var(--sea-ink-soft)] text-sm">{file.type}</td>
                  <td className="px-6 py-4 text-[var(--sea-ink-soft)] text-sm">{file.size}</td>
                  <td className="px-6 py-4 text-[var(--sea-ink-soft)] text-sm">{file.date}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </DashboardLayout>
  )
}
