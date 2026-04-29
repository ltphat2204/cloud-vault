import api from '@/lib/axios'
import type { ApiResponse, ActivityDto, PageResponse, ResourceType, AuditAction } from '@/types'

export interface AuditParams {
  page?: number
  size?: number
  action?: AuditAction
  resourceType?: ResourceType
}

export const auditApi = {
  listMyActivities: (params?: AuditParams) =>
    api.get<ApiResponse<PageResponse<ActivityDto>>>('audit', { params }),
  
  getResourceHistory: (resourceId: string, resourceType: ResourceType, params?: { page?: number; size?: number }) =>
    api.get<ApiResponse<PageResponse<ActivityDto>>>(`audit/resources/${resourceId}`, {
      params: { ...params, resourceType },
    }),
}
