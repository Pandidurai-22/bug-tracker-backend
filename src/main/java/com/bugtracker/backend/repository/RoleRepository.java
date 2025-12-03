// src/main/java/com/bugtracker/backend/repository/RoleRepository.java
package com.bugtracker.backend.repository;

import com.bugtracker.backend.model.ERole;
import com.bugtracker.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
    boolean existsByName(ERole name);
}