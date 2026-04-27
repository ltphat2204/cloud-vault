# Frontend Architecture – Detailed Description

## Technology Stack
- **React 19** with **Vite** (module bundler)
- **TypeScript** – strong typing
- **Zustand** – lightweight global state management
- **Tailwind CSS** – utility‑first styling
- **shadcn/ui** – accessible, customisable component library
- **TanStack Query (React Query)** – server state synchronisation
- **TanStack Router** – file‑based type‑safe routing
- **Axios** (or native fetch) – HTTP client

## Project Structure (simplified)
```
src/
├── assets/
├── components/        # reusable UI components (Button, FileCard, FolderTree)
├── features/          # feature modules (auth, projects, files, sharing)
│   ├── auth/
│   │   ├── components/
│   │   ├── hooks/
│   │   └── services/
│   └── files/
│       ├── components/   (FileList, UploadDropZone, ProgressBar)
│       └── services/     (useUploadMutation, useFilesQuery)
├── hooks/             # generic custom hooks
├── lib/               # utilities, axios instances, etc.
├── routes/            # TanStack Router configuration
├── stores/            # Zustand stores (auth store, current project)
└── types/             # shared TypeScript types
```

## State Management
- **Zustand** stores for client‑side only state: authenticated user, current project/folder, UI theme (dark/light).
- **TanStack Query** for all server‑side data (file lists, project lists, notifications). Handles caching, refetching, optimistic updates.

## Real‑Time Updates
- **WebSocket** connection opened after login.
- Events: file uploaded, file deleted, share invitation received.
- On event, TanStack Query invalidates related queries (e.g., `invalidateQueries(['files', projectId])`), so UI refreshes automatically.
- Upload progress bar updates via WebSocket messages.

## Routing
- Protected routes requiring authentication.
- Public routes: login, register, forgot password.
- Dynamic segments: `/projects/:projectId/folders/:folderId`.

## UI Framework
- shadcn/ui provides pre‑built accessible components (dialogs, dropdown menus, tabs, buttons).
- Customised with Tailwind `@theme` to match brand guidelines.
- Responsive grid and flex layouts; mobile‑first.

## Key Features Implementation
- **Drag & drop upload**: react‑dropzone, with progress via Socket.
- **Folder tree**: recursive component, lazy‑loaded children.
- **File preview**: image/PDF using `<img>` or `<iframe>` with signed MinIO URLs.
- **Share management**: popup dialog to add/remove users, set permissions.
