package com.technomancarai.tms;

import com.technomancarai.tms.dto.request.AddMemberRequest;
import com.technomancarai.tms.dto.request.AssignTaskRequest;
import com.technomancarai.tms.dto.request.PmTaskRequest;
import com.technomancarai.tms.dto.request.ProjectRequest;
import com.technomancarai.tms.dto.request.SetDueDateRequest;
import com.technomancarai.tms.dto.request.UpdateTaskPriorityPmRequest;
import com.technomancarai.tms.dto.request.UpdateTaskStatusEmployeeRequest;
import com.technomancarai.tms.dto.response.PageResponse;
import com.technomancarai.tms.dto.response.ProjectResponse;
import com.technomancarai.tms.dto.response.TaskResponse;
import com.technomancarai.tms.entity.Task;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.exception.BadRequestException;
import com.technomancarai.tms.repository.ProjectMemberRepository;
import com.technomancarai.tms.repository.ProjectRepository;
import com.technomancarai.tms.repository.TaskRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.service.AdminProjectService;
import com.technomancarai.tms.service.EmployeeService;
import com.technomancarai.tms.service.ProjectManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TaskManagementSystemApplication.class)
@Transactional
public class TaskAssignmentCrossRoleTest {

    @Autowired
    private AdminProjectService adminProjectService;

    @Autowired
    private ProjectManagerService projectManagerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private TaskRepository taskRepository;

    @MockitoBean
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @BeforeEach
    void setUpMocks() {
        jakarta.mail.internet.MimeMessage mimeMessage = org.mockito.Mockito.mock(jakarta.mail.internet.MimeMessage.class);
        org.mockito.Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Complete Cross-Role Task Assignment & Consistency Lifecycle")
    void testCompleteCrossRoleTaskAssignmentLifecycle() {
        // 1. Setup Admin, PM, and two Employees
        List<User> users = userRepository.findAll();
        assertTrue(users.size() >= 4, "Integration test requires at least 4 seeded users");

        User adminUser = users.get(0);
        User pmUser = users.get(1);
        User empUser1 = users.get(2);
        User empUser2 = users.get(3);

        // 2. Admin creates a project
        ProjectRequest projectReq = new ProjectRequest();
        projectReq.setProjectName("Cross Role Task System");
        projectReq.setProjectCode("CRTS-2026-" + System.currentTimeMillis());
        projectReq.setDescription("Testing complete cross-role task assignment consistency");
        projectReq.setManagerId(pmUser.getId());

        ProjectResponse projectRes = adminProjectService.createProject(projectReq, adminUser.getEmail());
        assertNotNull(projectRes);
        Long projectId = projectRes.getId();

        // 3. PM logs in and verifies assigned project list
        PageResponse<ProjectResponse> pmProjects = projectManagerService.getAssignedProjects(
                pmUser.getEmail(), 0, 10, "id", "asc"
        );
        assertTrue(pmProjects.getContent().stream().anyMatch(p -> p.getId().equals(projectId)),
                "PM must see project assigned by Admin");

        // 4. PM adds Employee 1 and Employee 2 as project members
        projectManagerService.addProjectMember(projectId, new AddMemberRequest(empUser1.getId()), pmUser.getEmail());
        projectManagerService.addProjectMember(projectId, new AddMemberRequest(empUser2.getId()), pmUser.getEmail());

        assertTrue(projectMemberRepository.existsByProjectIdAndUserId(projectId, empUser1.getId()));
        assertTrue(projectMemberRepository.existsByProjectIdAndUserId(projectId, empUser2.getId()));

        // 5. PM creates a task assigned to Employee 1
        PmTaskRequest createReq = new PmTaskRequest();
        createReq.setTitle("Front End Development");
        createReq.setDescription("Implement Task Workspace UI");
        createReq.setStartDate(LocalDate.now());
        createReq.setDueDate(LocalDate.now().plusDays(7));
        createReq.setEstimatedHours(BigDecimal.valueOf(20));
        createReq.setAssignedUserId(empUser1.getId()); // Pass assignedUserId

        TaskResponse createdTask = projectManagerService.createTask(projectId, createReq, pmUser.getEmail());
        assertNotNull(createdTask);
        assertNotNull(createdTask.getAssignedTo(), "TaskResponse.assignedTo object must not be null");
        assertEquals(empUser1.getId(), createdTask.getAssignedTo().getId());
        assertEquals(empUser1.getId(), createdTask.getAssignedUserId(), "TaskResponse.assignedUserId alias must match");
        assertEquals(empUser1.getId(), createdTask.getAssignedToId(), "TaskResponse.assignedToId alias must match");

        // 6. Verify Database Persistence
        Task dbTask = taskRepository.findById(createdTask.getId()).orElseThrow();
        assertNotNull(dbTask.getAssignedTo(), "Database row must persist assigned_to foreign key");
        assertEquals(empUser1.getId(), dbTask.getAssignedTo().getId());

        // 7. PM fetches task list (`GET /api/v1/pm/projects/{projectId}/tasks`)
        PageResponse<TaskResponse> pmTaskList = projectManagerService.getProjectTasks(
                projectId, null, 0, 10, "id", "asc", pmUser.getEmail()
        );
        assertFalse(pmTaskList.getContent().isEmpty());
        TaskResponse fetchedPmTask = pmTaskList.getContent().stream()
                .filter(t -> t.getId().equals(createdTask.getId()))
                .findFirst().orElseThrow();
        assertNotNull(fetchedPmTask.getAssignedTo());
        assertEquals(empUser1.getId(), fetchedPmTask.getAssignedTo().getId());
        assertNotNull(fetchedPmTask.getAssignee(), "TaskResponse.assignee alias must not be null");

        // 8. Employee 1 logs in and sees task in My Tasks (`GET /api/v1/employee/tasks`)
        PageResponse<TaskResponse> empTaskList = employeeService.getAssignedTasks(
                empUser1.getEmail(), null, null, null, 0, 10, "id", "asc"
        );
        assertTrue(empTaskList.getContent().stream().anyMatch(t -> t.getId().equals(createdTask.getId())),
                "Employee 1 must see task assigned to them in My Tasks");

        // 9. Employee 1 updates task status to IN_PROGRESS
        UpdateTaskStatusEmployeeRequest empStatusReq = new UpdateTaskStatusEmployeeRequest();
        empStatusReq.setStatusName("In Progress");
        TaskResponse updatedStatusTask = employeeService.updateTaskStatus(createdTask.getId(), empStatusReq, empUser1.getEmail());
        assertEquals("In Progress", updatedStatusTask.getTaskStatus());
        assertNotNull(updatedStatusTask.getAssignedTo(), "Assignee must remain intact after status update");

        // 10. PM checks task list again - verify assignee is STILL Employee 1
        TaskResponse pmCheckedTask1 = projectManagerService.getTaskById(createdTask.getId(), pmUser.getEmail());
        assertNotNull(pmCheckedTask1.getAssignedTo());
        assertEquals(empUser1.getId(), pmCheckedTask1.getAssignedTo().getId());

        // 11. PM changes task priority
        UpdateTaskPriorityPmRequest priorityReq = new UpdateTaskPriorityPmRequest();
        priorityReq.setTaskPriorityId(1L); // High/Normal
        TaskResponse updatedPriorityTask = projectManagerService.changeTaskPriority(createdTask.getId(), priorityReq, pmUser.getEmail());
        assertNotNull(updatedPriorityTask.getAssignedTo(), "Assignee must remain intact after priority update");

        // 12. PM changes task due date
        SetDueDateRequest dueDateReq = new SetDueDateRequest();
        dueDateReq.setDueDate(LocalDate.now().plusDays(10));
        TaskResponse updatedDueDateTask = projectManagerService.setTaskDueDate(createdTask.getId(), dueDateReq, pmUser.getEmail());
        assertNotNull(updatedDueDateTask.getAssignedTo(), "Assignee must remain intact after due date update");

        // 13. PM updates task details (`updateTask`)
        PmTaskRequest updateReq = new PmTaskRequest();
        updateReq.setTitle("Front End Development - Phase 1");
        updateReq.setAssignedUserId(empUser1.getId()); // Keep Employee 1
        TaskResponse updatedDetailsTask = projectManagerService.updateTask(createdTask.getId(), updateReq, pmUser.getEmail());
        assertEquals("Front End Development - Phase 1", updatedDetailsTask.getTitle());
        assertNotNull(updatedDetailsTask.getAssignedTo());
        assertEquals(empUser1.getId(), updatedDetailsTask.getAssignedTo().getId());

        // 14. PM reassigns task to Employee 2 (`PUT /api/v1/pm/tasks/{taskId}/assign`)
        AssignTaskRequest reassignReq = new AssignTaskRequest();
        reassignReq.setAssignedUserId(empUser2.getId());
        TaskResponse reassignedTask = projectManagerService.assignTask(createdTask.getId(), reassignReq, pmUser.getEmail());
        assertNotNull(reassignedTask.getAssignedTo());
        assertEquals(empUser2.getId(), reassignedTask.getAssignedTo().getId(), "Task must now be assigned to Employee 2");

        // Verify DB update for reassign
        Task reassignDbTask = taskRepository.findById(createdTask.getId()).orElseThrow();
        assertEquals(empUser2.getId(), reassignDbTask.getAssignedTo().getId());

        // 15. Attempt to assign task to a non-project member -> Must throw BadRequestException
        User nonMember = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(empUser1.getId()) && !u.getId().equals(empUser2.getId()) && !u.getId().equals(pmUser.getId()))
                .findFirst().orElseThrow();

        AssignTaskRequest invalidAssignReq = new AssignTaskRequest();
        invalidAssignReq.setAssignedUserId(nonMember.getId());
        assertThrows(BadRequestException.class, () -> projectManagerService.assignTask(createdTask.getId(), invalidAssignReq, pmUser.getEmail()),
                "Assigning task to a non-project member must throw BadRequestException");

        // 16. PM unassigns task (assignedUserId = 0) -> Task becomes unassigned
        AssignTaskRequest unassignReq = new AssignTaskRequest();
        unassignReq.setAssignedUserId(0L);
        TaskResponse unassignedTask = projectManagerService.assignTask(createdTask.getId(), unassignReq, pmUser.getEmail());
        assertNull(unassignedTask.getAssignedTo(), "Task must become unassigned when assignedUserId <= 0");
    }
}
