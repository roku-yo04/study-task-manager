package com.mta.studytaskmanager.repository;

import com.mta.studytaskmanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByUserId(Long userId);

    Optional<Category> findByUserIdAndName(Long userId, String name);
    Boolean existsByUserIdAndName(Long userId, String name);

}
