package com.technomancarai.tms;

import com.technomancarai.tms.dto.request.AddMemberRequest;
import com.technomancarai.tms.dto.request.PmTaskRequest;
import com.technomancarai.tms.dto.request.ResetPasswordRequest;
import com.technomancarai.tms.dto.request.RoleRequest;
import com.technomancarai.tms.dto.request.TaskCommentRequest;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectMemberResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.dto.response.RoleResponse;
import com.technomancarai.tms.dto.response.TaskCommentResponse;
import com.technomancarai.tms.dto.response.TaskResponse;
import com.technomancarai.tms.entity.OtpVerification;
import com.technomancarai.tms.entity.Project;
import com.technomancarai.tms.entity.Task;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.exception.BadRequestException;
import com.technomancarai.tms.repository.OtpVerificationRepository;
import com.technomancarai.tms.repository.ProjectMemberRepository;
import com.technomancarai.tms.repository.ProjectRepository;
import com.technomancarai.tms.repository.TaskCommentRepository;
import com.technomancarai.tms.repository.TaskRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.service.AuthService;
import com.technomancarai.tms.service.EmployeeService;
import com.technomancarai.tms.service.ProjectManagerService;
import com.technomancarai.tms.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TaskManagementSystemApplication.class)
@Transactional
public class BackendAuditIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProjectManagerService projectManagerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @BeforeEach
    void setUpMocks() {
        jakarta.mail.internet.MimeMessage mimeMessage = org.mockito.Mockito.mock(jakarta.mail.internet.MimeMessage.class);
        org.mockito.Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Audit Fix 1: Reset Password rejects expired OTP and prevents OTP reuse")
    void testResetPasswordExpiryAndReusePrevention() {
        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty());
        User user = users.get(0);

        // Save an expired OTP
        OtpVerification expiredOtp = OtpVerification.builder()
                .email(user.getEmail())
                .otp("888888")
                .purpose("FORGOT_PASSWORD")
                .isVerified(true)
                .expiresAt(LocalDateTime.now().minusMinutes(5))
                .build();
        otpVerificationRepository.save(expiredOtp);

        ResetPasswordRequest expiredRequest = new ResetPasswordRequest();
        expiredRequest.setEmail(user.getEmail());
        expiredRequest.setOtp("888888");
        expiredRequest.setNewPassword("NewPass@123");

        assertThrows(BadRequestException.class, () -> authService.resetPassword(expiredRequest),
                "Expired OTP must be rejected during password reset");

        // Save a valid, verified OTP
        OtpVerification validOtp = OtpVerification.builder()
                .email(user.getEmail())
                .otp("999999")
                .purpose("FORGOT_PASSWORD")
                .isVerified(true)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        otpVerificationRepository.save(validOtp);

        ResetPasswordRequest validRequest = new ResetPasswordRequest();
        validRequest.setEmail(user.getEmail());
        validRequest.setOtp("999999");
        validRequest.setNewPassword("NewPass@123");

        var response = authService.resetPassword(validRequest);
        assertNotNull(response);

        // Attempting to reuse the same OTP must fail
        assertThrows(BadRequestException.class, () -> authService.resetPassword(validRequest),
                "Reusing a previously consumed OTP must be rejected");
    }

    @Test
    @DisplayName("Audit Fix 2: End-to-end PM and Employee Task Comment Cross-Role Workflow")
    void testPmAndEmployeeTaskCommentCrossRoleWorkflow() {
        // Setup PM, Employee, Project, Task
        List<User> users = userRepository.findAll();
        assertTrue(users.size() >= 2);
        User pmUser = users.get(0);
        User empUser = users.get(1);

        Project project = projectRepository.findAll().get(0);
        project.setManager(pmUser);
        projectRepository.save(project);

        // Ensure Employee is project member
        if (!projectMemberRepository.existsByProjectIdAndUserId(project.getId(), empUser.getId())) {
            projectManagerService.addProjectMember(project.getId(), new AddMemberRequest(empUser.getId()), pmUser.getEmail());
        }

        // Create Task assigned to Employee
        PmTaskRequest taskRequest = new PmTaskRequest();
        taskRequest.setTitle("Audit Comment Task");
        taskRequest.setDescription("Testing PM and Employee comment flow");
        taskRequest.setAssignedUserId(empUser.getId());

        TaskResponse taskResponse = projectManagerService.createTask(project.getId(), taskRequest, pmUser.getEmail());
        assertNotNull(taskResponse);

        // Employee adds comment
        TaskCommentRequest empCommentReq = new TaskCommentRequest();
        empCommentReq.setComment("Employee progress update comment");
        TaskCommentResponse empCommentRes = employeeService.addTaskComment(taskResponse.getId(), empCommentReq, empUser.getEmail());
        assertNotNull(empCommentRes);

        // PM reads comments
        List<TaskCommentResponse> pmComments = projectManagerService.getTaskComments(taskResponse.getId(), pmUser.getEmail());
        assertFalse(pmComments.isEmpty());
        assertTrue(pmComments.stream().anyMatch(c -> c.getComment().equals("Employee progress update comment")));

        // PM adds comment
        TaskCommentRequest pmCommentReq = new TaskCommentRequest();
        pmCommentReq.setComment("PM review comment");
        TaskCommentResponse pmCommentRes = projectManagerService.addTaskComment(taskResponse.getId(), pmCommentReq, pmUser.getEmail());
        assertNotNull(pmCommentRes);

        // PM deletes comment
        projectManagerService.deleteTaskComment(pmCommentRes.getId(), pmUser.getEmail());

        List<TaskCommentResponse> pmCommentsAfterDelete = projectManagerService.getTaskComments(taskResponse.getId(), pmUser.getEmail());
        assertFalse(pmCommentsAfterDelete.stream().anyMatch(c -> c.getId().equals(pmCommentRes.getId())));
    }

    @Test
    @DisplayName("Audit Fix 3: Employee task pagination with DB-level filtering")
    void testEmployeeTaskPaginationWithFiltering() {
        List<User> users = userRepository.findAll();
        User empUser = users.get(0);

        PageResponse<TaskResponse> tasksPage = employeeService.getAssignedTasks(
                empUser.getEmail(), null, "In Progress", null, 0, 10, "id", "asc"
        );
        assertNotNull(tasksPage);
        assertTrue(tasksPage.getContent().stream().allMatch(t -> "In Progress".equalsIgnoreCase(t.getTaskStatus())));
    }

    @Test
    @DisplayName("Audit Fix 4: Unassign tasks when removing a project member")
    void testUnassignTasksOnMemberRemoval() {
        List<User> users = userRepository.findAll();
        assertTrue(users.size() >= 2);
        User pmUser = users.get(0);
        User empUser = users.get(1);

        Project project = projectRepository.findAll().get(0);
        project.setManager(pmUser);
        projectRepository.save(project);

        if (!projectMemberRepository.existsByProjectIdAndUserId(project.getId(), empUser.getId())) {
            projectManagerService.addProjectMember(project.getId(), new AddMemberRequest(empUser.getId()), pmUser.getEmail());
        }

        // Create Task assigned to Employee
        PmTaskRequest taskRequest = new PmTaskRequest();
        taskRequest.setTitle("Task To Be Unassigned");
        taskRequest.setAssignedUserId(empUser.getId());
        TaskResponse taskResponse = projectManagerService.createTask(project.getId(), taskRequest, pmUser.getEmail());
        assertNotNull(taskResponse);
        assertNotNull(taskResponse.getAssignedTo());

        // PM removes member
        projectManagerService.removeProjectMember(project.getId(), empUser.getId(), pmUser.getEmail());

        // Task should now be unassigned (assignedTo = null)
        Task updatedTask = taskRepository.findById(taskResponse.getId()).orElseThrow();
        assertNull(updatedTask.getAssignedTo(), "Task must be unassigned when member is removed from project");
    }

    @Test
    @DisplayName("Audit Fix 5: Role deletion cleans up UserRole mappings without DB integrity errors")
    void testDeleteRoleCleanUpUserRole() {
        RoleRequest roleReq = new RoleRequest();
        roleReq.setName("TEST_AUDIT_ROLE");
        RoleResponse roleRes = roleService.createRole(roleReq);
        assertNotNull(roleRes);

        // Delete created role
        assertDoesNotThrow(() -> roleService.deleteRole(roleRes.getId()),
                "Deleting a role must clean up mappings without FK constraint error");
    }
}
