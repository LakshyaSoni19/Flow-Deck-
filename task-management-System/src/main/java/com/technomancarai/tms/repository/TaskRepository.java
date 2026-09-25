package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByProjectIdAndTitleContainingIgnoreCase(Long projectId, String title, Pageable pageable);

    List<Task> findByAssignedToId(Long userId);

    Page<Task> findByAssignedToEmail(String email, Pageable pageable);

    Page<Task> findByAssignedToEmailAndTitleContainingIgnoreCase(String email, String title, Pageable pageable);

    List<Task> findByCreatedByUserId(Long userId);

    List<Task> findByTaskStatusId(Long statusId);

    List<Task> findByTaskPriorityId(Long priorityId);

    List<Task> findByProjectIdAndTaskStatusNameIgnoreCase(Long projectId, String statusName);

    List<Task> findByProjectIdAndTaskPriorityNameIgnoreCase(Long projectId, String priorityName);

    List<Task> findByProjectIdAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(Long projectId, LocalDate date, String statusName);

    List<Task> findByAssignedToEmailAndTaskStatusNameIgnoreCase(String email, String statusName);

    List<Task> findByAssignedToEmailAndTaskPriorityNameIgnoreCase(String email, String priorityName);

    List<Task> findByAssignedToEmailAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(String email, LocalDate date, String statusName);

    List<Task> findTop5ByAssignedToEmailAndDueDateAfterOrderByDueDateAsc(String email, LocalDate date);

    List<Task> findTop5ByProjectIdOrderByUpdatedAtDesc(Long projectId);

    long countByProjectId(Long projectId);

    long countByProjectIdAndTaskStatusId(Long projectId, Long statusId);

    long countByProjectIdAndTaskStatusNameIgnoreCase(Long projectId, String statusName);

    long countByProjectIdAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(Long projectId, LocalDate date, String statusName);

    long countByProjectIdAndAssignedToId(Long projectId, Long userId);

    long countByProjectIdAndAssignedToIdAndTaskStatusNameIgnoreCase(Long projectId, Long userId, String statusName);

    long countByProjectIdAndAssignedToIdAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(Long projectId, Long userId, LocalDate date, String statusName);

    long countByAssignedToEmail(String email);

    long countByAssignedToEmailAndTaskStatusNameIgnoreCase(String email, String statusName);

    long countByAssignedToEmailAndDueDateBeforeAndTaskStatusNameNotIgnoreCase(String email, LocalDate date, String statusName);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM Task t " +
            "LEFT JOIN t.assignedTo u " +
            "LEFT JOIN t.taskStatus ts " +
            "LEFT JOIN t.taskPriority tp " +
            "WHERE LOWER(u.email) = LOWER(:email) " +
            "AND (:search IS NULL OR :search = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:status IS NULL OR :status = '' OR LOWER(ts.name) = LOWER(:status)) " +
            "AND (:priority IS NULL OR :priority = '' OR LOWER(tp.name) = LOWER(:priority))")
    Page<Task> findByAssignedToEmailWithFilters(
            @org.springframework.data.repository.query.Param("email") String email,
            @org.springframework.data.repository.query.Param("search") String search,
            @org.springframework.data.repository.query.Param("status") String status,
            @org.springframework.data.repository.query.Param("priority") String priority,
            Pageable pageable
    );
}

