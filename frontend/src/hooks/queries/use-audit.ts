import { useQuery } from '@tanstack/react-query'
import { auditApi } from '@/api/audit'
import type { AuditParams } from '@/api/audit'
import type { ResourceType } from '@/types'

export function useMyActivities(params: AuditParams = {}) {
  const activitiesQuery = useQuery({
    queryKey: ['activities', params],
    queryFn: () => auditApi.listMyActivities(params).then((res) => res.data.data),
  })

  return {
    activities: activitiesQuery.data?.content ?? [],
    totalElements: activitiesQuery.data?.totalElements ?? 0,
    totalPages: activitiesQuery.data?.totalPages ?? 0,
    isLoading: activitiesQuery.isLoading,
    isError: activitiesQuery.isError,
    refetch: activitiesQuery.refetch,
  }
}

export function useResourceHistory(resourceId: string, resourceType: ResourceType, params: { page?: number; size?: number } = {}) {
  const historyQuery = useQuery({
    queryKey: ['resource-history', resourceId, resourceType, params],
    queryFn: () => auditApi.getResourceHistory(resourceId, resourceType, params).then((res) => res.data.data),
    enabled: !!resourceId && !!resourceType,
  })

  return {
    history: historyQuery.data?.content ?? [],
    totalElements: historyQuery.data?.totalElements ?? 0,
    totalPages: historyQuery.data?.totalPages ?? 0,
    isLoading: historyQuery.isLoading,
    isError: historyQuery.isError,
    refetch: historyQuery.refetch,
  }
}
