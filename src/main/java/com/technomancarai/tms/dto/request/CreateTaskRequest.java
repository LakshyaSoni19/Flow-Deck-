package com.technomancarai.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Task title must not exceed 200 characters")
    private String title;

    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private Long assignedToId;
    private Long taskStatusId;
    private Long taskPriorityId;
    private Long taskTypeId;
}
