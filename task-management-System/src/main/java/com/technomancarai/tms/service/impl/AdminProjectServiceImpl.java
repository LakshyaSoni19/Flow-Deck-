package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.dto.request.AssignProjectManagerRequest;
import com.technomancarai.tms.dto.request.ProjectRequest;
import com.technomancarai.tms.dto.request.UpdateProjectStatusRequest;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.entity.Project;
import com.technomancarai.tms.entity.ProjectMember;
import com.technomancarai.tms.entity.Role;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.entity.UserRole;
import com.technomancarai.tms.exception.DuplicateResourceException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.mapper.ProjectMapper;
import com.technomancarai.tms.repository.ProjectMemberRepository;
import com.technomancarai.tms.repository.ProjectRepository;
import com.technomancarai.tms.repository.RoleRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.repository.UserRoleRepository;
import com.technomancarai.tms.service.AdminProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProjectServiceImpl implements AdminProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProjectMemberRepository projectMemberRepository;
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

        User manager = null;
        if (request.getManagerId() != null) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager user not found with ID: " + request.getManagerId()));
            project.setManager(manager);
        }

        Project savedProject = projectRepository.save(project);
        if (manager != null) {
            ensureManagerRoleAndProjectMember(savedProject, manager);
        }

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

        User manager = null;
        if (request.getManagerId() != null) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager user not found with ID: " + request.getManagerId()));
            project.setManager(manager);
        }

        Project updatedProject = projectRepository.save(project);
        if (manager != null) {
            ensureManagerRoleAndProjectMember(updatedProject, manager);
        }

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
        ensureManagerRoleAndProjectMember(updatedProject, manager);

        return projectMapper.toProjectResponse(updatedProject);
    }

    private void ensureManagerRoleAndProjectMember(Project project, User manager) {
        if (project == null || manager == null) {
            return;
        }

        // 1. Automatically assign ROLE_PROJECT_MANAGER role if user does not already have it
        Role pmRole = roleRepository.findByName("ROLE_PROJECT_MANAGER")
                .orElseGet(() -> {
                    Role r = Role.builder().name("ROLE_PROJECT_MANAGER").build();
                    r.setIsActive(true);
                    return roleRepository.save(r);
                });

        boolean hasPmRole = userRoleRepository.findByUserId(manager.getId()).stream()
                .anyMatch(ur -> ur.getRole() != null && "ROLE_PROJECT_MANAGER".equals(ur.getRole().getName()));

        if (!hasPmRole) {
            UserRole userRole = UserRole.builder()
                    .user(manager)
                    .role(pmRole)
                    .build();
            userRole.setIsActive(true);
            userRoleRepository.save(userRole);
        }

        // 2. Automatically record assigned manager as a ProjectMember in project_member table
        if (project.getId() != null) {
            if (!projectMemberRepository.existsByProjectIdAndUserId(project.getId(), manager.getId())) {
                ProjectMember member = ProjectMember.builder()
                        .project(project)
                        .user(manager)
                        .joinedDate(LocalDate.now())
                        .build();
                member.setIsActive(true);
                projectMemberRepository.save(member);
            }
        }
    }
}
