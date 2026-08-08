package com.technomancarai.tms.service;

import com.technomancarai.tms.dto.request.UpdateUserAdminRequest;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.UserResponse;

public interface AdminUserService {

    PageResponse<UserResponse> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);

    UserResponse getUserById(Long id);

    UserResponse activateUser(Long id);

    UserResponse deactivateUser(Long id);

    UserResponse softDeleteUser(Long id);

    UserResponse updateUserProfile(Long id, UpdateUserAdminRequest request);

    PageResponse<UserResponse> searchUsers(String query, int pageNo, int pageSize, String sortBy, String sortDir);
}
