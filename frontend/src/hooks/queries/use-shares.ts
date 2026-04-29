import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { sharesApi } from '@/api/shares'
import type { ShareResourceRequest, CreatePublicLinkRequest } from '@/api/shares'
import { toast } from 'sonner'
import type { ResourceType, Permission } from '@/types'

export function useShares(resourceType?: ResourceType, resourceId?: string) {
  const queryClient = useQueryClient()

  const sharesQuery = useQuery({
    queryKey: ['shares', resourceType, resourceId],
    queryFn: () => sharesApi.listForResource(resourceType!, resourceId!).then((res) => res.data.data),
    enabled: !!resourceType && !!resourceId,
  })

  const sharedWithMeQuery = useQuery({
    queryKey: ['shares', 'shared-with-me'],
    queryFn: () => sharesApi.listSharedWithMe().then((res) => res.data.data),
  })

  const shareInternalMutation = useMutation({
    mutationFn: (data: ShareResourceRequest) => sharesApi.shareInternal(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['shares'] })
      toast.success('Resource shared successfully')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to share resource'
      toast.error(message)
    },
  })

  const createPublicLinkMutation = useMutation({
    mutationFn: (data: CreatePublicLinkRequest) => sharesApi.createPublicLink(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['shares'] })
      toast.success('Public link created successfully')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to create public link'
      toast.error(message)
    },
  })

  const updatePermissionMutation = useMutation({
    mutationFn: ({ id, permission }: { id: string; permission: Permission }) =>
      sharesApi.updatePermission(id, permission),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['shares'] })
      toast.success('Permission updated successfully')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to update permission'
      toast.error(message)
    },
  })

  const revokeMutation = useMutation({
    mutationFn: (id: string) => sharesApi.revoke(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['shares'] })
      toast.success('Access revoked successfully')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to revoke access'
      toast.error(message)
    },
  })

  return {
    shares: sharesQuery.data ?? [],
    sharedWithMe: sharedWithMeQuery.data ?? [],
    isLoading: sharesQuery.isLoading || sharedWithMeQuery.isLoading,
    shareInternal: shareInternalMutation.mutateAsync,
    createPublicLink: createPublicLinkMutation.mutateAsync,
    updatePermission: updatePermissionMutation.mutate,
    revokeShare: revokeMutation.mutate,
    isSharing: shareInternalMutation.isPending || createPublicLinkMutation.isPending,
  }
}
