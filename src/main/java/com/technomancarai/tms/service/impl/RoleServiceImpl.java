package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.dto.request.AssignRoleRequest;
import com.technomancarai.tms.dto.request.RoleRequest;
import com.technomancarai.tms.dto.response.RoleResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.entity.Role;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.entity.UserRole;
import com.technomancarai.tms.exception.DuplicateResourceException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.mapper.RoleMapper;
import com.technomancarai.tms.mapper.UserMapper;
import com.technomancarai.tms.repository.RoleRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.repository.UserRoleRepository;
import com.technomancarai.tms.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        String roleName = request.getName().trim();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        if (roleRepository.existsByName(roleName)) {
            throw new DuplicateResourceException("Role already exists with name: " + roleName);
        }

        Role role = Role.builder()
                .name(roleName)
                .build();
        role.setIsActive(true);

        Role savedRole = roleRepository.save(role);
        return roleMapper.toRoleResponse(savedRole);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        String roleName = request.getName().trim();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        if (!role.getName().equalsIgnoreCase(roleName) && roleRepository.existsByName(roleName)) {
            throw new DuplicateResourceException("Role already exists with name: " + roleName);
        }

        role.setName(roleName);
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toRoleResponse(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
        roleRepository.delete(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse assignRoleToUser(AssignRoleRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));

        if (!userRoleRepository.existsByUserIdAndRoleId(user.getId(), role.getId())) {
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();
            userRole.setIsActive(true);
            userRoleRepository.save(userRole);
        }

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<String> roles = userRoles.stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        return userMapper.toUserResponseWithRoles(user, roles);
    }
}
