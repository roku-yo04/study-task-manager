package com.mta.studytaskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // cho db biết đây là một enum và lưu trữ dưới dạng chuỗi, không phải số nguyên
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true,length = 50)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private java.util.Set<User> users;


}
