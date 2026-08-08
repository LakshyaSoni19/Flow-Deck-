package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.dto.request.AssignProjectManagerRequest;
import com.technomancarai.tms.dto.request.ProjectRequest;
import com.technomancarai.tms.dto.request.UpdateProjectStatusRequest;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.entity.Project;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.exception.DuplicateResourceException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.mapper.ProjectMapper;
import com.technomancarai.tms.repository.ProjectRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.service.AdminProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProjectServiceImpl implements AdminProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectResponse createProject(ProjectRequest request, String adminEmail) {
        if (projectRepository.existsByProjectCode(request.getProjectCode())) {
            throw new DuplicateResourceException("Project already exists with code: " + request.getProjectCode());
        }

        Project project = projectMapper.toProject(request);
        project.setIsActive(true);

        if (adminEmail != null) {
            userRepository.findByEmail(adminEmail).ifPresent(project::setCreatedByUser);
        }

        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager user not found with ID: " + request.getManagerId()));
            project.setManager(manager);
        }

        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectResponse(savedProject);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

        if (!project.getProjectCode().equalsIgnoreCase(request.getProjectCode())
                && projectRepository.existsByProjectCode(request.getProjectCode())) {
            throw new DuplicateResourceException("Project already exists with code: " + request.getProjectCode());
        }

        projectMapper.updateProjectFromAdminRequest(request, project);
        project.setProjectCode(request.getProjectCode());

        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager user not found with ID: " + request.getManagerId()));
            project.setManager(manager);
        }

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectResponse(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        projectRepository.delete(project);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> getAllProjects(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Project> projectsPage = projectRepository.findAll(pageable);

        List<ProjectResponse> content = projectsPage.getContent().stream()
                .map(projectMapper::toProjectResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProjectResponse>builder()
                .content(content)
                .pageNo(projectsPage.getNumber())
                .pageSize(projectsPage.getSize())
                .totalElements(projectsPage.getTotalElements())
                .totalPages(projectsPage.getTotalPages())
                .isLast(projectsPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse changeProjectStatus(Long id, UpdateProjectStatusRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        project.setStatus(request.getStatus());
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectResponse(updatedProject);
    }

    @Override
    @Transactional
    public ProjectResponse assignProjectManager(Long id, AssignProjectManagerRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager user not found with ID: " + request.getManagerId()));

        project.setManager(manager);
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectResponse(updatedProject);
    }
}
