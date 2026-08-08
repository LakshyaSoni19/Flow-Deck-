package com.technomancarai.tms.service;

import com.technomancarai.tms.dto.request.AssignProjectManagerRequest;
import com.technomancarai.tms.dto.request.ProjectRequest;
import com.technomancarai.tms.dto.request.UpdateProjectStatusRequest;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;

public interface AdminProjectService {

    ProjectResponse createProject(ProjectRequest request, String adminEmail);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void deleteProject(Long id);

    PageResponse<ProjectResponse> getAllProjects(int pageNo, int pageSize, String sortBy, String sortDir);

    ProjectResponse getProjectById(Long id);

    ProjectResponse changeProjectStatus(Long id, UpdateProjectStatusRequest request);

    ProjectResponse assignProjectManager(Long id, AssignProjectManagerRequest request);
}
