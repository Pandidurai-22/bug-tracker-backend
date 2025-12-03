// package com.bugtracker.backend.controller;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
// import java.util.HashMap;
// import java.util.Map;

// @RestController
// public class TestController {

//     @GetMapping("/test")
//     public String test() {
//         return "It works!";
//     }
    
//     @GetMapping("/health")
//     public ResponseEntity<Map<String, String>> healthCheck() {
//         Map<String, String> response = new HashMap<>();
//         response.put("status", "UP");
//         response.put("service", "Bug Tracker Backend");
//         return ResponseEntity.ok(response);
//     }
    
//     @GetMapping("/api/version")
//     public ResponseEntity<Map<String, String>> getVersion() {
//         Map<String, String> response = new HashMap<>();
//         response.put("version", "1.0.0");
//         response.put("build", System.getenv("RENDER_GIT_COMMIT") != null ? 
//                           System.getenv("RENDER_GIT_COMMIT").substring(0, 7) : "local");
//         return ResponseEntity.ok(response);
//     }
// }


// src/main/java/com/bugtracker/backend/controllers/TestController.java
package com.bugtracker.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('TESTER') or hasRole('DEVELOPER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/tester")
    @PreAuthorize("hasRole('TESTER')")
    public String testerAccess() {
        return "Tester Board.";
    }

    @GetMapping("/developer")
    @PreAuthorize("hasRole('DEVELOPER')")
    public String developerAccess() {
        return "Developer Board.";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public String managerAccess() {
        return "Manager Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}