package com.technomancarai.tms.controller.admin;

import com.technomancarai.tms.dto.request.AssignRoleRequest;
import com.technomancarai.tms.dto.request.RoleRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.RoleResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.service.RoleService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
@Tag(name = "04. Role Management", description = "Endpoints for creating, updating, listing roles, and assigning roles to users")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "01. Create Role", description = "Creates a new system role.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Role created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid role payload or duplicate role name")
    })
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Role created successfully"));
    }

    @GetMapping
    @Operation(summary = "02. Get All Roles", description = "Retrieves a list of all system roles.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success(roles, "Roles retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "03. Update Role", description = "Updates an existing role by ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @Parameter(description = "Target role ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody RoleRequest request
    ) {
        RoleResponse response = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Role updated successfully"));
    }

    @PostMapping("/assign")
    @Operation(summary = "04. Assign Role to User", description = "Assigns a role to a specified user.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role assigned to user successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User or role not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> assignRoleToUser(@Valid @RequestBody AssignRoleRequest request) {
        UserResponse response = roleService.assignRoleToUser(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Role assigned to user successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "05. Delete Role", description = "Deletes a role by ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @Parameter(description = "Target role ID", example = "1") @PathVariable Long id
    ) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully"));
    }
}
