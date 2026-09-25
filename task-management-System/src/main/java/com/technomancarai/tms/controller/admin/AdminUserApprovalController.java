package com.technomancarai.tms.controller.admin;

import com.technomancarai.tms.dto.request.ApproveUserRequest;
import com.technomancarai.tms.dto.request.RejectUserRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.service.AdminUserApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/approval")
@RequiredArgsConstructor
@Tag(name = "02. Admin User Approval", description = "Endpoints for Admin to review, approve, and reject user registration requests")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserApprovalController {

    private final AdminUserApprovalService adminUserApprovalService;

    @GetMapping("/pending")
    @Operation(summary = "01. View Pending Users", description = "Retrieves paginated list of users waiting for Admin approval.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pending user registration requests retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getPendingUsers(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<UserResponse> response = adminUserApprovalService.getPendingUsers(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Pending user registration requests retrieved successfully"));
    }

    @GetMapping("/approved")
    @Operation(summary = "02. View Approved Users", description = "Retrieves paginated list of approved users.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Approved users retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getApprovedUsers(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<UserResponse> response = adminUserApprovalService.getApprovedUsers(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Approved users retrieved successfully"));
    }

    @GetMapping("/rejected")
    @Operation(summary = "03. View Rejected Users", description = "Retrieves paginated list of rejected user registration requests.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rejected user requests retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getRejectedUsers(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<UserResponse> response = adminUserApprovalService.getRejectedUsers(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response, "Rejected user requests retrieved successfully"));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "04. Get User Details", description = "Fetches detailed user registration details by user ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails(
            @Parameter(description = "ID of the target user", example = "1") @PathVariable Long userId
    ) {
        UserResponse response = adminUserApprovalService.getUserDetails(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "User details retrieved successfully"));
    }

    @PostMapping("/approve/{userId}")
    @Operation(summary = "05. Approve User", description = "Approves a pending user registration request and assigns department, designation, and system role.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registration approved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "User is not in PENDING status")
    })
    public ResponseEntity<ApiResponse<UserResponse>> approveUser(
            @Parameter(description = "ID of user to approve", example = "1") @PathVariable Long userId,
            @Valid @RequestBody ApproveUserRequest request
    ) {
        UserResponse response = adminUserApprovalService.approveUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "User registration approved successfully"));
    }

    @PostMapping("/reject/{userId}")
    @Operation(summary = "06. Reject User", description = "Rejects a pending user registration request with a specified rejection reason.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registration rejected successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "User is not in PENDING status")
    })
    public ResponseEntity<ApiResponse<UserResponse>> rejectUser(
            @Parameter(description = "ID of user to reject", example = "1") @PathVariable Long userId,
            @Valid @RequestBody RejectUserRequest request
    ) {
        UserResponse response = adminUserApprovalService.rejectUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "User registration rejected successfully"));
    }
}
