package com.technomancarai.tms.mapper;

import com.technomancarai.tms.dto.request.CreateTaskRequest;
import com.technomancarai.tms.dto.request.PmTaskRequest;
import com.technomancarai.tms.dto.request.UpdateTaskRequest;
import com.technomancarai.tms.dto.response.TaskResponse;
import com.technomancarai.tms.entity.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = {UserMapper.class})
public interface TaskMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.projectName")
    @Mapping(target = "createdBy", source = "createdByUser")
    @Mapping(target = "taskStatus", source = "taskStatus.name")
    @Mapping(target = "taskPriority", source = "taskPriority.name")
    @Mapping(target = "taskType", source = "taskType.name")
    TaskResponse toTaskResponse(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "taskPriority", ignore = true)
    @Mapping(target = "taskType", ignore = true)
    @Mapping(target = "actualHours", ignore = true)
    @Mapping(target = "completionPercentage", ignore = true)
    Task toTask(CreateTaskRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "taskPriority", ignore = true)
    @Mapping(target = "taskType", ignore = true)
    @Mapping(target = "actualHours", ignore = true)
    @Mapping(target = "completionPercentage", ignore = true)
    Task toTask(PmTaskRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "taskPriority", ignore = true)
    @Mapping(target = "taskType", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "estimatedHours", ignore = true)
    void updateTaskFromRequest(UpdateTaskRequest request, @MappingTarget Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "taskPriority", ignore = true)
    @Mapping(target = "taskType", ignore = true)
    @Mapping(target = "actualHours", ignore = true)
    @Mapping(target = "completionPercentage", ignore = true)
    void updateTaskFromPmRequest(PmTaskRequest request, @MappingTarget Task task);
}
