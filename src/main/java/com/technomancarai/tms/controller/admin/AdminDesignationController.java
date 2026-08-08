package com.technomancarai.tms.controller.admin;

import com.technomancarai.tms.dto.request.DesignationRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.DesignationResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.service.DesignationService;
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
@RequestMapping("/api/v1/admin/designations")
@RequiredArgsConstructor
@Tag(name = "06. Designation Management", description = "Endpoints for employee designation and job title management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDesignationController {

    private final DesignationService designationService;

    @PostMapping
    @Operation(summary = "01. Create Designation", description = "Creates a new designation.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Designation created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate designation name"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<DesignationResponse>> createDesignation(
            @Valid @RequestBody DesignationRequest request
    ) {
        DesignationResponse response = designationService.createDesignation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Designation created successfully"));
    }

    @GetMapping
    @Operation(summary = "02. View All Designations", description = "Retrieve paginated and sorted list of all designations.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Designations retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<PageResponse<DesignationResponse>>> getAllDesignations(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<DesignationResponse> response = designationService.getAllDesignations(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Designations retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "03. Search Designations", description = "Search designations by name with pagination and sorting.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Designation search completed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<PageResponse<DesignationResponse>>> searchDesignations(
            @Parameter(description = "Search query string (designation name)", example = "Software Engineer") @RequestParam String query,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<DesignationResponse> response = designationService.searchDesignations(query, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Designation search completed successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "04. View Designation by ID", description = "Fetch designation details by ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Designation details retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Designation not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<DesignationResponse>> getDesignationById(
            @Parameter(description = "Target designation ID", example = "1") @PathVariable Long id
    ) {
        DesignationResponse response = designationService.getDesignationById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Designation details retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "05. Update Designation", description = "Updates an existing designation by ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Designation updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Designation not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate designation name"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<DesignationResponse>> updateDesignation(
            @Parameter(description = "Target designation ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody DesignationRequest request
    ) {
        DesignationResponse response = designationService.updateDesignation(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Designation updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "06. Delete Designation", description = "Deletes a designation by ID if not assigned to any user.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Designation deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete designation assigned to users"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Designation not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<Void>> deleteDesignation(
            @Parameter(description = "Target designation ID", example = "1") @PathVariable Long id
    ) {
        designationService.deleteDesignation(id);
        return ResponseEntity.ok(ApiResponse.success("Designation deleted successfully"));
    }
}
