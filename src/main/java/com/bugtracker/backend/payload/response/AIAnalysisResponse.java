package com.bugtracker.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisResponse {
    private String severity;
    private String priority;
    private List<String> tags;
    private String summary;
    private List<Double> embedding;
    private List<SimilarBugInfo> similarBugs;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimilarBugInfo {
        private Long bugId;
        private String title;
        private Double similarityScore;
    }
}

