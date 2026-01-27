package com.bugtracker.backend.service;

import com.bugtracker.backend.ai.AIServiceClient;
import com.bugtracker.backend.model.Bug;
import com.bugtracker.backend.payload.response.AIAnalysisResponse;
import com.bugtracker.backend.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BugService {

    @Autowired
    private BugRepository bugRepository;
    
    @Autowired(required = false)
    private AIServiceClient aiServiceClient;
    
    @Value("${ai.enabled:false}")
    private boolean aiEnabled;

    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    public Optional<Bug> getBugById(Long id) {
        return bugRepository.findById(id);
    }

    public Bug createBug(Bug bug) {
        bug.setCreatedAt(LocalDateTime.now());
        
        // Auto-fill status if not provided
        if (bug.getStatus() == null || bug.getStatus().isEmpty()) {
            bug.setStatus("Open");
        }
        
        // AI Analysis (if enabled)
        if (aiEnabled && aiServiceClient != null && bug.getDescription() != null) {
            try {
                String fullText = (bug.getTitle() != null ? bug.getTitle() + " " : "") + bug.getDescription();
                AIAnalysisResponse aiAnalysis = getAIAnalysis(fullText);
                
                if (aiAnalysis != null) {
                    // Set AI-predicted values if not manually set
                    if (bug.getSeverity() == null || bug.getSeverity().isEmpty()) {
                        bug.setSeverity(aiAnalysis.getSeverity());
                    }
                    if (bug.getPriority() == null || bug.getPriority().isEmpty()) {
                        bug.setPriority(aiAnalysis.getPriority());
                    }
                    if (bug.getTags() == null || bug.getTags().isEmpty()) {
                        bug.setTags(String.join(", ", aiAnalysis.getTags()));
                    }
                    bug.setAiAnalyzed(true);
                }
            } catch (Exception e) {
                // Log but don't fail bug creation if AI fails
                System.err.println("AI analysis failed: " + e.getMessage());
                bug.setAiAnalyzed(false);
            }
        }
        
        return bugRepository.save(bug);
    }

    public Bug updateBug(Long id, Bug updatedBug) {
        return bugRepository.findById(id)
            .map(existing -> {
                existing.setTitle(updatedBug.getTitle());
                existing.setDescription(updatedBug.getDescription());
                existing.setPriority(updatedBug.getPriority());
                existing.setSeverity(updatedBug.getSeverity());
                existing.setTags(updatedBug.getTags());
                existing.setStatus(updatedBug.getStatus());
                existing.setAssignee(updatedBug.getAssignee());
                existing.setDueDate(updatedBug.getDueDate());
                return bugRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("Bug not found"));
    }

    public void deleteBug(Long id) {
        bugRepository.deleteById(id);
    }
    
    /**
     * Get AI analysis for bug description
     */
    public AIAnalysisResponse getAIAnalysis(String bugDescription) {
        if (!aiEnabled) {
            System.err.println("AI is disabled in configuration");
            return null;
        }
        
        if (aiServiceClient == null) {
            System.err.println("AI Service Client is null - check if AI service is available");
            return null;
        }
        
        try {
            System.out.println("Calling AI service with description: " + bugDescription.substring(0, Math.min(50, bugDescription.length())));
            
            // Call comprehensive analysis endpoint
            AIServiceClient.ComprehensiveAnalysisResponse response = 
                aiServiceClient.getComprehensiveAnalysis(bugDescription);
            
            if (response != null) {
                System.out.println("AI Service Response received - Tags: " + 
                    (response.getTags() != null ? response.getTags() : "null") + 
                    ", Severity: " + response.getSeverity() + 
                    ", Priority: " + response.getPriority());
                
                AIAnalysisResponse analysis = new AIAnalysisResponse();
                analysis.setSeverity(response.getSeverity());
                analysis.setPriority(response.getPriority());
                // Ensure tags list is not null
                analysis.setTags(response.getTags() != null ? response.getTags() : Collections.emptyList());
                analysis.setSummary(response.getSummary());
                analysis.setEmbedding(response.getEmbedding());
                analysis.setConfidence(response.getConfidence());
                analysis.setModelVersion(response.getModelVersion());
                // Mark for review if confidence is low
                if (response.getConfidence() != null && response.getConfidence() < 0.6) {
                    analysis.setNeedsReview(true);
                } else {
                    analysis.setNeedsReview(false);
                }
                
                // Find similar bugs
                List<AIAnalysisResponse.SimilarBugInfo> similarBugs = findSimilarBugs(bugDescription, 5);
                analysis.setSimilarBugs(similarBugs);
                
                return analysis;
            } else {
                System.err.println("AI Service returned null response");
            }
        } catch (Exception e) {
            System.err.println("Error getting AI analysis: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Find similar bugs using AI embeddings
     */
    public List<AIAnalysisResponse.SimilarBugInfo> findSimilarBugs(String bugDescription, int limit) {
        if (!aiEnabled || aiServiceClient == null) {
            return Collections.emptyList();
        }
        
        try {
            // Get embedding for current bug
            List<Double> currentEmbedding = aiServiceClient.generateEmbedding(bugDescription);
            if (currentEmbedding == null || currentEmbedding.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Get all bugs with descriptions
            List<Bug> allBugs = bugRepository.findAll();
            
            // Calculate similarity scores
            List<AIAnalysisResponse.SimilarBugInfo> similarBugs = new ArrayList<>();
            
            for (Bug bug : allBugs) {
                if (bug.getDescription() == null || bug.getDescription().isEmpty()) {
                    continue;
                }
                
                try {
                    List<Double> bugEmbedding = aiServiceClient.generateEmbedding(
                        (bug.getTitle() != null ? bug.getTitle() + " " : "") + bug.getDescription()
                    );
                    
                    if (bugEmbedding != null && !bugEmbedding.isEmpty()) {
                        double similarity = cosineSimilarity(currentEmbedding, bugEmbedding);
                        
                        if (similarity > 0.7) {  // Threshold for similarity
                            AIAnalysisResponse.SimilarBugInfo similarBug = 
                                new AIAnalysisResponse.SimilarBugInfo();
                            similarBug.setBugId(bug.getId());
                            similarBug.setTitle(bug.getTitle());
                            similarBug.setSimilarityScore(similarity);
                            similarBugs.add(similarBug);
                        }
                    }
                } catch (Exception e) {
                    // Skip this bug if embedding fails
                    continue;
                }
            }
            
            // Sort by similarity and return top N
            similarBugs.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));
            return similarBugs.stream().limit(limit).collect(Collectors.toList());
            
        } catch (Exception e) {
            System.err.println("Error finding similar bugs: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Calculate cosine similarity between two vectors
     */
    private double cosineSimilarity(List<Double> vecA, List<Double> vecB) {
        if (vecA.size() != vecB.size()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < vecA.size(); i++) {
            dotProduct += vecA.get(i) * vecB.get(i);
            normA += vecA.get(i) * vecA.get(i);
            normB += vecB.get(i) * vecB.get(i);
        }
        
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
