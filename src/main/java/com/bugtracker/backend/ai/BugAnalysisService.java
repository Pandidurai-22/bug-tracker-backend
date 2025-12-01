package com.bugtracker.backend.ai;

import java.util.List;
import java.util.Map;

public interface BugAnalysisService {
    
    /**
     * Predicts the priority of a bug based on its description
     * @param bugDescription The description of the bug
     * @return Predicted priority (e.g., "LOW", "MEDIUM", "HIGH", "CRITICAL")
     */
    String predictPriority(String bugDescription);
    
    /**
     * Predicts the severity of a bug based on its description
     * @param bugDescription The description of the bug
     * @return Predicted severity (e.g., "ENHANCEMENT", "MINOR", "MAJOR", "CRITICAL")
     */
    String predictSeverity(String bugDescription);
    
    /**
     * Extracts key entities from the bug description
     * @param bugDescription The description of the bug
     * @return Map of entity types to their values
     */
    Map<String, List<String>> extractEntities(String bugDescription);
    
    /**
     * Finds similar past bugs
     * @param bugDescription The description of the current bug
     * @param limit Maximum number of similar bugs to return
     * @return List of similar bug IDs with similarity scores
     */
    List<SimilarBug> findSimilarBugs(String bugDescription, int limit);
    
    /**
     * Suggests potential solutions based on similar resolved bugs
     * @param bugDescription The description of the bug
     * @return List of potential solutions
     */
    List<String> suggestSolutions(String bugDescription);
    
    /**
     * Class to hold similar bug information
     */
    class SimilarBug {
        private final Long bugId;
        private final double similarityScore;
        
        public SimilarBug(Long bugId, double similarityScore) {
            this.bugId = bugId;
            this.similarityScore = similarityScore;
        }
        
        public Long getBugId() {
            return bugId;
        }
        
        public double getSimilarityScore() {
            return similarityScore;
        }
    }
}
