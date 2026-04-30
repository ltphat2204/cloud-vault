# CloudVault – Use Cases Agenda

This document lists all identified use cases for the CloudVault system.  
Each use case is detailed in its own file following the naming pattern `##-<short-name>.md`.

### 1. Identity & Access Management
| ID      | Use Case                                   | Description                                                       | File                             |
| ------- | ------------------------------------------ | ----------------------------------------------------------------- | -------------------------------- |
| UC-01   | User Registration                          | Create a new account with email and password.                     | `01-user-registration.md`       |
| UC-24   | Verify Email                               | Verify account ownership via email token.                         | `24-verify-email.md`            |
| UC-02   | User Login                                 | Authenticate and receive JWT tokens.                              | `02-user-login.md`              |
| UC-03   | User Logout                                | Invalidate current session/tokens.                                | `03-user-logout.md`             |
| UC-04   | Forgot Password / Reset                    | Request password reset email and set new password.                | `04-forgot-password.md`         |

### 2. Project Management
| ID      | Use Case                                   | Description                                                       | File                             |
| ------- | ------------------------------------------ | ----------------------------------------------------------------- | -------------------------------- |
| UC-05   | Create Project                             | Create a new storage project owned by the user.                   | `05-create-project.md`          |
| UC-06   | Manage Project                             | Rename or delete an existing project.                             | `06-manage-project.md`          |
| UC-07   | List Projects                              | View all projects belonging to the user.                          | `07-list-projects.md`           |

### 3. File & Folder Management
| ID      | Use Case                                   | Description                                                       | File                             |
| ------- | ------------------------------------------ | ----------------------------------------------------------------- | -------------------------------- |
| UC-08   | Create Folder                              | Create a new folder inside a project or another folder.           | `08-create-folder.md`           |
| UC-09   | Manage Folder                              | Rename, move, or soft‑delete a folder.                            | `09-manage-folder.md`          |
| UC-10   | Upload Files                               | Upload one or more files with real‑time progress.                 | `10-upload-files.md`            |
| UC-11   | Download Files                             | Download a single file or a folder as a ZIP archive.              | `11-download-files.md`          |
| UC-12   | View File Details                          | See metadata (name, size, type, dates, owner).                    | `12-view-file-details.md`       |
| UC-19   | File Versioning                            | View version history and restore a previous version of a file.    | `19-file-versioning.md`         |

### 4. Sharing & Collaboration
| ID      | Use Case                                   | Description                                                       | File                             |
| ------- | ------------------------------------------ | ----------------------------------------------------------------- | -------------------------------- |
| UC-15   | Share Resource                             | Share a project, folder, or file with another user (VIEW/EDIT).   | `15-share-resource.md`          |
| UC-16   | Manage Share Permissions                   | View and revoke existing shares.                                  | `16-manage-shares.md`           |
| UC-17   | Public Link Sharing                        | Generate a public link with optional password and expiry.         | `17-public-link-sharing.md`     |

### 5. Trash & Recovery
| ID      | Use Case                                   | Description                                                       | File                             |
| ------- | ------------------------------------------ | ----------------------------------------------------------------- | -------------------------------- |
| UC-13   | Trash Management                           | Restore or permanently delete items from the trash bin.           | `13-trash-management.md`        |

### 6. Search & Discovery
| ID      | Use Case                                   | Description                                                       | File                             |
| ------- | ------------------------------------------ | ----------------------------------------------------------------- | -------------------------------- |
| UC-14   | Search Files/Folders                       | Search by name globally or within a project/folder.               | `14-search-files-folders.md`    |

### 7. System Utilities
| ID      | Use Case                                   | Description                                                       | File                             |
| ------- | ------------------------------------------ | ----------------------------------------------------------------- | -------------------------------- |
| UC-18   | View Notifications                         | List and mark notifications as read.                              | `18-view-notifications.md`      |
| UC-21   | Real‑Time Updates                          | Receive live file list changes and notifications via WebSocket.   | `21-real-time-updates.md`       |
| UC-22   | Pagination & Sorting                       | Navigate paginated file/folder lists and sort by attributes.      | `22-pagination-sorting.md`      |
| UC-25   | View Activity History                      | Track operations on resources via audit logs.                     | `25-view-activity-history.md`   |

> Note: Some auxiliary features (like dark/light mode toggle) are considered UI concerns and not formal use cases.
