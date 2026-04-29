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

export type ResourceType = 'PROJECT' | 'FOLDER' | 'FILE'
export type Permission = 'VIEW' | 'EDIT'

export interface ShareDto {
  id: string
  resourceType: ResourceType
  resourceId: string
  sharedWithUser?: {
    id: string
    email: string
  }
  permission: Permission
  accessToken?: string
  publicUrl?: string
  expiresAt?: string
  createdAt: string
}

export interface SharedResourceDto {
  id: string
  resourceType: ResourceType
  resourceId: string
  resourceName: string
  sharedBy: string
  permission: Permission
  createdAt: string
}

export type NotificationType = 'SHARE_RECEIVED' | 'SHARE_UPDATED' | 'SHARE_REVOKED' | 'PROJECT_INVITATION' | 'SYSTEM_ALERT'

export interface NotificationDto {
  id: string
  type: NotificationType
  message: string
  read: boolean
  metadata: Record<string, string>
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

