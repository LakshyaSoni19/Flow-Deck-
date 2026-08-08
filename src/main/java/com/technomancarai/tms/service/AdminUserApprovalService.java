package com.technomancarai.tms.service;

import com.technomancarai.tms.dto.request.ApproveUserRequest;
import com.technomancarai.tms.dto.request.RejectUserRequest;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.UserResponse;

public interface AdminUserApprovalService {

    PageResponse<UserResponse> getPendingUsers(int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<UserResponse> getApprovedUsers(int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse<UserResponse> getRejectedUsers(int pageNo, int pageSize, String sortBy, String sortDir);

    UserResponse getUserDetails(Long userId);

    UserResponse approveUser(Long userId, ApproveUserRequest request);

    UserResponse rejectUser(Long userId, RejectUserRequest request);
}
