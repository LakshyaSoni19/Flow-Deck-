package com.technomancarai.tms.controller.admin;

import com.technomancarai.tms.dto.request.AssignProjectManagerRequest;
import com.technomancarai.tms.dto.request.ProjectRequest;
import com.technomancarai.tms.dto.request.UpdateProjectStatusRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.service.AdminProjectService;
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

@RestController
@RequestMapping("/api/v1/admin/projects")
@RequiredArgsConstructor
@Tag(name = "07. Project Management", description = "Endpoints for managing projects, status updates, and manager assignment")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProjectController {

    private final AdminProjectService adminProjectService;

    @PostMapping
    @Operation(summary = "01. Create Project", description = "Creates a new project in the system.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Project created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody ProjectRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        String adminEmail = authentication != null ? authentication.getName() : null;
        ProjectResponse response = adminProjectService.createProject(request, adminEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Project created successfully"));
    }

    @GetMapping
    @Operation(summary = "02. View All Projects", description = "Retrieves paginated list of all projects.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<ProjectResponse>>> getAllProjects(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<ProjectResponse> pageResponse = adminProjectService.getAllProjects(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Projects retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "03. View Project By ID", description = "Fetch project details by project ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project details retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long id
    ) {
        ProjectResponse response = adminProjectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Project details retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "04. Update Project", description = "Updates an existing project by ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request
    ) {
        ProjectResponse response = adminProjectService.updateProject(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Project updated successfully"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "05. Change Project Status", description = "Updates project status (e.g. IN_PROGRESS, COMPLETED).")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project status updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> changeProjectStatus(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateProjectStatusRequest request
    ) {
        ProjectResponse response = adminProjectService.changeProjectStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Project status updated successfully"));
    }

    @PutMapping("/{id}/assign-manager")
    @Operation(summary = "06. Assign Project Manager", description = "Assigns a Project Manager to a project.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project manager assigned successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project or User not found")
    })
    public ResponseEntity<ApiResponse<ProjectResponse>> assignProjectManager(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody AssignProjectManagerRequest request
    ) {
        ProjectResponse response = adminProjectService.assignProjectManager(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Project manager assigned successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "07. Delete Project", description = "Deletes a project by ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @Parameter(description = "Project ID", example = "1") @PathVariable Long id
    ) {
        adminProjectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully"));
    }
}
