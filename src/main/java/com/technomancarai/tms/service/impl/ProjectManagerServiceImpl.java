package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.dto.request.AddMemberRequest;
import com.technomancarai.tms.dto.request.AssignTaskRequest;
import com.technomancarai.tms.dto.request.PmTaskRequest;
import com.technomancarai.tms.dto.request.SetDueDateRequest;
import com.technomancarai.tms.dto.request.UpdateTaskPriorityPmRequest;
import com.technomancarai.tms.dto.request.UpdateTaskStatusPmRequest;
import com.technomancarai.tms.dto.response.EmployeeTaskSummaryResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectMemberResponse;
import com.technomancarai.tms.dto.response.ProjectProgressResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.dto.response.ProjectStatsResponse;
import com.technomancarai.tms.dto.response.TaskResponse;
import com.technomancarai.tms.entity.Project;
import com.technomancarai.tms.entity.ProjectMember;
import com.technomancarai.tms.entity.Task;
import com.technomancarai.tms.entity.TaskPriority;
import com.technomancarai.tms.entity.TaskStatus;
import com.technomancarai.tms.entity.TaskType;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.exception.BadRequestException;
import com.technomancarai.tms.exception.DuplicateResourceException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.mapper.ProjectMapper;
import com.technomancarai.tms.mapper.ProjectMemberMapper;
import com.technomancarai.tms.mapper.TaskMapper;
import com.technomancarai.tms.repository.ProjectMemberRepository;
import com.technomancarai.tms.repository.ProjectRepository;
import com.technomancarai.tms.repository.TaskPriorityRepository;
import com.technomancarai.tms.repository.TaskRepository;
import com.technomancarai.tms.repository.TaskStatusRepository;
import com.technomancarai.tms.repository.TaskTypeRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.service.ProjectManagerService;
import com.technomancarai.tms.service.ProjectSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectManagerServiceImpl implements ProjectManagerService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskPriorityRepository taskPriorityRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final ProjectSecurityService projectSecurityService;
    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final TaskMapper taskMapper;

    // 1. View Assigned Projects & Progress
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> getAssignedProjects(String pmEmail, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Project> projectsPage = projectRepository.findByManagerEmail(pmEmail, pageable);

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
    public ProjectResponse getProjectDetails(Long projectId, String pmEmail) {
        Project project = projectSecurityService.verifyProjectOwnership(projectId, pmEmail);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectProgressResponse getProjectProgress(Long projectId, String pmEmail) {
        Project project = projectSecurityService.verifyProjectOwnership(projectId, pmEmail);

        long totalTasks = taskRepository.countByProjectId(projectId);
        long completedTasks = taskRepository.countByProjectIdAndTaskStatusNameIgnoreCase(projectId, "Completed");
        long pendingTasks = totalTasks - completedTasks;

        double completionPercentage = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100.0 : 0.0;

        return ProjectProgressResponse.builder()
                .projectId(project.getId())
                .projectName(project.getProjectName())
                .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectStatsResponse getProjectStats(Long projectId, String pmEmail) {
        return getProjectOverview(projectId, pmEmail);
    }

    // 2. Manage Project Members
    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(Long projectId, String pmEmail) {
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        return members.stream()
                .map(projectMemberMapper::toProjectMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectMemberResponse addProjectMember(Long projectId, AddMemberRequest request, String pmEmail) {
        Project project = projectSecurityService.verifyProjectOwnership(projectId, pmEmail);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new DuplicateResourceException("User is already a member of this project");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(user)
                .joinedDate(LocalDate.now())
                .build();
        member.setIsActive(true);

        ProjectMember savedMember = projectMemberRepository.save(member);
        return projectMemberMapper.toProjectMemberResponse(savedMember);
    }

    @Override
    @Transactional
    public void removeProjectMember(Long projectId, Long userId, String pmEmail) {
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);

        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ResourceNotFoundException("Member relationship not found for user ID: " + userId + " in project ID: " + projectId);
        }

        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    // 3. Task Management
    @Override
    @Transactional
    public TaskResponse createTask(Long projectId, PmTaskRequest request, String pmEmail) {
        Project project = projectSecurityService.verifyProjectOwnership(projectId, pmEmail);

        User pmUser = userRepository.findByEmail(pmEmail)
                .orElseThrow(() -> new ResourceNotFoundException("PM user not found with email: " + pmEmail));

        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found with ID: " + request.getAssignedUserId()));

            if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, assignedUser.getId())) {
                throw new BadRequestException("Assigned user is not a member of project: " + project.getProjectName());
            }
        }

        Task task = taskMapper.toTask(request);
        task.setProject(project);
        task.setCreatedByUser(pmUser);
        task.setAssignedTo(assignedUser);
        task.setIsActive(true);

        if (request.getTaskStatusId() != null) {
            TaskStatus status = taskStatusRepository.findById(request.getTaskStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task status not found with ID: " + request.getTaskStatusId()));
            task.setTaskStatus(status);
        }

        if (request.getTaskPriorityId() != null) {
            TaskPriority priority = taskPriorityRepository.findById(request.getTaskPriorityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task priority not found with ID: " + request.getTaskPriorityId()));
            task.setTaskPriority(priority);
        }

        if (request.getTaskTypeId() != null) {
            TaskType type = taskTypeRepository.findById(request.getTaskTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task type not found with ID: " + request.getTaskTypeId()));
            task.setTaskType(type);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getProjectTasks(Long projectId, String titleSearch, int pageNo, int pageSize, String sortBy, String sortDir, String pmEmail) {
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Task> tasksPage;
        if (titleSearch != null && !titleSearch.isBlank()) {
            tasksPage = taskRepository.findByProjectIdAndTitleContainingIgnoreCase(projectId, titleSearch, pageable);
        } else {
            tasksPage = taskRepository.findByProjectId(projectId, pageable);
        }

        List<TaskResponse> content = tasksPage.getContent().stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());

        return PageResponse.<TaskResponse>builder()
                .content(content)
                .pageNo(tasksPage.getNumber())
                .pageSize(tasksPage.getSize())
                .totalElements(tasksPage.getTotalElements())
                .totalPages(tasksPage.getTotalPages())
                .isLast(tasksPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId, String pmEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        projectSecurityService.verifyProjectOwnership(task.getProject().getId(), pmEmail);
        return taskMapper.toTaskResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, PmTaskRequest request, String pmEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        Long projectId = task.getProject().getId();
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);

        taskMapper.updateTaskFromPmRequest(request, task);

        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found with ID: " + request.getAssignedUserId()));

            if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, assignedUser.getId())) {
                throw new BadRequestException("Assigned user is not a member of project: " + task.getProject().getProjectName());
            }
            task.setAssignedTo(assignedUser);
        }

        if (request.getTaskStatusId() != null) {
            TaskStatus status = taskStatusRepository.findById(request.getTaskStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task status not found with ID: " + request.getTaskStatusId()));
            task.setTaskStatus(status);
        }

        if (request.getTaskPriorityId() != null) {
            TaskPriority priority = taskPriorityRepository.findById(request.getTaskPriorityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task priority not found with ID: " + request.getTaskPriorityId()));
            task.setTaskPriority(priority);
        }

        if (request.getTaskTypeId() != null) {
            TaskType type = taskTypeRepository.findById(request.getTaskTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task type not found with ID: " + request.getTaskTypeId()));
            task.setTaskType(type);
        }

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, String pmEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        projectSecurityService.verifyProjectOwnership(task.getProject().getId(), pmEmail);
        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public TaskResponse assignTask(Long taskId, AssignTaskRequest request, String pmEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        Long projectId = task.getProject().getId();
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);

        User assignedUser = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getAssignedUserId()));

        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, assignedUser.getId())) {
            throw new BadRequestException("Assigned user is not a member of project: " + task.getProject().getProjectName());
        }

        task.setAssignedTo(assignedUser);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse changeTaskPriority(Long taskId, UpdateTaskPriorityPmRequest request, String pmEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        projectSecurityService.verifyProjectOwnership(task.getProject().getId(), pmEmail);

        TaskPriority priority = taskPriorityRepository.findById(request.getTaskPriorityId())
                .orElseThrow(() -> new ResourceNotFoundException("Task priority not found with ID: " + request.getTaskPriorityId()));

        task.setTaskPriority(priority);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse changeTaskStatus(Long taskId, UpdateTaskStatusPmRequest request, String pmEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        projectSecurityService.verifyProjectOwnership(task.getProject().getId(), pmEmail);

        TaskStatus status = taskStatusRepository.findById(request.getTaskStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with ID: " + request.getTaskStatusId()));

        task.setTaskStatus(status);
        if ("Completed".equalsIgnoreCase(status.getName())) {
            task.setCompletionPercentage(100);
        }

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse setTaskDueDate(Long taskId, SetDueDateRequest request, String pmEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        projectSecurityService.verifyProjectOwnership(task.getProject().getId(), pmEmail);

        task.setDueDate(request.getDueDate());
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(updatedTask);
    }

    // 4. Track Tasks
    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getPendingTasks(Long projectId, String pmEmail) {
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .filter(t -> t.getTaskStatus() == null || !"Completed".equalsIgnoreCase(t.getTaskStatus().getName()))
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getCompletedTasks(Long projectId, String pmEmail) {
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);
        List<Task> tasks = taskRepository.findByProjectIdAndTaskStatusNameIgnoreCase(projectId, "Completed");
        return tasks.stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getInProgressTasks(Long projectId, String pmEmail) {
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);
        List<Task> tasks = taskRepository.findByProjectIdAndTaskStatusNameIgnoreCase(projectId, "In Progress");
        return tasks.stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks(Long projectId, String pmEmail) {
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);
        List<Task> tasks = taskRepository.findByProjectIdAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(
                projectId, LocalDate.now(), "Completed"
        );
        return tasks.stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getHighPriorityTasks(Long projectId, String pmEmail) {
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);
        List<Task> highTasks = taskRepository.findByProjectIdAndTaskPriorityNameIgnoreCase(projectId, "High");
        List<Task> urgentTasks = taskRepository.findByProjectIdAndTaskPriorityNameIgnoreCase(projectId, "Urgent");

        List<Task> combined = new ArrayList<>(highTasks);
        combined.addAll(urgentTasks);

        return combined.stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectProgressResponse getTaskCompletionPercentage(Long projectId, String pmEmail) {
        return getProjectProgress(projectId, pmEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeTaskSummaryResponse> getEmployeeTaskSummary(Long projectId, String pmEmail) {
        projectSecurityService.verifyProjectOwnership(projectId, pmEmail);
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);

        List<EmployeeTaskSummaryResponse> summaries = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (ProjectMember member : members) {
            User user = member.getUser();
            long totalAssigned = taskRepository.countByProjectIdAndAssignedToId(projectId, user.getId());
            long completed = taskRepository.countByProjectIdAndAssignedToIdAndTaskStatusNameIgnoreCase(projectId, user.getId(), "Completed");
            long pending = totalAssigned - completed;
            long overdue = taskRepository.countByProjectIdAndAssignedToIdAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(
                    projectId, user.getId(), today, "Completed"
            );

            summaries.add(EmployeeTaskSummaryResponse.builder()
                    .userId(user.getId())
                    .userName((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""))
                    .userEmail(user.getEmail())
                    .totalAssignedTasks(totalAssigned)
                    .completedTasks(completed)
                    .pendingTasks(pending)
                    .overdueTasks(overdue)
                    .build());
        }

        return summaries;
    }

    // 5. Track Project
    @Override
    @Transactional(readOnly = true)
    public ProjectStatsResponse getProjectOverview(Long projectId, String pmEmail) {
        Project project = projectSecurityService.verifyProjectOwnership(projectId, pmEmail);

        long totalMembers = projectMemberRepository.findByProjectId(projectId).size();
        long totalTasks = taskRepository.countByProjectId(projectId);
        long completedTasks = taskRepository.countByProjectIdAndTaskStatusNameIgnoreCase(projectId, "Completed");
        long inProgressTasks = taskRepository.countByProjectIdAndTaskStatusNameIgnoreCase(projectId, "In Progress");
        long pendingTasks = totalTasks - completedTasks;
        long overdueTasks = taskRepository.countByProjectIdAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(
                projectId, LocalDate.now(), "Completed"
        );

        double completionPercentage = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100.0 : 0.0;

        List<TaskResponse> recentlyUpdated = taskRepository.findTop5ByProjectIdOrderByUpdatedAtDesc(projectId).stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());

        return ProjectStatsResponse.builder()
                .projectId(project.getId())
                .projectName(project.getProjectName())
                .totalMembers(totalMembers)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .inProgressTasks(inProgressTasks)
                .overdueTasks(overdueTasks)
                .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                .recentlyUpdatedTasks(recentlyUpdated)
                .build();
    }
}
