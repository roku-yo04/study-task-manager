package com.mta.studytaskmanager.modules.task.entity;
import com.mta.studytaskmanager.modules.category.entity.Category;
import com.mta.studytaskmanager.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 255)
    private String title;
    @Column(length = 1000)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskPriority priority;
    // hạn chót: chỉ cần biết ngày giờ , k cần rõ chi tiết mấy phút,giây.
    @Column(name = "due_date", nullable = true)
    private LocalDate dueDate;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // khi load task thì k cần load category, nên để LAZY,
    // chỉ load thêm category khi cần thiết, tránh việc load nhiều dữ liệu.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // maping với category_id trong bảng task, để biết task thuộc category nào.
    private Category category;
}
