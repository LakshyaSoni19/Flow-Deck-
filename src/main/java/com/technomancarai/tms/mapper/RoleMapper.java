package com.technomancarai.tms.mapper;

import com.technomancarai.tms.dto.request.RoleRequest;
import com.technomancarai.tms.dto.response.RoleResponse;
import com.technomancarai.tms.entity.Role;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RoleMapper {

    RoleResponse toRoleResponse(Role role);

    Role toRole(RoleRequest request);

    void updateRoleFromRequest(RoleRequest request, @MappingTarget Role role);
}
