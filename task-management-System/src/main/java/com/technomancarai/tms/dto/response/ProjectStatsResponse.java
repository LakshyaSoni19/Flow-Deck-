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
public class ProjectStatsResponse {

    private Long projectId;
    private String projectName;
    private Long totalMembers;
    private Long totalTasks;
    private Long completedTasks;
    private Long pendingTasks;
    private Long inProgressTasks;
    private Long overdueTasks;
    private Double completionPercentage;
    private List<TaskResponse> recentlyUpdatedTasks;
}
