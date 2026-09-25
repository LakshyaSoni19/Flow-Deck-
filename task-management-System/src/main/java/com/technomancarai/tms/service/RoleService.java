package com.technomancarai.tms.service;

import com.technomancarai.tms.dto.request.AssignRoleRequest;
import com.technomancarai.tms.dto.request.RoleRequest;
import com.technomancarai.tms.dto.response.RoleResponse;
import com.technomancarai.tms.dto.response.UserResponse;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);

    List<RoleResponse> getAllRoles();

    UserResponse assignRoleToUser(AssignRoleRequest request);
}
