package com.mta.studytaskmanager.repository;

import com.mta.studytaskmanager.entity.Task;
import com.mta.studytaskmanager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);
    Page<Task> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    Page<Task> findByUserIdAndCategoryIdAndStatus(Long userId, Long category);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
            "AND LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Task> searchByTitle(@Param("userId") Long userId,
                             @Param("keyword") String keyword,
                             Pageable pageable);

    List<Task> findByUserIdAndDueDateBeforeAndStatusNot(
            Long userId,
            LocalDate date,
            TaskStatus status
    );

    Long countByUserIdAndStatus(Long userId, TaskStatus status);

}
