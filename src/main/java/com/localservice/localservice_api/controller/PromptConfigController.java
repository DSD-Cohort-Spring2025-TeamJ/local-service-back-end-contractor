package com.localservice.localservice_api.controller;

import com.localservice.localservice_api.entity.PromptConfig;
import com.localservice.localservice_api.service.PromptConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prompts")
public class PromptConfigController {
    @Autowired
    private PromptConfigService promptConfigService;

    // Get all prompts
    @GetMapping
    public List<PromptConfig> getAllPrompts() {
        return promptConfigService.getAllPrompts();
    }

    // Get the latest prompt
    @GetMapping("/latest")
    public PromptConfig getLatestPrompt() {
        return promptConfigService.getLatestPrompt();
    }

    // Get a prompt by ID
    @GetMapping("/{id}")
    public Optional<PromptConfig> getPromptById(@PathVariable Long id) {
        return promptConfigService.getPromptById(id);
    }

    // Create a new prompt
    @PostMapping
    public PromptConfig createPrompt(@RequestBody PromptConfig promptConfig) {
        return promptConfigService.createPrompt(promptConfig);
    }

    // Update a prompt by ID
    @PutMapping("/{id}")
    public PromptConfig updatePrompt(@PathVariable Long id, @RequestBody PromptConfig updatedPrompt) {
        return promptConfigService.updatePrompt(id, updatedPrompt);
    }

    // Delete a prompt by ID
    @DeleteMapping("/{id}")
    public void deletePrompt(@PathVariable Long id) {
        promptConfigService.deletePrompt(id);
    }
}
