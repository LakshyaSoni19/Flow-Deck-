package com.technomancarai.tms.service;

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
import com.technomancarai.tms.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private DepartmentRequest departmentRequest;
    private DepartmentResponse departmentResponse;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .name("Engineering")
                .build();
        department.setId(1L);

        departmentRequest = DepartmentRequest.builder()
                .name("Engineering")
                .build();

        departmentResponse = DepartmentResponse.builder()
                .id(1L)
                .name("Engineering")
                .build();
    }

    @Test
    void createDepartment_Success() {
        when(departmentRepository.existsByName("Engineering")).thenReturn(false);
        when(departmentMapper.toDepartment(any(DepartmentRequest.class))).thenReturn(department);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(departmentMapper.toDepartmentResponse(any(Department.class))).thenReturn(departmentResponse);

        DepartmentResponse result = departmentService.createDepartment(departmentRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Engineering");
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void createDepartment_DuplicateName_ThrowsDuplicateResourceException() {
        when(departmentRepository.existsByName("Engineering")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.createDepartment(departmentRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Department");
    }

    @Test
    void updateDepartment_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.existsByNameAndIdNot("Engineering", 1L)).thenReturn(false);
        doNothing().when(departmentMapper).updateDepartmentFromRequest(any(DepartmentRequest.class), any(Department.class));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(departmentMapper.toDepartmentResponse(any(Department.class))).thenReturn(departmentResponse);

        DepartmentResponse result = departmentService.updateDepartment(1L, departmentRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Engineering");
    }

    @Test
    void updateDepartment_DuplicateName_ThrowsDuplicateResourceException() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.existsByNameAndIdNot("Engineering", 1L)).thenReturn(true);

        assertThatThrownBy(() -> departmentService.updateDepartment(1L, departmentRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void deleteDepartment_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(userRepository.existsByDepartmentId(1L)).thenReturn(false);
        doNothing().when(departmentRepository).delete(department);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).delete(department);
    }

    @Test
    void deleteDepartment_AssignedToUser_ThrowsBadRequestException() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(userRepository.existsByDepartmentId(1L)).thenReturn(true);

        assertThatThrownBy(() -> departmentService.deleteDepartment(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("assigned to one or more users");
    }

    @Test
    void getDepartmentById_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentMapper.toDepartmentResponse(department)).thenReturn(departmentResponse);

        DepartmentResponse result = departmentService.getDepartmentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getDepartmentById_NotFound_ThrowsResourceNotFoundException() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllDepartments_Success() {
        Page<Department> page = new PageImpl<>(List.of(department));
        when(departmentRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(departmentMapper.toDepartmentResponse(department)).thenReturn(departmentResponse);

        PageResponse<DepartmentResponse> response = departmentService.getAllDepartments(0, 10, "id", "asc");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void searchDepartments_Success() {
        Page<Department> page = new PageImpl<>(List.of(department));
        when(departmentRepository.findByNameContainingIgnoreCase(eq("Eng"), any(Pageable.class))).thenReturn(page);
        when(departmentMapper.toDepartmentResponse(department)).thenReturn(departmentResponse);

        PageResponse<DepartmentResponse> response = departmentService.searchDepartments("Eng", 0, 10, "id", "asc");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }
}
