package com.localservice.localservice_api.controller;

import com.localservice.localservice_api.dto.ServiceRequest;
import com.localservice.localservice_api.dto.ServiceResponse;
import com.localservice.localservice_api.service.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ClassifierController {

    private final OpenAIService openAIService;
    public ClassifierController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/classifyServiceDescription")
    public ResponseEntity<ServiceResponse> classifyService(@RequestBody ServiceRequest request) {
        ServiceResponse matchedService = openAIService.classifyService(request);
        return ResponseEntity.ok(matchedService);
    }

}
