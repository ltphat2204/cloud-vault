import api from '@/lib/axios'
import type { ApiResponse, FolderDto } from '@/types'

export interface CreateFolderRequest {
  name: string
  projectId: string
  parentFolderId?: string
}

export const foldersApi = {
  list: (projectId: string, parentFolderId?: string) =>
    api.get<ApiResponse<FolderDto[]>>('folders', {
      params: { projectId, parentFolderId },
    }),
  get: (id: string) => api.get<ApiResponse<FolderDto>>(`folders/${id}`),
  create: (data: CreateFolderRequest) =>
    api.post<ApiResponse<FolderDto>>('folders', data),
  update: (id: string, name: string) =>
    api.patch<ApiResponse<FolderDto>>(`folders/${id}`, { name }),
  move: (id: string, targetParentFolderId?: string) =>
    api.patch<ApiResponse<FolderDto>>(`folders/${id}/move`, {
      targetParentFolderId,
    }),
  delete: (id: string) => api.delete<ApiResponse<void>>(`folders/${id}`),
  getPath: (id: string) => api.get<ApiResponse<FolderDto[]>>(`folders/${id}/path`),
  listAll: (projectId: string) => api.get<ApiResponse<FolderDto[]>>(`folders/all`, { params: { projectId } }),
}
