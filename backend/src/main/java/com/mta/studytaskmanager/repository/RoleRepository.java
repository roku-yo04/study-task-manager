package com.mta.studytaskmanager.repository;

import com.mta.studytaskmanager.entity.Role;
import com.mta.studytaskmanager.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // can khi: roleRepository.findByRoleName(ROLE_USER)
    Optional<Role> findByName(RoleName name);
}



