package com.bugtracker.backend.controller;

import com.bugtracker.backend.model.Bug;
import com.bugtracker.backend.payload.response.AIAnalysisResponse;
import com.bugtracker.backend.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugs")
public class BugController {

    @Autowired
    private BugService bugService;

    @GetMapping
    public List<Bug> getAllBugs() {
        return bugService.getAllBugs();
    }

    @GetMapping("/{id}")
    public Bug getBugById(@PathVariable Long id) {
        return bugService.getBugById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found"));
    }

    @PostMapping
    public Bug createBug(@RequestBody Bug bug) {
        return bugService.createBug(bug);
    }

    @PutMapping("/{id}")
    public Bug updateBug(@PathVariable Long id, @RequestBody Bug bug) {
        return bugService.updateBug(id, bug);
    }

    @DeleteMapping("/{id}")
    public void deleteBug(@PathVariable Long id) {
        bugService.deleteBug(id);
    }
    
    /**
     * Get AI analysis for bug description (before creating bug)
     */
    @PostMapping("/ai/analyze")
    public ResponseEntity<AIAnalysisResponse> analyzeBug(@RequestBody BugAnalysisRequest request) {
        String fullText = (request.getTitle() != null ? request.getTitle() + " " : "") + 
                         (request.getDescription() != null ? request.getDescription() : "");
        
        AIAnalysisResponse analysis = bugService.getAIAnalysis(fullText);
        
        if (analysis != null) {
            return ResponseEntity.ok(analysis);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Find similar bugs
     */
    @PostMapping("/ai/similar")
    public ResponseEntity<List<AIAnalysisResponse.SimilarBugInfo>> findSimilarBugs(
            @RequestBody BugAnalysisRequest request,
            @RequestParam(defaultValue = "5") int limit) {
        String fullText = (request.getTitle() != null ? request.getTitle() + " " : "") + 
                         (request.getDescription() != null ? request.getDescription() : "");
        
        List<AIAnalysisResponse.SimilarBugInfo> similarBugs = 
            bugService.findSimilarBugs(fullText, limit);
        
        return ResponseEntity.ok(similarBugs);
    }
    
    // Request DTO for AI analysis
    static class BugAnalysisRequest {
        private String title;
        private String description;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
