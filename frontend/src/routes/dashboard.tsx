import { createFileRoute, Outlet } from '@tanstack/react-router'
import { DashboardLayout } from '@/components/layouts/DashboardLayout'

export const Route = createFileRoute('/dashboard')({
  component: DashboardLayoutWrapper,
})

function DashboardLayoutWrapper() {
  return (
    <DashboardLayout>
      <Outlet />
    </DashboardLayout>
  )
}
