package com.technomancarai.tms.service;

import com.technomancarai.tms.entity.Project;
import com.technomancarai.tms.entity.Task;
import com.technomancarai.tms.entity.TaskComment;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.exception.ForbiddenException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.repository.ProjectMemberRepository;
import com.technomancarai.tms.repository.ProjectRepository;
import com.technomancarai.tms.repository.TaskCommentRepository;
import com.technomancarai.tms.repository.TaskRepository;
import com.technomancarai.tms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeSecurityService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Project verifyProjectMembership(Long projectId, String employeeEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        User user = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + employeeEmail));

        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new ForbiddenException("Access denied: You are not a member of project: " + project.getProjectName());
        }

        return project;
    }

    @Transactional(readOnly = true)
    public Task verifyTaskAssignment(Long taskId, String employeeEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        if (task.getAssignedTo() == null || !task.getAssignedTo().getEmail().equalsIgnoreCase(employeeEmail)) {
            throw new ForbiddenException("Access denied: You are not assigned to task: " + task.getTitle());
        }

        return task;
    }

    @Transactional(readOnly = true)
    public TaskComment verifyCommentOwnership(Long commentId, String employeeEmail) {
        TaskComment comment = taskCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Task comment not found with ID: " + commentId));

        if (comment.getUser() == null || !comment.getUser().getEmail().equalsIgnoreCase(employeeEmail)) {
            throw new ForbiddenException("Access denied: You can only edit or delete your own comments");
        }

        return comment;
    }
}
