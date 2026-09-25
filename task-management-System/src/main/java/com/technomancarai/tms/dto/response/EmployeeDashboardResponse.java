package com.technomancarai.tms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardResponse {

    private Long totalAssignedTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Long overdueTasks;
    private Double completionPercentage;
    private List<TaskResponse> upcomingDeadlines;
}
