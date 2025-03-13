package com.localservice.localservice_api.service;

import com.localservice.localservice_api.entity.PromptConfig;
import com.localservice.localservice_api.repository.PromptConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromptConfigService {

    @Autowired
    private PromptConfigRepository promptConfigRepository;

    // Get all prompts
    public List<PromptConfig> getAllPrompts() {
        return promptConfigRepository.findAll();
    }

    // Get the latest prompt
    public PromptConfig getLatestPrompt() {
        return promptConfigRepository.findLatestPrompt();
    }

    // Get a specific prompt by ID
    public Optional<PromptConfig> getPromptById(Long id) {
        return promptConfigRepository.findById(id);
    }

    // Create a new prompt
    public PromptConfig createPrompt(PromptConfig promptConfig) {
        return promptConfigRepository.save(promptConfig);
    }

    // Update an existing prompt by ID
    public PromptConfig updatePrompt(Long id, PromptConfig updatedPrompt) {
        return promptConfigRepository.findById(id).map(existingPrompt -> {
            existingPrompt.setPromptText(updatedPrompt.getPromptText());
            return promptConfigRepository.save(existingPrompt);
        }).orElseThrow(() -> new RuntimeException("Prompt not found with id: " + id));
    }

    // Delete a prompt by ID
    public void deletePrompt(Long id) {
        promptConfigRepository.deleteById(id);
    }
}
