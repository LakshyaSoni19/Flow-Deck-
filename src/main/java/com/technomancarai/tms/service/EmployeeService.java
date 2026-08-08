package com.technomancarai.tms.service;

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

import java.util.List;

public interface EmployeeService {

    // 1. View Assigned Projects
    PageResponse<ProjectResponse> getAssignedProjects(String employeeEmail, int pageNo, int pageSize, String sortBy, String sortDir);

    ProjectResponse getProjectDetails(Long projectId, String employeeEmail);

    ProjectProgressResponse getProjectProgress(Long projectId, String employeeEmail);

    List<ProjectMemberResponse> getProjectMembers(Long projectId, String employeeEmail);

    // 2. View Assigned Tasks
    PageResponse<TaskResponse> getAssignedTasks(String employeeEmail, String searchTitle, String statusFilter, String priorityFilter, int pageNo, int pageSize, String sortBy, String sortDir);

    TaskResponse getTaskById(Long taskId, String employeeEmail);

    List<TaskResponse> getPendingTasks(String employeeEmail);

    List<TaskResponse> getCompletedTasks(String employeeEmail);

    List<TaskResponse> getOverdueTasks(String employeeEmail);

    // 3. Task Comments
    List<TaskCommentResponse> getTaskComments(Long taskId, String employeeEmail);

    TaskCommentResponse addTaskComment(Long taskId, TaskCommentRequest request, String employeeEmail);

    TaskCommentResponse editTaskComment(Long commentId, TaskCommentRequest request, String employeeEmail);

    void deleteTaskComment(Long commentId, String employeeEmail);

    // 4. Task Status
    TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusEmployeeRequest request, String employeeEmail);

    // 5. My Dashboard
    EmployeeDashboardResponse getEmployeeDashboard(String employeeEmail);

    // 6. My Profile
    UserResponse getProfile(String employeeEmail);

    UserResponse updateProfile(String employeeEmail, UpdateProfileRequest request);

    void changePassword(String employeeEmail, ChangePasswordRequest request);
}
