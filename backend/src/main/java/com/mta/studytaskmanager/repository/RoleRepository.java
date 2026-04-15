package com.mta.studytaskmanager.repository;

import com.mta.studytaskmanager.entity.Role;
import com.mta.studytaskmanager.enums.RoleName;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository {

    Optional<Role> findByName(RoleName name);
}
