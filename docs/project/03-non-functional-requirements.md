# 3. Non-Functional Requirements

- **Performance:** The system must respond quickly (< 2 seconds for most listing tasks), large uploads must not freeze the UI.
- **Scalability:** Architecture separates metadata (database) from actual file storage (object storage).
- **Security:** Protect user data, enforce strict access control, prevent malicious file uploads (check file type and size).
- **Reliability:** Use an event-driven architecture for background jobs (thumbnail generation, search indexing, notifications).
- **Usability:** Clean, responsive interface with support for dark/light mode.
- **Maintainability:** Clean code, API documentation, detailed logging.