package com.technomancarai.tms.service;

import com.technomancarai.tms.dto.request.DesignationRequest;
import com.technomancarai.tms.dto.response.DesignationResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.entity.Designation;
import com.technomancarai.tms.exception.BadRequestException;
import com.technomancarai.tms.exception.DuplicateResourceException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.mapper.DesignationMapper;
import com.technomancarai.tms.repository.DesignationRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.service.impl.DesignationServiceImpl;
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
public class DesignationServiceTest {

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DesignationMapper designationMapper;

    @InjectMocks
    private DesignationServiceImpl designationService;

    private Designation designation;
    private DesignationRequest designationRequest;
    private DesignationResponse designationResponse;

    @BeforeEach
    void setUp() {
        designation = Designation.builder()
                .name("Software Engineer")
                .build();
        designation.setId(1L);

        designationRequest = DesignationRequest.builder()
                .name("Software Engineer")
                .build();

        designationResponse = DesignationResponse.builder()
                .id(1L)
                .name("Software Engineer")
                .build();
    }

    @Test
    void createDesignation_Success() {
        when(designationRepository.existsByName("Software Engineer")).thenReturn(false);
        when(designationMapper.toDesignation(any(DesignationRequest.class))).thenReturn(designation);
        when(designationRepository.save(any(Designation.class))).thenReturn(designation);
        when(designationMapper.toDesignationResponse(any(Designation.class))).thenReturn(designationResponse);

        DesignationResponse result = designationService.createDesignation(designationRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Software Engineer");
        verify(designationRepository).save(any(Designation.class));
    }

    @Test
    void createDesignation_DuplicateName_ThrowsDuplicateResourceException() {
        when(designationRepository.existsByName("Software Engineer")).thenReturn(true);

        assertThatThrownBy(() -> designationService.createDesignation(designationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Designation");
    }

    @Test
    void updateDesignation_Success() {
        when(designationRepository.findById(1L)).thenReturn(Optional.of(designation));
        when(designationRepository.existsByNameAndIdNot("Software Engineer", 1L)).thenReturn(false);
        doNothing().when(designationMapper).updateDesignationFromRequest(any(DesignationRequest.class), any(Designation.class));
        when(designationRepository.save(any(Designation.class))).thenReturn(designation);
        when(designationMapper.toDesignationResponse(any(Designation.class))).thenReturn(designationResponse);

        DesignationResponse result = designationService.updateDesignation(1L, designationRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Software Engineer");
    }

    @Test
    void updateDesignation_DuplicateName_ThrowsDuplicateResourceException() {
        when(designationRepository.findById(1L)).thenReturn(Optional.of(designation));
        when(designationRepository.existsByNameAndIdNot("Software Engineer", 1L)).thenReturn(true);

        assertThatThrownBy(() -> designationService.updateDesignation(1L, designationRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void deleteDesignation_Success() {
        when(designationRepository.findById(1L)).thenReturn(Optional.of(designation));
        when(userRepository.existsByDesignationId(1L)).thenReturn(false);
        doNothing().when(designationRepository).delete(designation);

        designationService.deleteDesignation(1L);

        verify(designationRepository).delete(designation);
    }

    @Test
    void deleteDesignation_AssignedToUser_ThrowsBadRequestException() {
        when(designationRepository.findById(1L)).thenReturn(Optional.of(designation));
        when(userRepository.existsByDesignationId(1L)).thenReturn(true);

        assertThatThrownBy(() -> designationService.deleteDesignation(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("assigned to one or more users");
    }

    @Test
    void getDesignationById_Success() {
        when(designationRepository.findById(1L)).thenReturn(Optional.of(designation));
        when(designationMapper.toDesignationResponse(designation)).thenReturn(designationResponse);

        DesignationResponse result = designationService.getDesignationById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getDesignationById_NotFound_ThrowsResourceNotFoundException() {
        when(designationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> designationService.getDesignationById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllDesignations_Success() {
        Page<Designation> page = new PageImpl<>(List.of(designation));
        when(designationRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(designationMapper.toDesignationResponse(designation)).thenReturn(designationResponse);

        PageResponse<DesignationResponse> response = designationService.getAllDesignations(0, 10, "id", "asc");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void searchDesignations_Success() {
        Page<Designation> page = new PageImpl<>(List.of(designation));
        when(designationRepository.findByNameContainingIgnoreCase(eq("Software"), any(Pageable.class))).thenReturn(page);
        when(designationMapper.toDesignationResponse(designation)).thenReturn(designationResponse);

        PageResponse<DesignationResponse> response = designationService.searchDesignations("Software", 0, 10, "id", "asc");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }
}
