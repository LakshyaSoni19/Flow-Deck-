package com.technomancarai.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskCommentRequest {

    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotBlank(message = "Comment content cannot be empty")
    private String comment;
}
