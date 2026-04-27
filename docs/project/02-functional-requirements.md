# 2. Functional Requirements

## 2.1 User Management & Authentication

- Account registration (email + password).
- Login / Logout.
- Forgot password (reset password).
- JWT Authentication with Refresh Token.
- Each user has a separate storage space.

## 2.2 Project Management

- Create, rename, delete Projects.
- Each Project is a separate space containing Folders and Files.
- View the list of all the user’s Projects.

## 2.3 Folder and File Management

- Create, rename, move, delete Folders (supports tree-like folder structure).
- File upload (supports multiple files at once, drag & drop, real-time progress bar).
- File download (single file or multiple files compressed as a zip archive).
- View file details (name, size, type, creation/modification date, owner).
- Soft delete → move to **Trash Bin**, with options to restore or permanently delete.
- Search files/folders by name (globally or within a specific Project/Folder).

## 2.4 Sharing

- **Share an entire Project:** Send an invitation to another user with View or Edit permission.
- **Share a Folder:** Share a folder and all its contents.
- **Share a File:** Share individual files.
- Share via **public link** (optionally password-protected or with an expiration date).
- Manage the list of people who have been granted access and revoke permissions at any time.
- When a change occurs (new upload, edit, delete) inside shared content → the shared recipient receives a real-time notification.

## 2.5 Real-time & Notification

- Show real-time upload progress (percentage, speed).
- Instant notifications when:
    - Someone shares a Project/Folder/File with you.
    - A new file is uploaded to a Project/Folder you are following.
    - Sharing permissions change.
    - A file is deleted or restored.
- Real-time updates to the file/folder list when another user makes changes (if the same Project is open).

## 2.6 File Versioning

- Uploading a file with the same name → automatically creates a new version.
- View the version history and restore a previous version.

## 2.7 Additional Supporting Features

- Preview certain file types (image, PDF, text).
- Pagination and sorting of file listings (by name, size, modification date).
- Configurable upload file size limit.