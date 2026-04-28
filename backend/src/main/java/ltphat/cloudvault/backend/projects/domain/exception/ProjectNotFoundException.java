package ltphat.cloudvault.backend.projects.domain.exception;

import java.util.UUID;

public class ProjectNotFoundException extends ProjectException {
    private static final long serialVersionUID = 1L;
    public ProjectNotFoundException(UUID id) {
        super("Project not found: " + id);
    }
}
