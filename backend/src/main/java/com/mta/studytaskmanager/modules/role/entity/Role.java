package com.mta.studytaskmanager.modules.role.entity;
import com.mta.studytaskmanager.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "roles")
public class Role {
    @Id
    // enum la static, khi set thì user.setRole(Role.ADMIN) chứ không phải user.setRole("ADMIN")
    // cho db biết đây là một enum và lưu trữ dưới dạng chuỗi, không phải số nguyên
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name",nullable = false, unique = true, length = 50)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();


}
