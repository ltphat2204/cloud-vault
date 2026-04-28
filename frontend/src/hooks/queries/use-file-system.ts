import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { foldersApi } from '@/api/folders'
import { filesApi } from '@/api/files'
import { toast } from 'sonner'

export function useFolderContents(projectId: string, folderId?: string) {
  const queryClient = useQueryClient()

  const foldersQuery = useQuery({
    queryKey: ['folders', projectId, folderId],
    queryFn: () =>
      foldersApi.list(projectId, folderId).then((res) => res.data.data),
    enabled: !!projectId,
  })

  const filesQuery = useQuery({
    queryKey: ['files', projectId, folderId],
    queryFn: () =>
      filesApi.list(projectId, folderId).then((res) => res.data.data),
    enabled: !!projectId,
  })

  const createFolderMutation = useMutation({
    mutationFn: (name: string) =>
      foldersApi.create({ name, projectId, parentFolderId: folderId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['folders', projectId, folderId],
      })
      toast.success('Folder created successfully')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to create folder'
      toast.error(message)
    },
  })

  const uploadFileMutation = useMutation({
    mutationFn: (file: File) => filesApi.upload(projectId, file, folderId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['files', projectId, folderId],
      })
      toast.success('File uploaded successfully')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to upload file'
      toast.error(message)
    },
  })

  return {
    folders: foldersQuery.data ?? [],
    files: filesQuery.data ?? [],
    isLoading: foldersQuery.isLoading || filesQuery.isLoading,
    createFolder: createFolderMutation.mutate,
    uploadFile: uploadFileMutation.mutate,
    isCreatingFolder: createFolderMutation.isPending,
    isUploadingFile: uploadFileMutation.isPending,
  }
}

export function useFileSystemActions() {
  const queryClient = useQueryClient()

  const deleteFolder = useMutation({
    mutationFn: (id: string) => foldersApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['folders'] })
      toast.success('Folder moved to trash')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to delete folder'
      toast.error(message)
    },
  })

  const deleteFile = useMutation({
    mutationFn: (id: string) => filesApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['files'] })
      toast.success('File moved to trash')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to delete file'
      toast.error(message)
    },
  })

  const renameFolder = useMutation({
    mutationFn: ({ id, name }: { id: string; name: string }) =>
      foldersApi.update(id, name),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['folders'] })
      toast.success('Folder renamed')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to rename folder'
      toast.error(message)
    },
  })

  const renameFile = useMutation({
    mutationFn: ({ id, name }: { id: string; name: string }) =>
      filesApi.update(id, name),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['files'] })
      toast.success('File renamed')
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Failed to rename file'
      toast.error(message)
    },
  })

  return {
    deleteFolder: deleteFolder.mutate,
    deleteFile: deleteFile.mutate,
    renameFolder: renameFolder.mutate,
    renameFile: renameFile.mutate,
  }
}
