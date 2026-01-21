// src/main/java/com/bugtracker/backend/config/DataInitializer.java
package com.bugtracker.backend.config;

import com.bugtracker.backend.model.ERole;
import com.bugtracker.backend.model.Role;
import com.bugtracker.backend.model.User;
import com.bugtracker.backend.repository.RoleRepository;
import com.bugtracker.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, 
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create roles if they don't exist
        for (ERole role : ERole.values()) {
            if (!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(role));
            }
        }

        // Create test user if it doesn't exist
        if (!userRepository.existsByUsername("testing")) {
            User testUser = new User("testing", "testing@bugtracker.com", 
                                    passwordEncoder.encode("testing123"));
            
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
            roles.add(userRole);
            testUser.setRoles(roles);
            
            userRepository.save(testUser);
            System.out.println("Test user 'testing' created successfully!");
        }
    }
}