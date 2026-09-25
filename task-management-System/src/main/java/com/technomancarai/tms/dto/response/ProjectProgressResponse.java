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
public class ProjectProgressResponse {

    private Long projectId;
    private String projectName;
    private Double completionPercentage;
    private Long totalTasks;
    private Long completedTasks;
    private Long pendingTasks;
}
