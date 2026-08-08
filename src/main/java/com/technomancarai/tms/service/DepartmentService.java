package com.technomancarai.tms.service;

import com.technomancarai.tms.dto.request.DepartmentRequest;
import com.technomancarai.tms.dto.response.DepartmentResponse;
import com.technomancarai.tms.dto.response.PageResponse;

public interface DepartmentService {

    DepartmentResponse createDepartment(DepartmentRequest request);

    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);

    void deleteDepartment(Long id);

    DepartmentResponse getDepartmentById(Long id);

    PageResponse<DepartmentResponse> getAllDepartments(int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<DepartmentResponse> searchDepartments(String query, int pageNo, int pageSize, String sortBy, String sortDir);
}
