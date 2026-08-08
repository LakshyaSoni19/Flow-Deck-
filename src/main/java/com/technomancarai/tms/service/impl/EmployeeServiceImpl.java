package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.dto.request.ChangePasswordRequest;
import com.technomancarai.tms.dto.request.TaskCommentRequest;
import com.technomancarai.tms.dto.request.UpdateProfileRequest;
import com.technomancarai.tms.dto.request.UpdateTaskStatusEmployeeRequest;
import com.technomancarai.tms.dto.response.EmployeeDashboardResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectMemberResponse;
import com.technomancarai.tms.dto.response.ProjectProgressResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.dto.response.TaskCommentResponse;
import com.technomancarai.tms.dto.response.TaskResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.entity.Project;
import com.technomancarai.tms.entity.ProjectMember;
import com.technomancarai.tms.entity.Task;
import com.technomancarai.tms.entity.TaskComment;
import com.technomancarai.tms.entity.TaskStatus;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.entity.UserRole;
import com.technomancarai.tms.exception.BadRequestException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.mapper.ProjectMapper;
import com.technomancarai.tms.mapper.ProjectMemberMapper;
import com.technomancarai.tms.mapper.TaskCommentMapper;
import com.technomancarai.tms.mapper.TaskMapper;
import com.technomancarai.tms.mapper.UserMapper;
import com.technomancarai.tms.repository.ProjectMemberRepository;
import com.technomancarai.tms.repository.TaskCommentRepository;
import com.technomancarai.tms.repository.TaskRepository;
import com.technomancarai.tms.repository.TaskStatusRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.repository.UserRoleRepository;
import com.technomancarai.tms.service.EmployeeSecurityService;
import com.technomancarai.tms.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmployeeSecurityService employeeSecurityService;

    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final TaskMapper taskMapper;
    private final TaskCommentMapper taskCommentMapper;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    // 1. View Assigned Projects
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> getAssignedProjects(String employeeEmail, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by("project." + sortBy).ascending()
                : Sort.by("project." + sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<ProjectMember> memberPage = projectMemberRepository.findByUserEmail(employeeEmail, pageable);

        List<ProjectResponse> content = memberPage.getContent().stream()
                .map(pm -> projectMapper.toProjectResponse(pm.getProject()))
                .collect(Collectors.toList());

        return PageResponse.<ProjectResponse>builder()
                .content(content)
                .pageNo(memberPage.getNumber())
                .pageSize(memberPage.getSize())
                .totalElements(memberPage.getTotalElements())
                .totalPages(memberPage.getTotalPages())
                .isLast(memberPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectDetails(Long projectId, String employeeEmail) {
        Project project = employeeSecurityService.verifyProjectMembership(projectId, employeeEmail);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectProgressResponse getProjectProgress(Long projectId, String employeeEmail) {
        Project project = employeeSecurityService.verifyProjectMembership(projectId, employeeEmail);

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
    public List<ProjectMemberResponse> getProjectMembers(Long projectId, String employeeEmail) {
        employeeSecurityService.verifyProjectMembership(projectId, employeeEmail);
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        return members.stream()
                .map(projectMemberMapper::toProjectMemberResponse)
                .collect(Collectors.toList());
    }

    // 2. View Assigned Tasks
    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getAssignedTasks(String employeeEmail, String searchTitle, String statusFilter, String priorityFilter, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Task> tasksPage;
        if (searchTitle != null && !searchTitle.isBlank()) {
            tasksPage = taskRepository.findByAssignedToEmailAndTitleContainingIgnoreCase(employeeEmail, searchTitle, pageable);
        } else {
            tasksPage = taskRepository.findByAssignedToEmail(employeeEmail, pageable);
        }

        List<TaskResponse> content = tasksPage.getContent().stream()
                .filter(t -> {
                    if (statusFilter != null && !statusFilter.isBlank()) {
                        if (t.getTaskStatus() == null || !t.getTaskStatus().getName().equalsIgnoreCase(normalizeStatusName(statusFilter))) {
                            return false;
                        }
                    }
                    if (priorityFilter != null && !priorityFilter.isBlank()) {
                        if (t.getTaskPriority() == null || !t.getTaskPriority().getName().equalsIgnoreCase(priorityFilter)) {
                            return false;
                        }
                    }
                    return true;
                })
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
    public TaskResponse getTaskById(Long taskId, String employeeEmail) {
        Task task = employeeSecurityService.verifyTaskAssignment(taskId, employeeEmail);
        return taskMapper.toTaskResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getPendingTasks(String employeeEmail) {
        List<Task> tasks = taskRepository.findByAssignedToEmail(employeeEmail, Pageable.unpaged()).getContent();
        return tasks.stream()
                .filter(t -> t.getTaskStatus() == null || !"Completed".equalsIgnoreCase(t.getTaskStatus().getName()))
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getCompletedTasks(String employeeEmail) {
        List<Task> tasks = taskRepository.findByAssignedToEmailAndTaskStatusNameIgnoreCase(employeeEmail, "Completed");
        return tasks.stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks(String employeeEmail) {
        List<Task> tasks = taskRepository.findByAssignedToEmailAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(
                employeeEmail, LocalDate.now(), "Completed"
        );
        return tasks.stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    // 3. Task Comments
    @Override
    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getTaskComments(Long taskId, String employeeEmail) {
        employeeSecurityService.verifyTaskAssignment(taskId, employeeEmail);
        List<TaskComment> comments = taskCommentRepository.findByTaskIdOrderByCommentDateDesc(taskId);
        return comments.stream()
                .map(taskCommentMapper::toTaskCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskCommentResponse addTaskComment(Long taskId, TaskCommentRequest request, String employeeEmail) {
        Task task = employeeSecurityService.verifyTaskAssignment(taskId, employeeEmail);

        User user = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + employeeEmail));

        TaskComment comment = TaskComment.builder()
                .comment(request.getComment())
                .commentDate(LocalDateTime.now())
                .task(task)
                .user(user)
                .build();
        comment.setIsActive(true);

        TaskComment savedComment = taskCommentRepository.save(comment);
        return taskCommentMapper.toTaskCommentResponse(savedComment);
    }

    @Override
    @Transactional
    public TaskCommentResponse editTaskComment(Long commentId, TaskCommentRequest request, String employeeEmail) {
        TaskComment comment = employeeSecurityService.verifyCommentOwnership(commentId, employeeEmail);
        comment.setComment(request.getComment());
        comment.setCommentDate(LocalDateTime.now());

        TaskComment updatedComment = taskCommentRepository.save(comment);
        return taskCommentMapper.toTaskCommentResponse(updatedComment);
    }

    @Override
    @Transactional
    public void deleteTaskComment(Long commentId, String employeeEmail) {
        TaskComment comment = employeeSecurityService.verifyCommentOwnership(commentId, employeeEmail);
        taskCommentRepository.delete(comment);
    }

    // 4. Task Status Update (Restricted to TO_DO, IN_PROGRESS, COMPLETED)
    @Override
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusEmployeeRequest request, String employeeEmail) {
        Task task = employeeSecurityService.verifyTaskAssignment(taskId, employeeEmail);

        String normalizedStatus = normalizeStatusName(request.getStatusName());
        if (!"To Do".equalsIgnoreCase(normalizedStatus)
                && !"In Progress".equalsIgnoreCase(normalizedStatus)
                && !"Completed".equalsIgnoreCase(normalizedStatus)) {
            throw new BadRequestException("Employees can only update task status to TO_DO, IN_PROGRESS, or COMPLETED");
        }

        TaskStatus taskStatus = taskStatusRepository.findByName(normalizedStatus)
                .orElseGet(() -> taskStatusRepository.save(TaskStatus.builder().name(normalizedStatus).build()));

        task.setTaskStatus(taskStatus);
        if ("Completed".equalsIgnoreCase(normalizedStatus)) {
            task.setCompletionPercentage(100);
        }

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskResponse(updatedTask);
    }

    // 5. My Dashboard
    @Override
    @Transactional(readOnly = true)
    public EmployeeDashboardResponse getEmployeeDashboard(String employeeEmail) {
        long totalAssigned = taskRepository.countByAssignedToEmail(employeeEmail);
        long completed = taskRepository.countByAssignedToEmailAndTaskStatusNameIgnoreCase(employeeEmail, "Completed");
        long pending = totalAssigned - completed;
        long overdue = taskRepository.countByAssignedToEmailAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(
                employeeEmail, LocalDate.now(), "Completed"
        );

        double completionPercentage = totalAssigned > 0 ? ((double) completed / totalAssigned) * 100.0 : 0.0;

        List<TaskResponse> upcomingDeadlines = taskRepository
                .findTop5ByAssignedToEmailAndDueDateAfterOrderByDueDateAsc(employeeEmail, LocalDate.now().minusDays(1))
                .stream()
                .filter(t -> t.getTaskStatus() == null || !"Completed".equalsIgnoreCase(t.getTaskStatus().getName()))
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());

        return EmployeeDashboardResponse.builder()
                .totalAssignedTasks(totalAssigned)
                .completedTasks(completed)
                .pendingTasks(pending)
                .overdueTasks(overdue)
                .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                .upcomingDeadlines(upcomingDeadlines)
                .build();
    }

    // 6. My Profile
    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(String employeeEmail) {
        User user = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + employeeEmail));

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<String> roles = userRoles.stream().map(ur -> ur.getRole().getName()).collect(Collectors.toList());

        return userMapper.toUserResponseWithRoles(user, roles);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String employeeEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + employeeEmail));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getMobile() != null) user.setMobile(request.getMobile());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDob() != null) user.setDob(request.getDob());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());

        User updatedUser = userRepository.save(user);

        List<UserRole> userRoles = userRoleRepository.findByUserId(updatedUser.getId());
        List<String> roles = userRoles.stream().map(ur -> ur.getRole().getName()).collect(Collectors.toList());

        return userMapper.toUserResponseWithRoles(updatedUser, roles);
    }

    @Override
    @Transactional
    public void changePassword(String employeeEmail, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + employeeEmail));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private String normalizeStatusName(String raw) {
        if (raw == null) return null;
        switch (raw.trim().toUpperCase()) {
            case "TO_DO":
            case "TO DO":
                return "To Do";
            case "IN_PROGRESS":
            case "IN PROGRESS":
                return "In Progress";
            case "COMPLETED":
                return "Completed";
            default:
                return raw;
        }
    }
}
