package com.mta.studytaskmanager.core.common;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
/* Đây là class cha chứa mapping dùng chung. truyền field cho class con , k phải entity
* nên Jpa sẽ k tạo bảng riêng cho nó như entity.
*/
@MappedSuperclass
@Getter
@Setter
// khi entity đc tạo,save -> gọi auditing để tự động fill cho createdAt, updatedAt
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    // để mọi entity kế thừa, có thể thêm các trường chung như createdAt, updatedAt, id, ...
    // tránh việt lặp lại code ở các entity khác nhau, giúp code gọn gàng hơn.

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
