package com.technomancarai.tms.service.impl;

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
import com.technomancarai.tms.service.DesignationService;
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
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepository;
    private final UserRepository userRepository;
    private final DesignationMapper designationMapper;

    @Override
    @Transactional
    public DesignationResponse createDesignation(DesignationRequest request) {
        String designationName = request.getName().trim();
        if (designationRepository.existsByName(designationName)) {
            throw new DuplicateResourceException("Designation", "name", designationName);
        }

        Designation designation = designationMapper.toDesignation(request);
        designation.setName(designationName);
        Designation savedDesignation = designationRepository.save(designation);

        return designationMapper.toDesignationResponse(savedDesignation);
    }

    @Override
    @Transactional
    public DesignationResponse updateDesignation(Long id, DesignationRequest request) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + id));

        String newName = request.getName().trim();
        if (designationRepository.existsByNameAndIdNot(newName, id)) {
            throw new DuplicateResourceException("Designation", "name", newName);
        }

        designationMapper.updateDesignationFromRequest(request, designation);
        designation.setName(newName);
        Designation updatedDesignation = designationRepository.save(designation);

        return designationMapper.toDesignationResponse(updatedDesignation);
    }

    @Override
    @Transactional
    public void deleteDesignation(Long id) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + id));

        if (userRepository.existsByDesignationId(id)) {
            throw new BadRequestException("Cannot delete designation because it is assigned to one or more users");
        }

        designationRepository.delete(designation);
    }

    @Override
    @Transactional(readOnly = true)
    public DesignationResponse getDesignationById(Long id) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + id));

        return designationMapper.toDesignationResponse(designation);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DesignationResponse> getAllDesignations(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Designation> designationPage = designationRepository.findAll(pageable);

        List<DesignationResponse> content = designationPage.getContent().stream()
                .map(designationMapper::toDesignationResponse)
                .collect(Collectors.toList());

        return PageResponse.<DesignationResponse>builder()
                .content(content)
                .pageNo(designationPage.getNumber())
                .pageSize(designationPage.getSize())
                .totalElements(designationPage.getTotalElements())
                .totalPages(designationPage.getTotalPages())
                .isLast(designationPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DesignationResponse> searchDesignations(String query, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Designation> designationPage = designationRepository.findByNameContainingIgnoreCase(query, pageable);

        List<DesignationResponse> content = designationPage.getContent().stream()
                .map(designationMapper::toDesignationResponse)
                .collect(Collectors.toList());

        return PageResponse.<DesignationResponse>builder()
                .content(content)
                .pageNo(designationPage.getNumber())
                .pageSize(designationPage.getSize())
                .totalElements(designationPage.getTotalElements())
                .totalPages(designationPage.getTotalPages())
                .isLast(designationPage.isLast())
                .build();
    }
}
