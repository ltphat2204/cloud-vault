import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { trashApi } from '@/api/trash'
import { toast } from 'sonner'

export function useTrash() {
  const queryClient = useQueryClient()

  const trashQuery = useQuery({
    queryKey: ['trash'],
    queryFn: () => trashApi.list().then((res) => res.data.data),
  })

  const restoreMutation = useMutation({
    mutationFn: (ids: string[]) => trashApi.restore(ids),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trash'] })
      queryClient.invalidateQueries({ queryKey: ['folders'] })
      queryClient.invalidateQueries({ queryKey: ['files'] })
      toast.success('Items restored successfully')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to restore items'
      toast.error(message)
    },
  })

  const deleteMutation = useMutation({
    mutationFn: (ids: string[]) => trashApi.deletePermanently(ids),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trash'] })
      toast.success('Items permanently deleted')
    },
    onError: (error: any) => {
      const message =
        error.response?.data?.message || 'Failed to delete items permanently'
      toast.error(message)
    },
  })

  const emptyTrashMutation = useMutation({
    mutationFn: () => trashApi.empty(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trash'] })
      toast.success('Trash emptied')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to empty trash'
      toast.error(message)
    },
  })

  return {
    trashItems: trashQuery.data ?? [],
    isLoading: trashQuery.isLoading,
    restore: restoreMutation.mutate,
    deletePermanently: deleteMutation.mutate,
    emptyTrash: emptyTrashMutation.mutate,
    isRestoring: restoreMutation.isPending,
    isDeleting: deleteMutation.isPending,
    isEmptying: emptyTrashMutation.isPending,
  }
}
