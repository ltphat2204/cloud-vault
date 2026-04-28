import { create } from 'zustand'

export interface BreadcrumbItem {
  id: string
  name: string
  parentFolderId?: string | null
}

interface NavigationState {
  currentFolderId: string | undefined
  path: BreadcrumbItem[]
  setFolder: (folderId: string | undefined, name?: string) => void
  setPath: (path: BreadcrumbItem[]) => void
  reset: () => void
  navigateTo: (folderId: string | undefined, path?: BreadcrumbItem[]) => void
}

export const useNavigationStore = create<NavigationState>((set) => ({
  currentFolderId: undefined,
  path: [],

  setFolder: (folderId) =>
    set(() => {
      if (folderId === undefined) {
        return { currentFolderId: undefined, path: [] }
      }
      return { currentFolderId: folderId }
    }),

  setPath: (path) => set({ path }),

  reset: () => set({ currentFolderId: undefined, path: [] }),

  navigateTo: (folderId, path) =>
    set({
      currentFolderId: folderId,
      path: path || [],
    }),
}))
