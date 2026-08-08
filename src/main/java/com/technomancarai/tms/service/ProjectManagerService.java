package com.technomancarai.tms.service;

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

import java.util.List;

public interface ProjectManagerService {

    // 1. View Assigned Projects & Progress
    PageResponse<ProjectResponse> getAssignedProjects(String pmEmail, int pageNo, int pageSize, String sortBy, String sortDir);

    ProjectResponse getProjectDetails(Long projectId, String pmEmail);

    ProjectProgressResponse getProjectProgress(Long projectId, String pmEmail);

    ProjectStatsResponse getProjectStats(Long projectId, String pmEmail);

    // 2. Manage Project Members
    List<ProjectMemberResponse> getProjectMembers(Long projectId, String pmEmail);

    ProjectMemberResponse addProjectMember(Long projectId, AddMemberRequest request, String pmEmail);

    void removeProjectMember(Long projectId, Long userId, String pmEmail);

    // 3. Task Management
    TaskResponse createTask(Long projectId, PmTaskRequest request, String pmEmail);

    PageResponse<TaskResponse> getProjectTasks(Long projectId, String titleSearch, int pageNo, int pageSize, String sortBy, String sortDir, String pmEmail);

    TaskResponse getTaskById(Long taskId, String pmEmail);

    TaskResponse updateTask(Long taskId, PmTaskRequest request, String pmEmail);

    void deleteTask(Long taskId, String pmEmail);

    TaskResponse assignTask(Long taskId, AssignTaskRequest request, String pmEmail);

    TaskResponse changeTaskPriority(Long taskId, UpdateTaskPriorityPmRequest request, String pmEmail);

    TaskResponse changeTaskStatus(Long taskId, UpdateTaskStatusPmRequest request, String pmEmail);

    TaskResponse setTaskDueDate(Long taskId, SetDueDateRequest request, String pmEmail);

    // 4. Track Tasks
    List<TaskResponse> getPendingTasks(Long projectId, String pmEmail);

    List<TaskResponse> getCompletedTasks(Long projectId, String pmEmail);

    List<TaskResponse> getInProgressTasks(Long projectId, String pmEmail);

    List<TaskResponse> getOverdueTasks(Long projectId, String pmEmail);

    List<TaskResponse> getHighPriorityTasks(Long projectId, String pmEmail);

    ProjectProgressResponse getTaskCompletionPercentage(Long projectId, String pmEmail);

    List<EmployeeTaskSummaryResponse> getEmployeeTaskSummary(Long projectId, String pmEmail);

    // 5. Track Project
    ProjectStatsResponse getProjectOverview(Long projectId, String pmEmail);
}
