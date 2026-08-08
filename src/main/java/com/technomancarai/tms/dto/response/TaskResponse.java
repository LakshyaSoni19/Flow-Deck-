package com.technomancarai.tms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private Integer completionPercentage;
    private Long projectId;
    private String projectName;
    private UserResponse assignedTo;
    private UserResponse createdBy;
    private String taskStatus;
    private String taskPriority;
    private String taskType;
    private LocalDateTime createdAt;
}
