import api from '@/lib/axios'
import type { ApiResponse, FileDto } from '@/types'

export const filesApi = {
  list: (projectId: string, folderId?: string) =>
    api.get<ApiResponse<FileDto[]>>('files', {
      params: { projectId, folderId },
    }),
  get: (id: string) => api.get<ApiResponse<FileDto>>(`files/${id}`),
  upload: (projectId: string, file: File, folderId?: string) => {
    const formData = new FormData()
    formData.append('projectId', projectId)
    formData.append('file', file)
    if (folderId) formData.append('folderId', folderId)
    return api.post<ApiResponse<FileDto>>('files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
  download: async (id: string, fileName: string) => {
    const response = await api.get(`files/${id}/download`, {
      responseType: 'blob',
    })
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', fileName)
    document.body.appendChild(link)
    link.click()
    link.remove()
  },
  update: (id: string, name: string) =>
    api.patch<ApiResponse<FileDto>>(`files/${id}`, { name }),
  move: (id: string, targetFolderId?: string) =>
    api.put<ApiResponse<void>>(`files/${id}/move`, { targetFolderId }),
  delete: (id: string) => api.delete<ApiResponse<void>>(`files/${id}`),
}
