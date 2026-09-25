package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.dto.response.LookupResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.mapper.UserMapper;
import com.technomancarai.tms.repository.CityRepository;
import com.technomancarai.tms.repository.DepartmentRepository;
import com.technomancarai.tms.repository.DesignationRepository;
import com.technomancarai.tms.repository.TaskPriorityRepository;
import com.technomancarai.tms.repository.TaskStatusRepository;
import com.technomancarai.tms.repository.TaskTypeRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.repository.UserRoleRepository;
import com.technomancarai.tms.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LookupServiceImpl implements LookupService {

    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final CityRepository cityRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskPriorityRepository taskPriorityRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<LookupResponse> getDepartments() {
        return departmentRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(d -> LookupResponse.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupResponse> getDesignations() {
        return designationRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(d -> LookupResponse.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupResponse> getCities() {
        return cityRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(c -> LookupResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .extra(c.getState() != null ? c.getState().getName() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupResponse> getTaskStatuses() {
        return taskStatusRepository.findAll(Sort.by("id").ascending())
                .stream()
                .map(s -> LookupResponse.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupResponse> getTaskPriorities() {
        return taskPriorityRepository.findAll(Sort.by("id").ascending())
                .stream()
                .map(p -> LookupResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupResponse> getTaskTypes() {
        return taskTypeRepository.findAll(Sort.by("id").ascending())
                .stream()
                .map(t -> LookupResponse.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsersForLookup(String query) {
        List<User> users;
        if (query == null || query.isBlank()) {
            users = userRepository.findByIsActiveTrue();
        } else {
            String searchTerm = query.trim();
            users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    searchTerm, searchTerm, searchTerm, PageRequest.of(0, 50)
            ).getContent();
        }

        return users.stream()
                .map(user -> {
                    List<String> roles = userRoleRepository.findByUserId(user.getId())
                            .stream()
                            .map(ur -> ur.getRole().getName())
                            .collect(Collectors.toList());
                    return userMapper.toUserResponseWithRoles(user, roles);
                })
                .collect(Collectors.toList());
    }
}
