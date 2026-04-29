import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { notificationsApi } from '@/api/notifications'
import type { ListNotificationsParams } from '@/api/notifications'
import { toast } from 'sonner'

export function useNotifications(params: ListNotificationsParams = {}) {
  const queryClient = useQueryClient()

  const notificationsQuery = useQuery({
    queryKey: ['notifications', params],
    queryFn: () => notificationsApi.list(params).then((res) => res.data.data),
  })

  const markAsReadMutation = useMutation({
    mutationFn: (id: string) => notificationsApi.markAsRead(id),
    onMutate: async (id) => {
      const queryKey = ['notifications', params]
      await queryClient.cancelQueries({ queryKey })
      const previousNotifications = queryClient.getQueryData(queryKey)
      
      queryClient.setQueryData(queryKey, (old: any) => {
        if (!old) return old
        return {
          ...old,
          content: old.content.map((n: any) => 
            n.id === id ? { ...n, read: true } : n
          )
        }
      })
      
      return { previousNotifications, queryKey }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] })
    },
    onError: (error: any, _id, context) => {
      if (context?.previousNotifications) {
        queryClient.setQueryData(context.queryKey, context.previousNotifications)
      }
      console.error('Failed to mark notification as read:', error)
      const message = error.response?.data?.message || 'Failed to mark notification as read'
      toast.error(message)
    },
  })

  const markAllAsReadMutation = useMutation({
    mutationFn: () => notificationsApi.markAllAsRead(),
    onMutate: async () => {
      const queryKey = ['notifications', params]
      await queryClient.cancelQueries({ queryKey })
      const previousNotifications = queryClient.getQueryData(queryKey)
      
      queryClient.setQueryData(queryKey, (old: any) => {
        if (!old) return old
        return {
          ...old,
          content: old.content.map((n: any) => ({ ...n, read: true }))
        }
      })
      
      return { previousNotifications, queryKey }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] })
      toast.success('All notifications marked as read')
    },
    onError: (error: any, _variables, context) => {
      if (context?.previousNotifications) {
        queryClient.setQueryData(context.queryKey, context.previousNotifications)
      }
      console.error('Failed to mark all notifications as read:', error)
      const message = error.response?.data?.message || 'Failed to mark all as read'
      toast.error(message)
    },
  })

  return {
    notifications: notificationsQuery.data?.content ?? [],
    totalElements: notificationsQuery.data?.totalElements ?? 0,
    unreadCount: (notificationsQuery.data?.content ?? []).filter((n: any) => !n.read).length,
    isLoading: notificationsQuery.isLoading,
    markAsRead: markAsReadMutation.mutate,
    markAllAsRead: markAllAsReadMutation.mutate,
    isMarkingRead: markAsReadMutation.isPending || markAllAsReadMutation.isPending,
  }
}
