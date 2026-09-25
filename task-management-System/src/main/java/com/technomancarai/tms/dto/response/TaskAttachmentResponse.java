package com.technomancarai.tms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachmentResponse {

    private Long id;
    private Long taskId;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    private UserResponse uploadedBy;
}
