package com.technomancarai.tms.mapper;

import com.technomancarai.tms.dto.response.NotificationResponse;
import com.technomancarai.tms.entity.Notification;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface NotificationMapper {

    NotificationResponse toNotificationResponse(Notification notification);
}
