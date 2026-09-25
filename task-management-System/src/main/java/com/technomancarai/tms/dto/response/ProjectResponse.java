package com.technomancarai.tms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String projectName;
    private String projectCode;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean isActive;

    private Long createdByUserId;
    private String createdByUserName;

    private Long managerId;
    private String managerName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
