package com.technomancarai.tms.mapper;

import com.technomancarai.tms.dto.request.RegisterRequest;
import com.technomancarai.tms.dto.request.UpdateUserAdminRequest;
import com.technomancarai.tms.dto.request.UpdateUserRequest;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "cityName", source = "user.city.name")
    @Mapping(target = "departmentName", source = "user.department.name")
    @Mapping(target = "designationName", source = "user.designation.name")
    @Mapping(target = "roles", source = "roles")
    UserResponse toUserResponseWithRoles(User user, List<String> roles);

    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "designationName", source = "designation.name")
    @Mapping(target = "roles", ignore = true)
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "designation", ignore = true)
    User toUser(RegisterRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "designation", ignore = true)
    void updateUserFromRequest(UpdateUserRequest request, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "designation", ignore = true)
    void updateUserFromAdminRequest(UpdateUserAdminRequest request, @MappingTarget User user);
}
