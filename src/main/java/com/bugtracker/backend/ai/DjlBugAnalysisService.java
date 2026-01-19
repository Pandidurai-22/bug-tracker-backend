package com.bugtracker.backend.ai;

import com.bugtracker.backend.model.Bug;
import com.bugtracker.backend.repository.BugRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-powered bug analysis service using Hugging Face models via Python microservice
 * Implements microservices architecture with fallback to rule-based analysis
 */
@Service
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true", matchIfMissing = false)
public class DjlBugAnalysisService implements BugAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(DjlBugAnalysisService.class);
    
    private final AIServiceClient aiServiceClient;
    private final BugRepository bugRepository;
    
    @Autowired
    public DjlBugAnalysisService(AIServiceClient aiServiceClient, BugRepository bugRepository) {
        this.aiServiceClient = aiServiceClient;
        this.bugRepository = bugRepository;
    }
    
    @Override
    public String predictPriority(String bugDescription) {
        // Try AI service first (Hugging Face model)
        if (aiServiceClient.isServiceAvailable()) {
            String aiResult = aiServiceClient.predictPriority(bugDescription);
            if (aiResult != null && !aiResult.isEmpty()) {
                logger.debug("AI priority prediction: {}", aiResult);
                return aiResult;
            }
        }
        
        // Fallback to rule-based prediction
        logger.debug("Using rule-based priority prediction");
        return ruleBasedPriorityPrediction(bugDescription);
    }
    
    @Override
    public String predictSeverity(String bugDescription) {
        // Try AI service first (Hugging Face model)
        if (aiServiceClient.isServiceAvailable()) {
            String aiResult = aiServiceClient.predictSeverity(bugDescription);
            if (aiResult != null && !aiResult.isEmpty()) {
                logger.debug("AI severity prediction: {}", aiResult);
                return aiResult;
            }
        }
        
        // Fallback to rule-based prediction
        logger.debug("Using rule-based severity prediction");
        return ruleBasedSeverityPrediction(bugDescription);
    }
    
    @Override
    public Map<String, List<String>> extractEntities(String bugDescription) {
        // Try AI service first
        if (aiServiceClient.isServiceAvailable()) {
            Map<String, List<String>> entities = aiServiceClient.extractEntities(bugDescription);
            if (entities != null && !entities.isEmpty()) {
                return entities;
            }
        }
        
        // Fallback to simple extraction
        return simpleEntityExtraction(bugDescription);
    }
    
    @Override
    public List<SimilarBug> findSimilarBugs(String bugDescription, int limit) {
        try {
            // Generate embedding for the input bug description
            List<Double> queryEmbedding = aiServiceClient.generateEmbedding(bugDescription);
            if (queryEmbedding == null || queryEmbedding.isEmpty()) {
                logger.warn("Failed to generate embedding, returning empty similar bugs list");
                return Collections.emptyList();
            }
            
            // Get all bugs with embeddings
            List<Bug> allBugs = bugRepository.findAll();
            
            // Calculate cosine similarity for each bug
            List<SimilarBug> similarBugs = new ArrayList<>();
            for (Bug bug : allBugs) {
                if (bug.getDescription() == null || bug.getDescription().isEmpty()) {
                    continue;
                }
                
                // Generate embedding for this bug if not already stored
                List<Double> bugEmbedding = aiServiceClient.generateEmbedding(bug.getDescription());
                if (bugEmbedding == null || bugEmbedding.isEmpty()) {
                    continue;
                }
                
                double similarity = cosineSimilarity(queryEmbedding, bugEmbedding);
                if (similarity > 0.5) { // Threshold for similarity
                    similarBugs.add(new SimilarBug(bug.getId(), similarity));
                }
            }
            
            // Sort by similarity and return top N
            return similarBugs.stream()
                    .sorted((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()))
                    .limit(limit)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error finding similar bugs: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<String> suggestSolutions(String bugDescription) {
        // Try AI service first
        if (aiServiceClient.isServiceAvailable()) {
            List<String> suggestions = aiServiceClient.suggestSolutions(bugDescription);
            if (suggestions != null && !suggestions.isEmpty()) {
                return suggestions;
            }
        }
        
        // Fallback to rule-based suggestions
        return ruleBasedSolutionSuggestions(bugDescription);
    }
    
    // Helper methods for fallback rule-based analysis
    
    private String ruleBasedPriorityPrediction(String bugDescription) {
        String lowerDesc = bugDescription.toLowerCase();
        
        if (lowerDesc.contains("crash") || lowerDesc.contains("data loss") || 
            lowerDesc.contains("security") || lowerDesc.contains("critical")) {
            return "HIGH";
        } else if (lowerDesc.contains("not working") || lowerDesc.contains("error") ||
                   lowerDesc.contains("broken") || lowerDesc.contains("fail")) {
            return "MEDIUM";
        } else if (lowerDesc.contains("suggestion") || lowerDesc.contains("enhancement") ||
                   lowerDesc.contains("improvement")) {
            return "LOW";
        }
        return "MEDIUM";
    }
    
    private String ruleBasedSeverityPrediction(String bugDescription) {
        String lowerDesc = bugDescription.toLowerCase();
        
        if (lowerDesc.contains("crash") || lowerDesc.contains("data loss") ||
            lowerDesc.contains("security breach")) {
            return "CRITICAL";
        } else if (lowerDesc.contains("error") || lowerDesc.contains("not working") ||
                   lowerDesc.contains("broken")) {
            return "MAJOR";
        } else if (lowerDesc.contains("cosmetic") || lowerDesc.contains("typo") ||
                   lowerDesc.contains("minor")) {
            return "MINOR";
        } else if (lowerDesc.contains("suggestion") || lowerDesc.contains("enhancement")) {
            return "ENHANCEMENT";
        }
        return "NORMAL";
    }
    
    private Map<String, List<String>> simpleEntityExtraction(String bugDescription) {
        Map<String, List<String>> entities = new HashMap<>();
        String lowerDesc = bugDescription.toLowerCase();
        
        // Extract components
        List<String> components = new ArrayList<>();
        if (lowerDesc.contains("api")) components.add("API");
        if (lowerDesc.contains("database") || lowerDesc.contains("db")) components.add("Database");
        if (lowerDesc.contains("frontend") || lowerDesc.contains("ui")) components.add("UI");
        if (lowerDesc.contains("backend")) components.add("Backend");
        if (lowerDesc.contains("auth")) components.add("Authentication");
        if (!components.isEmpty()) {
            entities.put("components", components);
        }
        
        return entities;
    }
    
    private List<String> ruleBasedSolutionSuggestions(String bugDescription) {
        List<String> suggestions = new ArrayList<>();
        String lowerDesc = bugDescription.toLowerCase();
        
        if (lowerDesc.contains("null pointer") || lowerDesc.contains("npe")) {
            suggestions.add("Check for null references before accessing object properties or methods.");
            suggestions.add("Add null checks in the code where the error occurs.");
        }
        
        if (lowerDesc.contains("timeout") || lowerDesc.contains("timed out")) {
            suggestions.add("Increase the timeout threshold in the configuration.");
            suggestions.add("Optimize the database queries that might be causing the delay.");
        }
        
        if (lowerDesc.contains("authentication") || lowerDesc.contains("login")) {
            suggestions.add("Verify user credentials and authentication tokens.");
            suggestions.add("Check if the user has proper permissions for the requested resource.");
        }
        
        // Default suggestions
        if (suggestions.isEmpty()) {
            suggestions.add("Check the application logs for more detailed error information.");
            suggestions.add("Verify that all required dependencies are properly installed and configured.");
        }
        
        return suggestions;
    }
    
    /**
     * Calculate cosine similarity between two embedding vectors
     */
    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}


// package com.bugtracker.backend.ai;

// import ai.djl.Model;
// import ai.djl.inference.Predictor;
// import ai.djl.modality.Classifications;
// import ai.djl.repository.zoo.Criteria;
// import ai.djl.repository.zoo.ZooModel;
// import ai.djl.training.util.ProgressBar;
// import org.springframework.stereotype.Service;

// import jakarta.annotation.PostConstruct;

// import java.util.*;

// @Service
// public class DjlBugAnalysisService implements BugAnalysisService {
    
//     private ZooModel<String, Classifications> model;
    
//     @PostConstruct
//     public void init() {
//         try {
//             // Load the Hugging Face sentiment analysis model
//             Criteria<String, Classifications> criteria = Criteria.builder()
//                     .setTypes(String.class, Classifications.class)
//                     .optModelUrls("djl://ai.djl.huggingface.pytorch/distilbert-base-uncased-finetuned-sst-2-english")
//                     .optEngine("PyTorch")
//                     .optProgress(new ProgressBar())
//                     .build();
            
//             this.model = criteria.loadModel();
//             System.out.println("Hugging Face model loaded successfully!");
//         } catch (Exception e) {
//             System.err.println("Failed to load AI model. Using rule-based fallback.");
//             System.err.println("Error: " + e.getMessage());
//             this.model = null;
//         }
//     }

//     @Override
//     public String predictPriority(String bugDescription) {
//         // In a real implementation, this would use a trained model
//         // For now, we'll use a simple rule-based approach
//         String lowerDesc = bugDescription.toLowerCase();
        
//         if (lowerDesc.contains("crash") || lowerDesc.contains("data loss") || lowerDesc.contains("security")) {
//             return "HIGH";
//         } else if (lowerDesc.contains("not working") || lowerDesc.contains("error")) {
//             return "MEDIUM";
//         } else if (lowerDesc.contains("suggestion") || lowerDesc.contains("enhancement")) {
//             return "LOW";
//         }
//         return "MEDIUM";
//     }
    
//     @Override
//     public String predictSeverity(String bugDescription) {
//         // In a real implementation, this would use a trained model
//         // For now, we'll use a simple rule-based approach
//         String lowerDesc = bugDescription.toLowerCase();
        
//         if (lowerDesc.contains("crash") || lowerDesc.contains("data loss")) {
//             return "CRITICAL";
//         } else if (lowerDesc.contains("error") || lowerDesc.contains("not working")) {
//             return "MAJOR";
//         } else if (lowerDesc.contains("cosmetic") || lowerDesc.contains("typo")) {
//             return "MINOR";
//         } else if (lowerDesc.contains("suggestion") || lowerDesc.contains("enhancement")) {
//             return "ENHANCEMENT";
//         }
//         return "NORMAL";
//     }
    
//     @Override
//     public Map<String, List<String>> extractEntities(String bugDescription) {
//         // Since we removed OpenNLP NER, return an empty map
//         return new HashMap<>();
//     }
    
//     @Override
//     public List<SimilarBug> findSimilarBugs(String bugDescription, int limit) {
//         // In a real implementation, this would use vector similarity search
//         // For now, return an empty list
//         return Collections.emptyList();
//     }
    
//     @Override
//     public List<String> suggestSolutions(String bugDescription) {
//         // In a real implementation, this would use a language model
//         // For now, return some generic suggestions based on keywords
//         List<String> suggestions = new ArrayList<>();
        
//         String lowerDesc = bugDescription.toLowerCase();
        
//         if (lowerDesc.contains("null pointer")) {
//             suggestions.add("Check for null references before accessing object properties or methods.");
//             suggestions.add("Add null checks in the code where the error occurs.");
//         }
        
//         if (lowerDesc.contains("timeout")) {
//             suggestions.add("Increase the timeout threshold in the configuration.");
//             suggestions.add("Optimize the database queries that might be causing the delay.");
//         }
        
//         if (suggestions.isEmpty()) {
//             suggestions.add("Check the application logs for more detailed error information.");
//             suggestions.add("Verify that all required dependencies are properly installed and configured.");
//         }
        
//         return suggestions;
//     }
// }







// package com.bugtracker.backend.ai;

// import ai.djl.Model;
// import ai.djl.inference.Predictor;
// import ai.djl.modality.Classifications;
// import ai.djl.repository.zoo.Criteria;
// import ai.djl.repository.zoo.ZooModel;
// import ai.djl.training.util.ProgressBar;
// import org.springframework.stereotype.Service;

// import jakarta.annotation.PostConstruct;

// import java.util.*;

// @Service
// public class DjlBugAnalysisService implements BugAnalysisService {
    
//     private ZooModel<String, Classifications> model;
//     private NameFinderME nameFinder;
    
//     // @PostConstruct
//     // public void init() throws Exception {
//     //     // Initialize the Hugging Face model for text classification
//     //     Criteria<String, Classifications> criteria = Criteria.builder()
//     //             .setTypes(String.class, Classifications.class)
//     //             .optModelUrls("djl://ai.djl.huggingface.pytorch/distilbert-base-uncased-finetuned-sst-2-english")
//     //             .optEngine("PyTorch")
//     //             .optProgress(new ProgressBar())
//     //             .build();
        
//     //     this.model = criteria.loadModel();
        
//     //     // Initialize OpenNLP name finder for entity extraction
//     //     try (InputStream modelIn = getClass().getResourceAsStream("/models/en-ner-bugtracker.bin")) {
//     //         if (modelIn == null) {
//     //             // Fallback to a default model if custom model not found
//     //             TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(
//     //                     getClass().getResourceAsStream("/models/en-ner-person.bin"));
//     //             this.nameFinder = new NameFinderME(nameFinderModel);
//     //         } else {
//     //             TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(modelIn);
//     //             this.nameFinder = new NameFinderME(nameFinderModel);
//     //         }
//     //     } catch (IOException e) {
//     //         throw new RuntimeException("Failed to initialize NER model", e);
//     //     }
//     // }
//     @PostConstruct
//     public void init() {
//         try {
//             // Load the Hugging Face sentiment analysis model
//             Criteria<String, Classifications> criteria = Criteria.builder()
//                     .setTypes(String.class, Classifications.class)
//                     .optModelUrls("djl://ai.djl.huggingface.pytorch/distilbert-base-uncased-finetuned-sst-2-english")
//                     .optEngine("PyTorch")
//                     .optProgress(new ProgressBar())
//                     .build();
            
//             this.model = criteria.loadModel();
//             System.out.println("Hugging Face model loaded successfully!");
//         } catch (Exception e) {
//             System.err.println("Failed to load AI model. Using rule-based fallback.");
//             System.err.println("Error: " + e.getMessage());
//             this.model = null;
//         }
//     }
//     @Override
//     public String predictPriority(String bugDescription) {
//         // In a real implementation, this would use a trained model
//         // For now, we'll use a simple rule-based approach
//         String lowerDesc = bugDescription.toLowerCase();
        
//         if (lowerDesc.contains("crash") || lowerDesc.contains("data loss") || lowerDesc.contains("security")) {
//             return "HIGH";
//         } else if (lowerDesc.contains("not working") || lowerDesc.contains("error")) {
//             return "MEDIUM";
//         } else if (lowerDesc.contains("suggestion") || lowerDesc.contains("enhancement")) {
//             return "LOW";
//         }
//         return "MEDIUM";
//     }
    
//     @Override
//     public String predictSeverity(String bugDescription) {
//         // In a real implementation, this would use a trained model
//         // For now, we'll use a simple rule-based approach
//         String lowerDesc = bugDescription.toLowerCase();
        
//         if (lowerDesc.contains("crash") || lowerDesc.contains("data loss")) {
//             return "CRITICAL";
//         } else if (lowerDesc.contains("error") || lowerDesc.contains("not working")) {
//             return "MAJOR";
//         } else if (lowerDesc.contains("cosmetic") || lowerDesc.contains("typo")) {
//             return "MINOR";
//         } else if (lowerDesc.contains("suggestion") || lowerDesc.contains("enhancement")) {
//             return "ENHANCEMENT";
//         }
//         return "NORMAL";
//     }
    
//     @Override
//     public Map<String, List<String>> extractEntities(String bugDescription) {
//         // Since we removed OpenNLP NER, return an empty map
//         return new HashMap<>();
//     }
    
//     @Override
//     public List<SimilarBug> findSimilarBugs(String bugDescription, int limit) {
//         // In a real implementation, this would use vector similarity search
//         // For now, return an empty list
//         return Collections.emptyList();
//     }
    
//     @Override
//     public List<String> suggestSolutions(String bugDescription) {
//         // In a real implementation, this would use a language model
//         // For now, return some generic suggestions based on keywords
//         List<String> suggestions = new ArrayList<>();
        
//         String lowerDesc = bugDescription.toLowerCase();
        
//         if (lowerDesc.contains("null pointer")) {
//             suggestions.add("Check for null references before accessing object properties or methods.");
//             suggestions.add("Add null checks in the code where the error occurs.");
//         }
        
//         if (lowerDesc.contains("timeout")) {
//             suggestions.add("Increase the timeout threshold in the configuration.");
//             suggestions.add("Optimize the database queries that might be causing the delay.");
//         }
        
//         if (suggestions.isEmpty()) {
//             suggestions.add("Check the application logs for more detailed error information.");
//             suggestions.add("Verify that all required dependencies are properly installed and configured.");
//         }
        
//         return suggestions;
//     }
    
//     @Override
//     protected void finalize() throws Throwable {
//         try {
//             if (model != null) {
//                 model.close();
//             }
//         } finally {
//             super.finalize();
//         }
//     }
// }
