import api from '@/lib/axios'
import type { ApiResponse, ShareDto, SharedResourceDto, ResourceType, Permission } from '@/types'

export interface ShareResourceRequest {
  resourceType: ResourceType
  resourceId: string
  userEmail: string
  permission: Permission
}

export interface CreatePublicLinkRequest {
  resourceType: ResourceType
  resourceId: string
  password?: string
  expiresAt?: string
}

export interface UpdateShareRequest {
  permission: Permission
}

export const sharesApi = {
  shareInternal: (data: ShareResourceRequest) =>
    api.post<ApiResponse<ShareDto>>('shares', data),

  createPublicLink: (data: CreatePublicLinkRequest) =>
    api.post<ApiResponse<ShareDto>>('shares/public', data),

  updatePermission: (id: string, permission: Permission) =>
    api.patch<ApiResponse<void>>(`shares/${id}`, { permission }),

  revoke: (id: string) => api.delete<ApiResponse<void>>(`shares/${id}`),

  listForResource: (type: ResourceType, id: string) =>
    api.get<ApiResponse<ShareDto[]>>(`shares/resource/${type}/${id}`),

  listSharedWithMe: () =>
    api.get<ApiResponse<SharedResourceDto[]>>('shares/shared-with-me'),
}
