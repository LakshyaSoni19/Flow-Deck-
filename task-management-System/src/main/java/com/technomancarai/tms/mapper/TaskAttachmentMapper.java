package com.technomancarai.tms.mapper;

import com.technomancarai.tms.dto.response.TaskAttachmentResponse;
import com.technomancarai.tms.entity.TaskAttachment;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = {UserMapper.class})
public interface TaskAttachmentMapper {

    @Mapping(target = "taskId", source = "task.id")
    TaskAttachmentResponse toTaskAttachmentResponse(TaskAttachment taskAttachment);
}
