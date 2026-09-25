package com.technomancarai.tms.service;

import com.technomancarai.tms.entity.Project;
import com.technomancarai.tms.exception.ForbiddenException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectSecurityService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public Project verifyProjectOwnership(Long projectId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (project.getManager() == null || !project.getManager().getEmail().equalsIgnoreCase(userEmail)) {
            throw new ForbiddenException("Access denied: You are not the assigned Project Manager for this project");
        }

        return project;
    }
}
