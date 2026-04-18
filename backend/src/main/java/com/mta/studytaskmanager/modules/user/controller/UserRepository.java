package com.mta.studytaskmanager.modules.user.controller;

import com.mta.studytaskmanager.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // register
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // login / load user
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}