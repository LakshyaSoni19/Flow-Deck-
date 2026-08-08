package com.technomancarai.tms.controller.employee;

import com.technomancarai.tms.dto.request.ChangePasswordRequest;
import com.technomancarai.tms.dto.request.TaskCommentRequest;
import com.technomancarai.tms.dto.request.UpdateProfileRequest;
import com.technomancarai.tms.dto.request.UpdateTaskStatusEmployeeRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.EmployeeDashboardResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectMemberResponse;
import com.technomancarai.tms.dto.response.ProjectProgressResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.dto.response.TaskCommentResponse;
import com.technomancarai.tms.dto.response.TaskResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.service.EmployeeService;
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
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
@Tag(name = "09. Employee Workspace", description = "Endpoints for Employees to view assigned projects/tasks, manage comments, update task status, view personal dashboard, and manage profile")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {

    private final EmployeeService employeeService;

    // -------------------------------------------------------------------------
    // 1. View Assigned Projects
    // -------------------------------------------------------------------------

    @GetMapping("/projects")
    @Operation(summary = "01. View Assigned Projects", description = "Retrieves paginated list of projects where the employee is a member.")
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
        PageResponse<ProjectResponse> response = employeeService.getAssignedProjects(
                authentication.getName(), pageNo, pageSize, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.success(response, "Assigned projects retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "02. View Project Details", description = "Fetches details of an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project details retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectDetails(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        ProjectResponse response = employeeService.getProjectDetails(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Project details retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/progress")
    @Operation(summary = "03. View Project Progress", description = "Fetches progress of an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project progress retrieved successfully")
    })
    public ResponseEntity<ApiResponse<ProjectProgressResponse>> getProjectProgress(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        ProjectProgressResponse response = employeeService.getProjectProgress(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Project progress retrieved successfully"));
    }

    @GetMapping("/projects/{projectId}/members")
    @Operation(summary = "04. View Project Members", description = "Lists members of an assigned project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project members retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<ProjectMemberResponse>>> getProjectMembers(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long projectId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        List<ProjectMemberResponse> members = employeeService.getProjectMembers(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(members, "Project members retrieved successfully"));
    }

    // -------------------------------------------------------------------------
    // 2. View Assigned Tasks & Task Status Update
    // -------------------------------------------------------------------------

    @GetMapping("/tasks")
    @Operation(summary = "05. View Assigned Tasks", description = "Retrieves paginated tasks assigned to the employee with optional search and filters.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Assigned tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> getAssignedTasks(
            @Parameter(description = "Search query string", example = "Bug fix") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by status", example = "IN_PROGRESS") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by priority", example = "HIGH") @RequestParam(required = false) String priority,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(hidden = true) Authentication authentication
    ) {
        PageResponse<TaskResponse> response = employeeService.getAssignedTasks(
                authentication.getName(), search, status, priority, pageNo, pageSize, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.success(response, "Assigned tasks retrieved successfully"));
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "06. View Task By ID", description = "Fetches details of an assigned task.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task details retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskResponse response = employeeService.getTaskById(taskId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Task details retrieved successfully"));
    }

    @GetMapping("/tasks/pending")
    @Operation(summary = "07. View Pending Tasks", description = "Fetches pending tasks assigned to the employee.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pending tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getPendingTasks(@Parameter(hidden = true) Authentication authentication) {
        List<TaskResponse> tasks = employeeService.getPendingTasks(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tasks, "Pending tasks retrieved successfully"));
    }

    @GetMapping("/tasks/completed")
    @Operation(summary = "08. View Completed Tasks", description = "Fetches completed tasks assigned to the employee.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Completed tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getCompletedTasks(@Parameter(hidden = true) Authentication authentication) {
        List<TaskResponse> tasks = employeeService.getCompletedTasks(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tasks, "Completed tasks retrieved successfully"));
    }

    @GetMapping("/tasks/overdue")
    @Operation(summary = "09. View Overdue Tasks", description = "Fetches overdue tasks assigned to the employee.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Overdue tasks retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks(@Parameter(hidden = true) Authentication authentication) {
        List<TaskResponse> tasks = employeeService.getOverdueTasks(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tasks, "Overdue tasks retrieved successfully"));
    }

    @PutMapping("/tasks/{taskId}/status")
    @Operation(summary = "10. Update Task Status", description = "Updates status of assigned task (restricted to TO_DO, IN_PROGRESS, COMPLETED).")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task status updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusEmployeeRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskResponse response = employeeService.updateTaskStatus(taskId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Task status updated successfully"));
    }

    // -------------------------------------------------------------------------
    // 3. Task Comments
    // -------------------------------------------------------------------------

    @GetMapping("/tasks/{taskId}/comments")
    @Operation(summary = "11. View Task Comments", description = "Retrieves all comments for an assigned task.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task comments retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TaskCommentResponse>>> getTaskComments(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        List<TaskCommentResponse> comments = employeeService.getTaskComments(taskId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(comments, "Task comments retrieved successfully"));
    }

    @PostMapping("/tasks/{taskId}/comments")
    @Operation(summary = "12. Add Task Comment", description = "Adds a comment to an assigned task.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Comment added successfully")
    })
    public ResponseEntity<ApiResponse<TaskCommentResponse>> addTaskComment(
            @Parameter(description = "Task ID", example = "1") @PathVariable Long taskId,
            @Valid @RequestBody TaskCommentRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskCommentResponse response = employeeService.addTaskComment(taskId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Comment added successfully"));
    }

    @PutMapping("/comments/{commentId}")
    @Operation(summary = "13. Edit Own Comment", description = "Edits a comment authored by the employee.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Comment updated successfully")
    })
    public ResponseEntity<ApiResponse<TaskCommentResponse>> editTaskComment(
            @Parameter(description = "Comment ID", example = "1") @PathVariable Long commentId,
            @Valid @RequestBody TaskCommentRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        TaskCommentResponse response = employeeService.editTaskComment(commentId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Comment updated successfully"));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "14. Delete Own Comment", description = "Deletes a comment authored by the employee.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Comment deleted successfully")
    })
    public ResponseEntity<ApiResponse<Void>> deleteTaskComment(
            @Parameter(description = "Comment ID", example = "1") @PathVariable Long commentId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        employeeService.deleteTaskComment(commentId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully"));
    }

    // -------------------------------------------------------------------------
    // 4. My Dashboard
    // -------------------------------------------------------------------------

    @GetMapping("/dashboard")
    @Operation(
        summary = "01. Employee Dashboard",
        description = "Fetches personal task counts, completion %, and upcoming deadlines.",
        tags = {"10. Dashboard", "09. Employee Workspace"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee dashboard metrics retrieved successfully")
    })
    public ResponseEntity<ApiResponse<EmployeeDashboardResponse>> getEmployeeDashboard(@Parameter(hidden = true) Authentication authentication) {
        EmployeeDashboardResponse response = employeeService.getEmployeeDashboard(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Employee dashboard metrics retrieved successfully"));
    }

    // -------------------------------------------------------------------------
    // 5. My Profile
    // -------------------------------------------------------------------------

    @GetMapping("/profile")
    @Operation(summary = "15. View Own Profile", description = "Fetches profile details of the authenticated employee.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@Parameter(hidden = true) Authentication authentication) {
        UserResponse response = employeeService.getProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Profile retrieved successfully"));
    }

    @PutMapping("/profile")
    @Operation(summary = "16. Update Own Profile", description = "Updates profile details of the authenticated employee.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        UserResponse response = employeeService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile updated successfully"));
    }

    @PutMapping("/profile/change-password")
    @Operation(summary = "17. Change Password", description = "Changes password for the authenticated employee.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid current password")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        employeeService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }
}
