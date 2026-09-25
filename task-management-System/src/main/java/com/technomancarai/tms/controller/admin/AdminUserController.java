package com.technomancarai.tms.controller.admin;

import com.technomancarai.tms.dto.request.UpdateUserAdminRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "03. User Management", description = "Endpoints for managing users, activating/deactivating, and soft deletion")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "01. View All Users", description = "Retrieve paginated list of all registered users in the system.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<UserResponse> pageResponse = adminUserService.getAllUsers(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "02. View User by ID", description = "Fetch detailed user details by user ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "Target user ID", example = "1") @PathVariable Long id
    ) {
        UserResponse response = adminUserService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "User details retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "03. Search Users", description = "Search users by first name, last name, or email with pagination.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User search completed successfully")
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUsers(
            @Parameter(description = "Search query string (name or email)", example = "John") @RequestParam String query,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<UserResponse> pageResponse = adminUserService.searchUsers(query, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "User search completed successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "04. Update User Profile", description = "Update user profile details by ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(
            @Parameter(description = "Target user ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateUserAdminRequest request
    ) {
        UserResponse response = adminUserService.updateUserProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "User profile updated successfully"));
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "05. Activate User", description = "Enables user account access.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User activated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(
            @Parameter(description = "Target user ID", example = "1") @PathVariable Long id
    ) {
        UserResponse response = adminUserService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success(response, "User activated successfully"));
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "06. Deactivate User", description = "Disables user account access.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deactivated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(
            @Parameter(description = "Target user ID", example = "1") @PathVariable Long id
    ) {
        UserResponse response = adminUserService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success(response, "User deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "07. Delete User (Soft Delete)", description = "Soft deletes user account by setting isActive to false.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User soft deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(
            @Parameter(description = "Target user ID", example = "1") @PathVariable Long id
    ) {
        UserResponse response = adminUserService.softDeleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(response, "User soft deleted successfully"));
    }
}
