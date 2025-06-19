package com.bugtracker.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "It works!";
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Bug Tracker Backend");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/version")
    public ResponseEntity<Map<String, String>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("version", "1.0.0");
        response.put("build", System.getenv("RENDER_GIT_COMMIT") != null ? 
                          System.getenv("RENDER_GIT_COMMIT").substring(0, 7) : "local");
        return ResponseEntity.ok(response);
    }
}
