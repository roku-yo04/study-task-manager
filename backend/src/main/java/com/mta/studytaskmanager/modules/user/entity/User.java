package com.mta.studytaskmanager.modules.user.entity;

import com.mta.studytaskmanager.core.common.BaseEntity;
import com.mta.studytaskmanager.modules.category.entity.Category;
import com.mta.studytaskmanager.modules.role.entity.Role;
import com.mta.studytaskmanager.modules.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "users")

public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // cho phép tự động tăng giá trị của id và  sử dụng chiến lược tăng dần (IDENTITY) để tạo giá trị id mới khi thêm bản ghi mới vào bảng users
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    // đảm bảo username không được null, phải là duy nhất và có độ dài tối đa 50 ký tự
    // -> trường hợp userlogin trùng nhau sẽ bị lỗi, không cho phép tạo mới

    @Column(nullable = false,unique = true,length = 100)
    private String email;

    @Column(name = "password_hash",nullable = false,length = 255)
    private String passwordHash;

    // jpa sẽ mapping trường displayName với cột display_name
    // trong database thay vì displayName vì theo mặc định jpa sẽ mapping theo tên trường trong class
    @Column(name = "display_name",length = 100)
    private String displayName;

    @ColumnDefault("1") // Khi câu lệnh SQL INSERT thực thi tại DB.
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    // mặc định là true, có nghĩa là tài khoản sẽ được kích hoạt khi tạo mới. Nếu muốn vô hiệu hóa tài khoản
    // Check khi login ,gọi api + xử lí task, category thì sẽ check trường isActive nếu false thì sẽ trả về lỗi tài khoản bị vô hiệu hóa

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> categories = new HashSet<>();
    // set để không trùng lặp category của user.

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER) // khi load user thì sẽ tự động load các role liên quan đến user đó luôn, tránh việc phải gọi thêm 1 câu query để lấy role của user
    // LAZY: Chỉ tải khi bạn thực sự gọi đến (ví dụ: user.getRoles()).
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles  = new HashSet<>();
    // set để không trùng lặp role của user
}
