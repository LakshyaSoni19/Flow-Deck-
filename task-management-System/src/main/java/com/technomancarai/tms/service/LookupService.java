package com.technomancarai.tms.service;

import com.technomancarai.tms.dto.response.LookupResponse;
import com.technomancarai.tms.dto.response.UserResponse;

import java.util.List;

public interface LookupService {

    List<LookupResponse> getDepartments();

    List<LookupResponse> getDesignations();

    List<LookupResponse> getCities();

    List<LookupResponse> getTaskStatuses();

    List<LookupResponse> getTaskPriorities();

    List<LookupResponse> getTaskTypes();

    List<UserResponse> searchUsersForLookup(String query);
}
