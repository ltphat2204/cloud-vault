package ltphat.cloudvault.backend.projects.domain.exception;

import java.util.UUID;

public class ProjectNotFoundException extends ProjectException {
    public ProjectNotFoundException(UUID id) {
        super("Project not found: " + id);
    }
}
