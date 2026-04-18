package com.mta.studytaskmanager.modules.role.repository;

import com.mta.studytaskmanager.modules.role.enums.RoleName;
import com.mta.studytaskmanager.modules.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // can khi: roleRepository.findByRoleName(ROLE_USER)
    Optional<Role> findByName(RoleName name);
}



