package com.technomancarai.tms.mapper;

import com.technomancarai.tms.dto.request.CreateProjectRequest;
import com.technomancarai.tms.dto.request.ProjectRequest;
import com.technomancarai.tms.dto.request.UpdateProjectRequest;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.entity.Project;
import com.technomancarai.tms.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ProjectMapper {

    @Mapping(target = "createdByUserId", source = "createdByUser.id")
    @Mapping(target = "createdByUserName", source = "createdByUser", qualifiedByName = "formatUserName")
    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerName", source = "manager", qualifiedByName = "formatUserName")
    ProjectResponse toProjectResponse(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Project toProject(CreateProjectRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Project toProject(ProjectRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "projectCode", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "manager", ignore = true)
    void updateProjectFromRequest(UpdateProjectRequest request, @MappingTarget Project project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "manager", ignore = true)
    void updateProjectFromAdminRequest(ProjectRequest request, @MappingTarget Project project);

    @Named("formatUserName")
    default String formatUserName(User user) {
        if (user == null) {
            return null;
        }
        return (user.getFirstName() != null ? user.getFirstName() : "") +
               (user.getLastName() != null ? " " + user.getLastName() : "");
    }
}
