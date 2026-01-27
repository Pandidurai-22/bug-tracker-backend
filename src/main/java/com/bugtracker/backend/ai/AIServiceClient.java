package com.bugtracker.backend.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

/**
 * HTTP Client for communicating with Python AI microservice
 * Implements microservices architecture pattern for AI/ML capabilities
 */
@Component
public class AIServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(AIServiceClient.class);
    private final RestTemplate restTemplate;
    private final String aiServiceUrl;
    private final int timeout;
    
    public AIServiceClient(
            @Value("${ai.service.url:http://localhost:8000}") String aiServiceUrl,
            @Value("${ai.service.timeout:5000}") int timeout) {
        this.aiServiceUrl = aiServiceUrl;
        this.timeout = timeout;
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Request DTOs for AI service
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class BugRequest {
        private String text;
        private String task;
        
        public BugRequest(String text, String task) {
            this.text = text;
            this.task = task;
        }
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ComprehensiveAnalysisRequest {
        private String text;
        
        public ComprehensiveAnalysisRequest(String text) {
            this.text = text;
        }
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AnalysisResponse {
        private String result;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ComprehensiveAnalysisResponse {
        private String severity;
        private String priority;
        private List<String> tags;
        private String summary;
        private List<Double> embedding;
        private Double confidence;  // ML model confidence score
        private String modelVersion;  // Model version for tracking
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class EmbeddingResponse {
        private List<Double> embedding;
    }
    
    /**
     * Check if AI service is available
     */
    public boolean isServiceAvailable() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                aiServiceUrl + "/health", Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn("AI service health check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Predict priority using Hugging Face model
     */
    public String predictPriority(String bugDescription) {
        try {
            BugRequest request = new BugRequest(bugDescription, "predict_priority");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<BugRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<AnalysisResponse> response = restTemplate.postForEntity(
                aiServiceUrl + "/analyze",
                entity,
                AnalysisResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getResult();
            }
        } catch (Exception e) {
            logger.error("Error calling AI service for priority prediction: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Predict severity using Hugging Face model
     */
    public String predictSeverity(String bugDescription) {
        try {
            BugRequest request = new BugRequest(bugDescription, "predict_severity");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<BugRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<AnalysisResponse> response = restTemplate.postForEntity(
                aiServiceUrl + "/analyze",
                entity,
                AnalysisResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getResult();
            }
        } catch (Exception e) {
            logger.error("Error calling AI service for severity prediction: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Extract entities from bug description
     * Note: Entities are now part of tags in comprehensive analysis
     */
    public Map<String, List<String>> extractEntities(String bugDescription) {
        try {
            ComprehensiveAnalysisRequest request = new ComprehensiveAnalysisRequest(bugDescription);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ComprehensiveAnalysisRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<ComprehensiveAnalysisResponse> response = restTemplate.postForEntity(
                aiServiceUrl + "/analyze/comprehensive",
                entity,
                ComprehensiveAnalysisResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Convert tags to entities format
                Map<String, List<String>> entities = new HashMap<>();
                if (response.getBody().getTags() != null) {
                    entities.put("tags", response.getBody().getTags());
                }
                return entities;
            }
        } catch (Exception e) {
            logger.error("Error calling AI service for entity extraction: {}", e.getMessage());
        }
        return new HashMap<>();
    }
    
    /**
     * Get comprehensive AI analysis
     */
    public ComprehensiveAnalysisResponse getComprehensiveAnalysis(String bugDescription) {
        try {
            ComprehensiveAnalysisRequest request = new ComprehensiveAnalysisRequest(bugDescription);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ComprehensiveAnalysisRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<ComprehensiveAnalysisResponse> response = restTemplate.postForEntity(
                aiServiceUrl + "/analyze/comprehensive",
                entity,
                ComprehensiveAnalysisResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            logger.error("Error calling AI service for comprehensive analysis: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Generate embedding vector for similarity search
     */
    public List<Double> generateEmbedding(String bugDescription) {
        try {
            ComprehensiveAnalysisRequest request = new ComprehensiveAnalysisRequest(bugDescription);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ComprehensiveAnalysisRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<ComprehensiveAnalysisResponse> response = restTemplate.postForEntity(
                aiServiceUrl + "/analyze/comprehensive",
                entity,
                ComprehensiveAnalysisResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getEmbedding();
            }
        } catch (Exception e) {
            logger.error("Error calling AI service for embedding generation: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Get solution suggestions
     * Note: Solution suggestions not implemented in current AI service
     * Returns empty list - can be extended later
     */
    public List<String> suggestSolutions(String bugDescription) {
        // Solution suggestions not implemented in current lightweight AI service
        // Can be added later if needed
        return new ArrayList<>();
    }
}

