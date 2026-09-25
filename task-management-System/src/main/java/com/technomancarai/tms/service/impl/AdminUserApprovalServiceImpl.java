package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.dto.request.ApproveUserRequest;
import com.technomancarai.tms.dto.request.RejectUserRequest;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.entity.Department;
import com.technomancarai.tms.entity.Designation;
import com.technomancarai.tms.entity.Role;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.entity.UserRole;
import com.technomancarai.tms.exception.BadRequestException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.mapper.UserMapper;
import com.technomancarai.tms.repository.DepartmentRepository;
import com.technomancarai.tms.repository.DesignationRepository;
import com.technomancarai.tms.repository.RoleRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.repository.UserRoleRepository;
import com.technomancarai.tms.service.AdminUserApprovalService;
import com.technomancarai.tms.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserApprovalServiceImpl implements AdminUserApprovalService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final EmailService emailService;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getPendingUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        return getUsersByApprovalStatus("PENDING", pageNo, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getApprovedUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        return getUsersByApprovalStatus("APPROVED", pageNo, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getRejectedUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        return getUsersByApprovalStatus("REJECTED", pageNo, pageSize, sortBy, sortDir);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return mapUserToResponseWithRoles(user);
    }

    @Override
    @Transactional
    public UserResponse approveUser(Long userId, ApproveUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if ("APPROVED".equalsIgnoreCase(user.getApprovalStatus())) {
            throw new BadRequestException("User account is already approved");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId()));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + request.getDesignationId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));

        user.setDepartment(department);
        user.setDesignation(designation);
        user.setApprovalStatus("APPROVED");
        user.setRejectionReason(null);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        if (!userRoleRepository.existsByUserIdAndRoleId(savedUser.getId(), role.getId())) {
            UserRole userRole = UserRole.builder()
                    .user(savedUser)
                    .role(role)
                    .build();
            userRole.setIsActive(true);
            userRoleRepository.save(userRole);
        }

        emailService.sendApprovalEmail(
                savedUser.getEmail(),
                savedUser.getFirstName() + " " + savedUser.getLastName(),
                role.getName()
        );

        return mapUserToResponseWithRoles(savedUser);
    }

    @Override
    @Transactional
    public UserResponse rejectUser(Long userId, RejectUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if ("REJECTED".equalsIgnoreCase(user.getApprovalStatus())) {
            throw new BadRequestException("User account is already rejected");
        }

        user.setApprovalStatus("REJECTED");
        user.setRejectionReason(request.getRejectionReason());
        user.setIsActive(false);

        User savedUser = userRepository.save(user);

        emailService.sendRejectionEmail(
                savedUser.getEmail(),
                savedUser.getFirstName() + " " + savedUser.getLastName(),
                request.getRejectionReason()
        );

        return mapUserToResponseWithRoles(savedUser);
    }

    private PageResponse<UserResponse> getUsersByApprovalStatus(String status, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> usersPage = userRepository.findByApprovalStatus(status, pageable);

        List<UserResponse> content = usersPage.getContent().stream()
                .map(this::mapUserToResponseWithRoles)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .content(content)
                .pageNo(usersPage.getNumber())
                .pageSize(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .isLast(usersPage.isLast())
                .build();
    }

    private UserResponse mapUserToResponseWithRoles(User user) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<String> roles = userRoles.stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());
        return userMapper.toUserResponseWithRoles(user, roles);
    }
}
