import api from '@/lib/axios'
import type { ApiResponse, ProjectDto } from '@/types'

export const projectsApi = {
  list: () => api.get<ApiResponse<ProjectDto[]>>('projects'),
  get: (id: string) => api.get<ApiResponse<ProjectDto>>(`projects/${id}`),
  create: (name: string) =>
    api.post<ApiResponse<ProjectDto>>('projects', { name }),
  update: (id: string, name: string) =>
    api.patch<ApiResponse<ProjectDto>>(`projects/${id}`, { name }),
  delete: (id: string) => api.delete<ApiResponse<void>>(`projects/${id}`),
}
