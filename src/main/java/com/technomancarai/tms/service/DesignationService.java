package com.technomancarai.tms.service;

import com.technomancarai.tms.dto.request.DesignationRequest;
import com.technomancarai.tms.dto.response.DesignationResponse;
import com.technomancarai.tms.dto.response.PageResponse;

public interface DesignationService {

    DesignationResponse createDesignation(DesignationRequest request);

    DesignationResponse updateDesignation(Long id, DesignationRequest request);

    void deleteDesignation(Long id);

    DesignationResponse getDesignationById(Long id);

    PageResponse<DesignationResponse> getAllDesignations(int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<DesignationResponse> searchDesignations(String query, int pageNo, int pageSize, String sortBy, String sortDir);
}
