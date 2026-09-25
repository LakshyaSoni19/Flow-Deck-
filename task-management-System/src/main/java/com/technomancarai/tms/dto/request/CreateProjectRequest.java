package com.technomancarai.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 45, message = "Project name must not exceed 45 characters")
    private String projectName;

    @NotBlank(message = "Project code is required")
    @Size(max = 45, message = "Project code must not exceed 45 characters")
    private String projectCode;

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 45, message = "Status must not exceed 45 characters")
    private String status;
}
