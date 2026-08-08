package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.dto.request.DepartmentRequest;
import com.technomancarai.tms.dto.response.DepartmentResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.entity.Department;
import com.technomancarai.tms.exception.BadRequestException;
import com.technomancarai.tms.exception.DuplicateResourceException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.mapper.DepartmentMapper;
import com.technomancarai.tms.repository.DepartmentRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.service.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        String departmentName = request.getName().trim();
        if (departmentRepository.existsByName(departmentName)) {
            throw new DuplicateResourceException("Department", "name", departmentName);
        }

        Department department = departmentMapper.toDepartment(request);
        department.setName(departmentName);
        Department savedDepartment = departmentRepository.save(department);

        return departmentMapper.toDepartmentResponse(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        String newName = request.getName().trim();
        if (departmentRepository.existsByNameAndIdNot(newName, id)) {
            throw new DuplicateResourceException("Department", "name", newName);
        }

        departmentMapper.updateDepartmentFromRequest(request, department);
        department.setName(newName);
        Department updatedDepartment = departmentRepository.save(department);

        return departmentMapper.toDepartmentResponse(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        if (userRepository.existsByDepartmentId(id)) {
            throw new BadRequestException("Cannot delete department because it is assigned to one or more users");
        }

        departmentRepository.delete(department);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        return departmentMapper.toDepartmentResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> getAllDepartments(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Department> departmentPage = departmentRepository.findAll(pageable);

        List<DepartmentResponse> content = departmentPage.getContent().stream()
                .map(departmentMapper::toDepartmentResponse)
                .collect(Collectors.toList());

        return PageResponse.<DepartmentResponse>builder()
                .content(content)
                .pageNo(departmentPage.getNumber())
                .pageSize(departmentPage.getSize())
                .totalElements(departmentPage.getTotalElements())
                .totalPages(departmentPage.getTotalPages())
                .isLast(departmentPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> searchDepartments(String query, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Department> departmentPage = departmentRepository.findByNameContainingIgnoreCase(query, pageable);

        List<DepartmentResponse> content = departmentPage.getContent().stream()
                .map(departmentMapper::toDepartmentResponse)
                .collect(Collectors.toList());

        return PageResponse.<DepartmentResponse>builder()
                .content(content)
                .pageNo(departmentPage.getNumber())
                .pageSize(departmentPage.getSize())
                .totalElements(departmentPage.getTotalElements())
                .totalPages(departmentPage.getTotalPages())
                .isLast(departmentPage.isLast())
                .build();
    }
}
