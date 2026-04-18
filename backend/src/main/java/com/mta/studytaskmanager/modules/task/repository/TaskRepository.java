package com.mta.studytaskmanager.modules.task.repository;

import com.mta.studytaskmanager.modules.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Tìm đúng 1 task theo id và user
    Optional<Task> findByIdAndUserId(Long id, Long userId);

    // List task của 1 user
    Page<Task> findByUserId(Long userId, Pageable pageable);

    // List task theo category của user
    Page<Task> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    // List task theo status của user
    Page<Task> findByUserIdAndStatus(Long userId, TaskStatus status, Pageable pageable);

    // Search title trong phạm vi user
    Page<Task> findByUserIdAndTitleContainingIgnoreCase(Long userId, String keyword, Pageable pageable);

    // List overdue task của user
    Page<Task> findByUserIdAndDueDateBefore(Long userId, LocalDate dueDate, Pageable pageable);

    // Xóa trực tiếp theo id + user (optional)
    void deleteByIdAndUserId(Long id, Long userId);
}