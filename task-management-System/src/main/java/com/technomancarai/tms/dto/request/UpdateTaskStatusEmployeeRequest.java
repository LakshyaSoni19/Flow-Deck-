package com.technomancarai.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateTaskStatusEmployeeRequest {

    @NotBlank(message = "Task status is required (TO_DO, IN_PROGRESS, or COMPLETED)")
    private String statusName;
}
