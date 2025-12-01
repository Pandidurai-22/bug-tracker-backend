package com.bugtracker.backend.controller;

import com.bugtracker.backend.ai.BugAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIAnalysisController {

    private final BugAnalysisService bugAnalysisService;

    @Autowired
    public AIAnalysisController(BugAnalysisService bugAnalysisService) {
        this.bugAnalysisService = bugAnalysisService;
    }

    @PostMapping("/analyze/priority")
    public ResponseEntity<String> predictPriority(@RequestParam String description) {
        String priority = bugAnalysisService.predictPriority(description);
        return ResponseEntity.ok(priority);
    }

    @PostMapping("/analyze/severity")
    public ResponseEntity<String> predictSeverity(@RequestParam String description) {
        String severity = bugAnalysisService.predictSeverity(description);
        return ResponseEntity.ok(severity);
    }

    @PostMapping("/analyze/entities")
    public ResponseEntity<Map<String, List<String>>> extractEntities(@RequestParam String description) {
        Map<String, List<String>> entities = bugAnalysisService.extractEntities(description);
        return ResponseEntity.ok(entities);
    }

    @PostMapping("/analyze/similar")
    public ResponseEntity<List<BugAnalysisService.SimilarBug>> findSimilarBugs(
            @RequestParam String description,
            @RequestParam(defaultValue = "5") int limit) {
        List<BugAnalysisService.SimilarBug> similarBugs = 
            bugAnalysisService.findSimilarBugs(description, limit);
        return ResponseEntity.ok(similarBugs);
    }

    @PostMapping("/suggest/solutions")
    public ResponseEntity<List<String>> suggestSolutions(@RequestParam String description) {
        List<String> solutions = bugAnalysisService.suggestSolutions(description);
        return ResponseEntity.ok(solutions);
    }
}
