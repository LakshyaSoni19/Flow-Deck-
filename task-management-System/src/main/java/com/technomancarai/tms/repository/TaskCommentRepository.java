package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    List<TaskComment> findByTaskIdOrderByCommentDateDesc(Long taskId);

    long countByTaskId(Long taskId);
}
