package ltphat.cloudvault.backend.projects.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.projects.application.dto.CreateProjectRequest;
import ltphat.cloudvault.backend.projects.application.dto.ProjectDto;
import ltphat.cloudvault.backend.projects.application.dto.UpdateProjectRequest;
import ltphat.cloudvault.backend.projects.application.mapper.ProjectApplicationMapper;
import ltphat.cloudvault.backend.projects.application.service.IProjectService;
import ltphat.cloudvault.backend.projects.domain.exception.ProjectNotFoundException;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository projectRepository;
    private final ProjectApplicationMapper projectApplicationMapper;

    @Override
    @Transactional
    public ProjectDto createProject(CreateProjectRequest request, UUID ownerId) {
        Project project = Project.createNew(request.getName(), ownerId);
        Project savedProject = projectRepository.save(project);
        
        // TODO: Initialize root folder for the project when Folders module is implemented
        
        return projectApplicationMapper.toDto(savedProject);
    }

    @Override
    public ProjectDto getProject(UUID id, UUID ownerId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        
        if (!project.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have access to this project");
        }
        
        if (project.isDeleted()) {
            throw new ProjectNotFoundException(id);
        }
        
        return projectApplicationMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> listProjects(UUID ownerId) {
        return projectRepository.findByOwnerId(ownerId).stream()
                .map(projectApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDto updateProject(UUID id, UpdateProjectRequest request, UUID ownerId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        
        if (!project.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have access to this project");
        }
        
        if (project.isDeleted()) {
            throw new ProjectNotFoundException(id);
        }
        
        project.update(request.getName());
        Project updatedProject = projectRepository.save(project);
        
        return projectApplicationMapper.toDto(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(UUID id, UUID ownerId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        
        if (!project.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have access to this project");
        }
        
        project.softDelete();
        projectRepository.save(project);
    }
}
