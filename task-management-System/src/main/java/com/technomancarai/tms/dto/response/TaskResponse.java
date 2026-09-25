package com.technomancarai.tms.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("assignee")
    public UserResponse getAssignee() {
        return assignedTo;
    }

    @JsonProperty("assignedUserId")
    public Long getAssignedUserId() {
        return assignedTo != null ? assignedTo.getId() : null;
    }

    @JsonProperty("assignedToId")
    public Long getAssignedToId() {
        return assignedTo != null ? assignedTo.getId() : null;
    }

    @JsonProperty("assignedUserName")
    public String getAssignedUserName() {
        return buildUserName(assignedTo);
    }

    @JsonProperty("assignedToName")
    public String getAssignedToName() {
        return buildUserName(assignedTo);
    }

    private String buildUserName(UserResponse user) {
        if (user == null) return null;
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        String full = (first + " " + last).trim();
        return full.isEmpty() ? user.getEmail() : full;
    }
}

