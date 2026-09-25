package com.technomancarai.tms.controller;

import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.LookupResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.service.LookupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
@Tag(name = "11. Master Data & Lookups", description = "Endpoints for retrieving dropdown lookup lists (Departments, Designations, Cities, Task Statuses, Task Priorities, Task Types, Users)")
public class LookupController {

    private final LookupService lookupService;

    @GetMapping("/departments")
    @Operation(summary = "01. Get Departments Lookup", description = "Retrieves a simple list of all active departments for selection dropdowns.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Departments lookup retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<LookupResponse>>> getDepartments() {
        List<LookupResponse> responses = lookupService.getDepartments();
        return ResponseEntity.ok(ApiResponse.success(responses, "Departments lookup retrieved successfully"));
    }

    @GetMapping("/designations")
    @Operation(summary = "02. Get Designations Lookup", description = "Retrieves a simple list of all active designations for selection dropdowns.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Designations lookup retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<LookupResponse>>> getDesignations() {
        List<LookupResponse> responses = lookupService.getDesignations();
        return ResponseEntity.ok(ApiResponse.success(responses, "Designations lookup retrieved successfully"));
    }

    @GetMapping("/cities")
    @Operation(summary = "03. Get Cities Lookup", description = "Retrieves a simple list of all active cities for selection dropdowns.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cities lookup retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<LookupResponse>>> getCities() {
        List<LookupResponse> responses = lookupService.getCities();
        return ResponseEntity.ok(ApiResponse.success(responses, "Cities lookup retrieved successfully"));
    }

    @GetMapping("/task-statuses")
    @Operation(summary = "04. Get Task Statuses Lookup", description = "Retrieves all task statuses with IDs and names for dropdown selectors.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task statuses lookup retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<LookupResponse>>> getTaskStatuses() {
        List<LookupResponse> responses = lookupService.getTaskStatuses();
        return ResponseEntity.ok(ApiResponse.success(responses, "Task statuses lookup retrieved successfully"));
    }

    @GetMapping("/task-priorities")
    @Operation(summary = "05. Get Task Priorities Lookup", description = "Retrieves all task priorities with IDs and names for dropdown selectors.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task priorities lookup retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<LookupResponse>>> getTaskPriorities() {
        List<LookupResponse> responses = lookupService.getTaskPriorities();
        return ResponseEntity.ok(ApiResponse.success(responses, "Task priorities lookup retrieved successfully"));
    }

    @GetMapping("/task-types")
    @Operation(summary = "06. Get Task Types Lookup", description = "Retrieves all task types with IDs and names for dropdown selectors.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task types lookup retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<LookupResponse>>> getTaskTypes() {
        List<LookupResponse> responses = lookupService.getTaskTypes();
        return ResponseEntity.ok(ApiResponse.success(responses, "Task types lookup retrieved successfully"));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @Operation(summary = "07. Search / List Users for Selection", description = "Retrieves active users with ID, name, email, department, designation, and roles for member/manager assignment dropdowns.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users lookup retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin or PM role required")
    })
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersLookup(
            @Parameter(description = "Optional search string (name or email)", example = "Lakshya")
            @RequestParam(required = false) String query
    ) {
        List<UserResponse> responses = lookupService.searchUsersForLookup(query);
        return ResponseEntity.ok(ApiResponse.success(responses, "Users lookup retrieved successfully"));
    }
}
