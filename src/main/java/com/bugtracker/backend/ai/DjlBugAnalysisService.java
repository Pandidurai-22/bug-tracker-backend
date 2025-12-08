package com.bugtracker.backend.ai;

import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.ZooModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true", matchIfMissing = false)
public class DjlBugAnalysisService implements BugAnalysisService {
    
    // We're removing the model loading since it's causing issues
    // private ZooModel<String, Classifications> model;
    
    @Override
    public String predictPriority(String bugDescription) {
        // Simple rule-based priority prediction
        String lowerDesc = bugDescription.toLowerCase();
        
        if (lowerDesc.contains("crash") || lowerDesc.contains("data loss") || lowerDesc.contains("security")) {
            return "HIGH";
        } else if (lowerDesc.contains("not working") || lowerDesc.contains("error")) {
            return "MEDIUM";
        } else if (lowerDesc.contains("suggestion") || lowerDesc.contains("enhancement")) {
            return "LOW";
        }
        return "MEDIUM";
    }
    
    @Override
    public String predictSeverity(String bugDescription) {
        // Simple rule-based severity prediction
        String lowerDesc = bugDescription.toLowerCase();
        
        if (lowerDesc.contains("crash") || lowerDesc.contains("data loss")) {
            return "CRITICAL";
        } else if (lowerDesc.contains("error") || lowerDesc.contains("not working")) {
            return "MAJOR";
        } else if (lowerDesc.contains("cosmetic") || lowerDesc.contains("typo")) {
            return "MINOR";
        } else if (lowerDesc.contains("suggestion") || lowerDesc.contains("enhancement")) {
            return "ENHANCEMENT";
        }
        return "NORMAL";
    }
    
    @Override
    public Map<String, List<String>> extractEntities(String bugDescription) {
        // Simple entity extraction (can be expanded)
        Map<String, List<String>> entities = new HashMap<>();
        // Add any simple pattern matching here if needed
        return entities;
    }
    
    @Override
    public List<SimilarBug> findSimilarBugs(String bugDescription, int limit) {
        // Return empty list since we're not doing similarity matching
        return Collections.emptyList();
    }
    
    @Override
    public List<String> suggestSolutions(String bugDescription) {
        // Simple solution suggestions based on keywords
        List<String> suggestions = new ArrayList<>();
        String lowerDesc = bugDescription.toLowerCase();
        
        if (lowerDesc.contains("null pointer")) {
            suggestions.add("Check for null references before accessing object properties or methods.");
            suggestions.add("Add null checks in the code where the error occurs.");
        }
        
        if (lowerDesc.contains("timeout")) {
            suggestions.add("Increase the timeout threshold in the configuration.");
            suggestions.add("Optimize the database queries that might be causing the delay.");
        }
        
        // Default suggestions if no specific ones match
        if (suggestions.isEmpty()) {
            suggestions.add("Check the application logs for more detailed error information.");
            suggestions.add("Verify that all required dependencies are properly installed and configured.");
        }
        
        return suggestions;
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
