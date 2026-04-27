# 1. Introduction, Goals & Scope, Target Users

## 1.1 Introduction

**Project Name:** CloudVault – Personalized Cloud File Storage and Sharing System

**General Description:**

CloudVault is a web platform that enables **multiple users** to register accounts and use their own private file storage space. Each user can create multiple **Projects**. Within each Project, users can freely organize files and folders.

The system provides all basic storage features along with advanced capabilities such as **flexible sharing** (sharing an entire Project, a Folder, or individual Files), **real-time notifications**, **asynchronous event processing**, and high performance through caching.

The project aims to build a complete, secure, easy-to-use, and scalable system, suitable for showcasing in a portfolio and demonstrating modern full-stack skills.

## 1.2 Goals and Scope

**Main Goals:**

- Allow users to manage files and folders visually, like a personal cloud drive.
- Support multiple users with strictly isolated data.
- Enable flexible sharing between users (Project / Folder / File).
- Handle smooth file uploads and downloads with real-time progress.
- Ensure a fast, reliable system through caching and asynchronous event processing.
- Build a beautiful, responsive UI that is easy to use on both desktop and mobile devices.

**Scope of the Project:**

- Complete user management and authentication functionality.
- Management of Projects, Folders, and Files.
- Multi-level sharing (Project, Folder, File).
- Upload/Download with real-time progress.
- Real-time notifications and updates when changes occur.
- Soft delete (trash bin), restore, permanent delete.
- File/folder search.
- Basic file versioning.
- Basic audit log (activity history).
- Support for multiple file types (document, image, video, archive, etc.).

**Out of Scope (not included in this version):**

Direct file editing on the cloud, desktop sync app, storage billing, AI file analysis.

## 1.3 Target Users

- **Regular User:** Can register, create Projects, upload files, share with others, and receive notifications.
- **Project / Folder / File Owner:** Has full management rights over sharing, deletion, and restoration.
- **Shared Recipient:** Can view, download, and upload (depending on granted permissions: view-only or edit).