package com.technomancarai.tms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class UpdateTaskRequest {

    @Size(max = 200, message = "Task title must not exceed 200 characters")
    private String title;

    private String description;
    private LocalDate dueDate;
    private BigDecimal actualHours;

    @Min(value = 0, message = "Completion percentage cannot be less than 0")
    @Max(value = 100, message = "Completion percentage cannot exceed 100")
    private Integer completionPercentage;

    private Long assignedToId;
    private Long taskStatusId;
    private Long taskPriorityId;
    private Long taskTypeId;
}
