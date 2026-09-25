package com.technomancarai.tms.controller;

import com.technomancarai.tms.TaskManagementSystemApplication;
import com.technomancarai.tms.dto.request.DepartmentRequest;
import com.technomancarai.tms.dto.request.DesignationRequest;
import com.technomancarai.tms.dto.response.DepartmentResponse;
import com.technomancarai.tms.dto.response.DesignationResponse;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.service.DepartmentService;
import com.technomancarai.tms.service.DesignationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TaskManagementSystemApplication.class)
@AutoConfigureMockMvc
public class AdminDepartmentAndDesignationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private DesignationService designationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateDepartment() throws Exception {
        DepartmentResponse response = DepartmentResponse.builder().id(1L).name("HR").build();
        when(departmentService.createDepartment(any(DepartmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"HR\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("HR"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void nonAdminGetsForbiddenForDepartment() throws Exception {
        mockMvc.perform(get("/api/v1/admin/departments"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void pmGetsForbiddenForDesignation() throws Exception {
        mockMvc.perform(get("/api/v1/admin/designations"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanSearchDepartments() throws Exception {
        PageResponse<DepartmentResponse> pageResponse = PageResponse.<DepartmentResponse>builder()
                .content(List.of(DepartmentResponse.builder().id(1L).name("IT").build()))
                .pageNo(0).pageSize(10).totalElements(1).totalPages(1).isLast(true)
                .build();

        when(departmentService.searchDepartments(eq("IT"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/admin/departments/search?query=IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("IT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateDesignation() throws Exception {
        DesignationResponse response = DesignationResponse.builder().id(1L).name("Lead Developer").build();
        when(designationService.createDesignation(any(DesignationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/designations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lead Developer\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Lead Developer"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanSearchDesignations() throws Exception {
        PageResponse<DesignationResponse> pageResponse = PageResponse.<DesignationResponse>builder()
                .content(List.of(DesignationResponse.builder().id(1L).name("Architect").build()))
                .pageNo(0).pageSize(10).totalElements(1).totalPages(1).isLast(true)
                .build();

        when(designationService.searchDesignations(eq("Arch"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/admin/designations/search?query=Arch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Architect"));
    }
}
