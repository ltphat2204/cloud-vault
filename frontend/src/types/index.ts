export interface ApiResponse<T> {
  success: boolean
  message?: string
  data: T
  timestamp: string
}

export interface ProjectDto {
  id: string
  name: string
  ownerId: string
  createdAt: string
  updatedAt: string
}

export interface FolderDto {
  id: string
  name: string
  projectId: string
  parentFolderId: string | null
  createdAt: string
  updatedAt: string
}

export interface FileDto {
  id: string
  name: string
  size: number
  mimeType: string
  projectId: string
  folderId: string | null
  versionNumber: number
  createdAt: string
  updatedAt: string
}

export interface TrashItemDto {
  id: string
  name: string
  type: 'FILE' | 'FOLDER' | 'PROJECT'
  size: number | null
  deletedAt: string
  projectId: string
  originalPath: string
}
