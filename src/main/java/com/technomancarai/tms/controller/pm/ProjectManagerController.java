package com.technomancarai.tms.controller.pm;

import com.technomancarai.tms.dto.request.AddMemberRequest;
import com.technomancarai.tms.dto.request.AssignTaskRequest;
import com.technomancarai.tms.dto.request.PmTaskRequest;
import com.technomancarai.tms.dto.request.SetDueDateRequest;
import com.technomancarai.tms.dto.request.UpdateTaskPriorityPmRequest;
import com.technomancarai.tms.dto.request.UpdateTaskStatusPmRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.EmployeeTaskSummaryResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectMemberResponse;
import com.technomancarai.tms.dto.response.ProjectProgressResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.dto.response.ProjectStatsResponse;
import com.technomancarai.tms.dto.response.TaskResponse;
import com.technomancarai.tms.service.ProjectManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pm")
@RequiredArgsConstructor
@Tag(name = "08. Project Manager Workspace", description = "Endpoints for Project Managers to manage assigned projects, project members, tasks, and progress statistics")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('PROJECT_MANAGER')")
public class ProjectManagerController {

    private final ProjectManagerService projectManagerService;

    // -------------------------------------------------------------------------
    // 1. View Assigned Projects & Progress
    // -------------------------------------------------------------------------

    @GetMapping("/projects")
    @Operation(summary = "01. View Assigned Projects", description = "Retrieves paginated list of projects assigned to the authenticated Project Manager.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Assigned projects retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<ProjectResponse>>> getAssignedProjects(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(hidden = true) Authentication authentication
    ) {
        PageResponse<ProjectResponse> response = projectManagerService.getAssignedProjects(
                authentication.getName(), pageNo, pageSize, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.success(response, "Assigned projects retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "02. View Project Details", description = "Fetches project details for an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project details retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectDetails(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        ProjectResponse response = projectManagerService.getProjectDetails(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Project details retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/progress")
    @Operation(summary = "03. View Project Progress", description = "Fetches project progress metrics and completion percentage.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project progress retrieved successfully")
    })
    public ResponseEntity<ApiResponse<ProjectProgressResponse>> getProjectProgress(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        ProjectProgressResponse response = projectManagerService.getProjectProgress(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Project progress retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/stats")
    @Operation(summary = "04. View Project Statistics", description = "Fetches project statistics including tasks, members, and status counts.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project statistics retrieved successfully")
    })
    public ResponseEntity<ApiResponse<ProjectStatsResponse>> getProjectStats(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        ProjectStatsResponse response = projectManagerService.getProjectStats(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Project statistics retrieved successfully"));
    }

    // -------------------------------------------------------------------------
    // 2. Manage Project Members
    // -------------------------------------------------------------------------

    @GetMapping("/projects/{projectId}/members")
    @Operation(summary = "05. View Project Members", description = "Lists all members belonging to an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project members retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<ProjectMemberResponse>>> getProjectMembers(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        List<ProjectMemberResponse> members = projectManagerService.getProjectMembers(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(members, "Project members retrieved successfully"));
    }

    @PostMapping("/projects/{projectId}/members")
    @Operation(summary = "06. Add Member to Project", description = "Adds an existing user as a member to an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Member added to project successfully")
    })
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> addProjectMember(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Valid @RequestBody AddMemberRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        ProjectMemberResponse response = projectManagerService.addProjectMember(projectId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Member added to project successfully"));
    }

    @DeleteMapping("/projects/{projectId}/members/{userId}")
    @Operation(summary = "07. Remove Member from Project", description = "Removes a user from an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Member removed from project successfully")
    })
    public ResponseEntity<ApiResponse<Void>> removeProjectMember(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(description = "User ID", example = "2") @PathVariable Long userId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        projectManagerService.removeProjectMember(projectId, userId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Member removed from project successfully"));
    }

    // -------------------------------------------------------------------------
    // 3. Task Management
    // -------------------------------------------------------------------------

    @PostMapping("/projects/{projectId}/tasks")
    @Operation(summary = "08. Create Task", description = "Creates a new task in an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Task created successfully")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Valid @RequestBody PmTaskRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskResponse response = projectManagerService.createTask(projectId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Task created successfully"));
    }

    @GetMapping("/projects/{projectId}/tasks")
    @Operation(summary = "09. View All Project Tasks", description = "Retrieves paginated tasks for an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> getProjectTasks(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Search query string", example = "Frontend") @RequestParam(required = false) String search,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(hidden = true) Authentication authentication
    ) {
        PageResponse<TaskResponse> response = projectManagerService.getProjectTasks(
                projectId, search, pageNo, pageSize, sortBy, sortDir, authentication.getName()
        );
        return ResponseEntity.ok(ApiResponse.success(response, "Project tasks retrieved successfully"));
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "10. View Task By ID", description = "Fetches task details by task ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task details retrieved successfully")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskResponse response = projectManagerService.getTaskById(taskId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Task details retrieved successfully"));
    }

    @PutMapping("/tasks/{taskId}")
    @Operation(summary = "11. Update Task", description = "Updates an existing task in an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task updated successfully")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Valid @RequestBody PmTaskRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskResponse response = projectManagerService.updateTask(taskId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Task updated successfully"));
    }

    @DeleteMapping("/tasks/{taskId}")
    @Operation(summary = "12. Delete Task", description = "Deletes a task by ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task deleted successfully")
    })
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        projectManagerService.deleteTask(taskId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully"));
    }

    @PutMapping("/tasks/{taskId}/assign")
    @Operation(summary = "13. Assign / Reassign Task", description = "Assigns a task to a project member.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task assigned successfully")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskResponse response = projectManagerService.assignTask(taskId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Task assigned successfully"));
    }

    @PutMapping("/tasks/{taskId}/priority")
    @Operation(summary = "14. Change Task Priority", description = "Updates task priority level.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task priority updated successfully")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> changeTaskPriority(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskPriorityPmRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskResponse response = projectManagerService.changeTaskPriority(taskId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Task priority updated successfully"));
    }

    @PutMapping("/tasks/{taskId}/status")
    @Operation(summary = "15. Change Task Status", description = "Updates task status.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task status updated successfully")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> changeTaskStatus(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusPmRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskResponse response = projectManagerService.changeTaskStatus(taskId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Task status updated successfully"));
    }

    @PutMapping("/tasks/{taskId}/due-date")
    @Operation(summary = "16. Set Task Due Date", description = "Updates task due date.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task due date updated successfully")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> setTaskDueDate(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Valid @RequestBody SetDueDateRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskResponse response = projectManagerService.setTaskDueDate(taskId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Task due date updated successfully"));
    }

    // -------------------------------------------------------------------------
    // 4. Track Tasks
    // -------------------------------------------------------------------------

    @GetMapping("/projects/{projectId}/tasks/pending")
    @Operation(summary = "17. Get Pending Tasks", description = "Fetches all incomplete/pending tasks for an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pending tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getPendingTasks(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        List<TaskResponse> tasks = projectManagerService.getPendingTasks(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tasks, "Pending tasks retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/tasks/completed")
    @Operation(summary = "18. Get Completed Tasks", description = "Fetches all completed tasks for an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Completed tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getCompletedTasks(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        List<TaskResponse> tasks = projectManagerService.getCompletedTasks(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tasks, "Completed tasks retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/tasks/in-progress")
    @Operation(summary = "19. Get In Progress Tasks", description = "Fetches all in-progress tasks for an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "In-progress tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getInProgressTasks(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        List<TaskResponse> tasks = projectManagerService.getInProgressTasks(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tasks, "In-progress tasks retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/tasks/overdue")
    @Operation(summary = "20. Get Overdue Tasks", description = "Fetches all overdue tasks for an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Overdue tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        List<TaskResponse> tasks = projectManagerService.getOverdueTasks(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tasks, "Overdue tasks retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/tasks/high-priority")
    @Operation(summary = "21. Get High Priority Tasks", description = "Fetches high & urgent priority tasks for an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "High priority tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getHighPriorityTasks(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        List<TaskResponse> tasks = projectManagerService.getHighPriorityTasks(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tasks, "High priority tasks retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/tasks/completion-percentage")
    @Operation(summary = "22. Task Completion Percentage", description = "Fetches overall completion percentage for an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task completion percentage retrieved successfully")
    })
    public ResponseEntity<ApiResponse<ProjectProgressResponse>> getTaskCompletionPercentage(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        ProjectProgressResponse response = projectManagerService.getTaskCompletionPercentage(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Task completion percentage retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/employee-summary")
    @Operation(summary = "23. Employee Task Summary", description = "Fetches summary of tasks assigned to each project member.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee task summary retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<EmployeeTaskSummaryResponse>>> getEmployeeTaskSummary(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        List<EmployeeTaskSummaryResponse> summaries = projectManagerService.getEmployeeTaskSummary(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(summaries, "Employee task summary retrieved successfully"));
    }

    // -------------------------------------------------------------------------
    // 5. Track Project
    // -------------------------------------------------------------------------

    @GetMapping("/projects/{projectId}/overview")
    @Operation(summary = "24. Track Project Overview", description = "Fetches comprehensive metrics, progress, member/task counts, and recently updated tasks.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project overview retrieved successfully")
    })
    public ResponseEntity<ApiResponse<ProjectStatsResponse>> getProjectOverview(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        ProjectStatsResponse response = projectManagerService.getProjectOverview(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Project overview retrieved successfully"));
    }
}
