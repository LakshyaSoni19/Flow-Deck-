package com.technomancarai.tms.mapper;

import com.technomancarai.tms.dto.request.AddProjectMemberRequest;
import com.technomancarai.tms.dto.response.ProjectMemberResponse;
import com.technomancarai.tms.entity.ProjectMember;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = {UserMapper.class})
public interface ProjectMemberMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.projectName")
    ProjectMemberResponse toProjectMemberResponse(ProjectMember projectMember);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "user", ignore = true)
    ProjectMember toProjectMember(AddProjectMemberRequest request);
}
