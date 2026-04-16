package com.mta.studytaskmanager.entity.WriteAgain;

import com.mta.studytaskmanager.entity.Task;
import com.mta.studytaskmanager.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "name"})})
public class CategoryTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false,length = 7)
    private String color = "#3357FF";

    @Column(name = "created_at" ,nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false,name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // DB: foreign key còn java: object
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // "category" là tên trường trong class Task để ánh xạ với Category
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();
}
