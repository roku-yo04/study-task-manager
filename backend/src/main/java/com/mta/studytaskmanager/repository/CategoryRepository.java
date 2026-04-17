package com.mta.studytaskmanager.repository;

import com.mta.studytaskmanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Kiểm tra trong cùng 1 user đã có category trùng tên chưa
    boolean existsByNameAndUserId(String name, Long userId);

    // Đếm số category của 1 user
    long countByUserId(Long userId);

    // Lấy danh sách category của user
    List<Category> findByUserId(Long userId);

    // Tìm category theo id và user
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    // Optional: xóa trực tiếp theo id và user
    void deleteByIdAndUserId(Long id, Long userId);
}