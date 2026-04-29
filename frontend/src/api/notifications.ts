import api from '@/lib/axios'
import type { ApiResponse, NotificationDto, PageResponse } from '@/types'

export interface ListNotificationsParams {
  page?: number
  size?: number
  unreadOnly?: boolean
}

export const notificationsApi = {
  list: (params: ListNotificationsParams = {}) =>
    api.get<ApiResponse<PageResponse<NotificationDto>>>('notifications', { params }),

  markAsRead: (id: string) =>
    api.patch<ApiResponse<void>>(`notifications/${id}/read`),

  markAllAsRead: () =>
    api.patch<ApiResponse<void>>('notifications/read-all'),
}
