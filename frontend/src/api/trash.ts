import api from '@/lib/axios'
import type { ApiResponse, TrashItemDto } from '@/types'

export const trashApi = {
  list: () => api.get<ApiResponse<TrashItemDto[]>>('trash'),
  restore: (itemIds: string[]) =>
    api.post<ApiResponse<void>>('trash/restore', { itemIds }),
  deletePermanently: (itemIds: string[]) =>
    api.delete<ApiResponse<void>>('trash', { data: { itemIds } }),
  empty: () => api.delete<ApiResponse<void>>('trash/empty'),
  recoverAll: () => api.post<ApiResponse<void>>('trash/recover-all'),
}
