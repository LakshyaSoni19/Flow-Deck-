package com.technomancarai.tms.controller;

import com.technomancarai.tms.TaskManagementSystemApplication;
import com.technomancarai.tms.dto.request.AssignProjectManagerRequest;
import com.technomancarai.tms.dto.request.ProjectRequest;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.entity.Role;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.entity.UserRole;
import com.technomancarai.tms.repository.ProjectMemberRepository;
import com.technomancarai.tms.repository.ProjectRepository;
import com.technomancarai.tms.repository.RoleRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.repository.UserRoleRepository;
import com.technomancarai.tms.service.AdminProjectService;
import com.technomancarai.tms.service.ProjectManagerService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TaskManagementSystemApplication.class)
@Transactional
public class ProjectManagerAssignmentBugTest {

    @Autowired
    private AdminProjectService adminProjectService;

    @Autowired
    private ProjectManagerService projectManagerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    public void setUp() {
        Mockito.when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    public void testAdminCreatesProjectAndAssignsSahilAsPM() {
        // 1. Setup Admin user
        String adminEmail = "lakshyasoni0422@gmail.com";
        User admin = userRepository.findByEmail(adminEmail).orElseGet(() -> {
            User u = User.builder()
                .firstName("Lakshya")
                .lastName("Soni")
                .email(adminEmail)
                .password("password123")
                .approvalStatus("APPROVED")
                .build();
            u.setIsActive(true);
            return userRepository.save(u);
        });

        // 2. Setup Sahil user as a regular Employee (Initial state before PM assignment)
        String sahilEmail = "sahilotwal02@gmail.com";
        User sahil = userRepository.findByEmail(sahilEmail).orElseGet(() -> {
            User u = User.builder()
                .firstName("Sahil")
                .lastName("Otwal")
                .email(sahilEmail)
                .password("password123")
                .approvalStatus("APPROVED")
                .build();
            u.setIsActive(true);
            return userRepository.save(u);
        });

        Role employeeRole = roleRepository.findFirstByName("ROLE_EMPLOYEE").orElseGet(() -> {
            Role r = Role.builder().name("ROLE_EMPLOYEE").build();
            r.setIsActive(true);
            return roleRepository.save(r);
        });

        if (userRoleRepository.findByUserId(sahil.getId()).isEmpty()) {
            UserRole ur = UserRole.builder().user(sahil).role(employeeRole).build();
            ur.setIsActive(true);
            userRoleRepository.save(ur);
        }

        // Verify Sahil initially does NOT have ROLE_PROJECT_MANAGER
        // 3. Admin creates project assigning Sahil via managerId
        ProjectRequest createRequest = ProjectRequest.builder()
                .projectName("Test Flow Deck Project Bug Test")
                .projectCode("TFD-BUG-001")
                .description("Test project")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .status("IN_PROGRESS")
                .managerId(sahil.getId())
                .build();

        ProjectResponse createResponse = adminProjectService.createProject(createRequest, adminEmail);
        assertThat(createResponse).isNotNull();
        assertThat(createResponse.getManagerId()).isEqualTo(sahil.getId());

        // 4. Verify that assigning Sahil automatically:
        // A. Granted ROLE_PROJECT_MANAGER role to Sahil
        boolean nowHasPmRole = userRoleRepository.findByUserId(sahil.getId()).stream()
                .anyMatch(ur -> "ROLE_PROJECT_MANAGER".equals(ur.getRole().getName()));
        assertThat(nowHasPmRole).isTrue();

        // B. Added Sahil to project_member table for this project
        boolean isProjectMember = projectMemberRepository.existsByProjectIdAndUserId(createResponse.getId(), sahil.getId());
        assertThat(isProjectMember).isTrue();

        // 5. Query Sahil's assigned projects as PM
        PageResponse<ProjectResponse> sahilProjects = projectManagerService.getAssignedProjects(sahilEmail, 0, 10, "id", "asc");

        assertThat(sahilProjects.getContent())
                .withFailMessage("Expected PM Sahil to see assigned project, but got empty list!")
                .isNotEmpty();

        boolean containsCreatedProject = sahilProjects.getContent().stream()
                .anyMatch(p -> p.getId().equals(createResponse.getId()));
        assertThat(containsCreatedProject).isTrue();
    }
}
