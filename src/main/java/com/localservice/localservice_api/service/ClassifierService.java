package com.localservice.localservice_api.service;

import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ClassifierService {
    private final Map<String, String> serviceDescriptions;
    private final CosineSimilarity cosineSimilarity;

    public ClassifierService() {
        this.cosineSimilarity = new CosineSimilarity();
        this.serviceDescriptions = new HashMap<>();
        serviceDescriptions.put("Plumbing Repair", "Pipe leak, plumber, drainage, faucet, pipe burst, water heater repair");
        serviceDescriptions.put("Electrical Repair", "Electrician, wiring, circuit breaker, voltage issues, fuse, power failure");
        serviceDescriptions.put("HVAC Servicing", "Heating, ventilation, air conditioning, HVAC maintenance, AC repair, furnace");
        serviceDescriptions.put("Appliance Repair", "Fridge repair, washing machine, microwave, oven, dishwasher");
        serviceDescriptions.put("Carpentry & Handyman", "Furniture repair, woodwork, drilling, fixing cabinets, carpenter");
        serviceDescriptions.put("Painting & Wall Repair", "Paint job, wall cracks, putty, brush, roller, wall damage");
        serviceDescriptions.put("Locksmith Services", "Lost keys, lock replacement, locksmith, broken lock, key cutting");
        serviceDescriptions.put("Roof & Gutter Cleaning", "Gutter cleaning, roof maintenance, pressure washer, debris removal");
        serviceDescriptions.put("Pest Control", "Insects, rodent, extermination, cockroach removal, termite, rat control");
        serviceDescriptions.put("Home Cleaning Services", "Dusting, vacuum, cleaning, sanitize, house cleaning, mop, carpet cleaning");
    }

    public String classifyService(String description) {
        Map<String, Double> similarityScores = new HashMap<>();

        // Compute cosine similarity between user input and each service
        for (Map.Entry<String, String> entry : serviceDescriptions.entrySet()) {
            double similarity = cosineSimilarity.cosineSimilarity(
                    tokenize(description), tokenize(entry.getValue())
            );
            similarityScores.put(entry.getKey(), similarity);
        }

        // Find the best match
        return similarityScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .filter(entry -> entry.getValue() > 0.1) // Confidence threshold
                .map(Map.Entry::getKey)
                .orElse("Unknown Service - Please provide more details");
    }

    private Map<CharSequence, Integer> tokenize(String text) {
        Map<CharSequence, Integer> tokens = new HashMap<>();
        for (String word : text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+")) {
            word = stem(word); // ✅ Apply stemming to handle variations
            tokens.put(word, tokens.getOrDefault(word, 0) + 1);
        }
        return tokens;
    }

    // ✅ Simple stemming function (convert similar words)
    private String stem(String word) {
        if (word.endsWith("ing")) return word.substring(0, word.length() - 3); // "dripping" → "drip"
        if (word.endsWith("ed")) return word.substring(0, word.length() - 2);  // "leaked" → "leak"
        return word;
    }

}
