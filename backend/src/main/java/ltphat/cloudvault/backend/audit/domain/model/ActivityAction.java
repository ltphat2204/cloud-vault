package ltphat.cloudvault.backend.audit.domain.model;

public enum ActivityAction {
    // Project actions
    PROJECT_CREATED,
    PROJECT_RENAMED,
    PROJECT_DELETED,
    PROJECT_RESTORED,
    PROJECT_PERMANENTLY_DELETED,
    
    // Folder actions
    FOLDER_CREATED,
    FOLDER_RENAMED,
    FOLDER_MOVED,
    FOLDER_DELETED,
    FOLDER_RESTORED,
    FOLDER_PERMANENTLY_DELETED,
    
    // File actions
    FILE_UPLOADED,
    FILE_DOWNLOADED,
    FILE_RENAMED,
    FILE_MOVED,
    FILE_DELETED,
    FILE_RESTORED,
    FILE_PERMANENTLY_DELETED,
    FILE_VERSION_RESTORED,
    
    // Sharing actions
    RESOURCE_SHARED,
    SHARING_REVOKED,
    PUBLIC_LINK_CREATED,
    SHARING_UPDATED,
    
    // Trash actions
    TRASH_EMPTIED,
    TRASH_RECOVERED
}
