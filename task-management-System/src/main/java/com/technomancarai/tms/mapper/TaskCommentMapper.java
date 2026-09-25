package com.technomancarai.tms.mapper;

import com.technomancarai.tms.dto.request.CreateTaskCommentRequest;
import com.technomancarai.tms.dto.response.TaskCommentResponse;
import com.technomancarai.tms.entity.TaskComment;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = {UserMapper.class})
public interface TaskCommentMapper {

    @Mapping(target = "taskId", source = "task.id")
    TaskCommentResponse toTaskCommentResponse(TaskComment taskComment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "commentDate", ignore = true)
    TaskComment toTaskComment(CreateTaskCommentRequest request);
}
