package com.technomancarai.tms.dto.response;

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
public class EmployeeTaskSummaryResponse {

    private Long userId;
    private String userName;
    private String userEmail;
    private Long totalAssignedTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Long overdueTasks;
}
