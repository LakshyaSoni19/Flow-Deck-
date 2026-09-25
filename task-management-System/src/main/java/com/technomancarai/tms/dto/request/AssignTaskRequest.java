package com.technomancarai.tms.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class AssignTaskRequest {

    @NotNull(message = "Assigned user ID is required")
    @JsonAlias({"assignedToId", "assignedUserId", "assigneeId", "userId"})
    private Long assignedUserId;

    public Long getAssignedToId() {
        return assignedUserId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedUserId = assignedToId;
    }
}

