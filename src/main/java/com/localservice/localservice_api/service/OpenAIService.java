package com.localservice.localservice_api.service;

import com.localservice.localservice_api.dto.ServiceRequest;
import com.localservice.localservice_api.dto.ServiceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {
    private final RestClient restClient;

    @Value("${openai.api.key}")  // ✅ Set API Key in `application.properties`
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public OpenAIService() {
        this.restClient = RestClient.builder().baseUrl(API_URL).build();
    }

    public ServiceResponse classifyService(ServiceRequest request) {
        // Create the OpenAI prompt

        String prompt = """
    You are a service classification assistant that categorizes customer issues into one of the following categories: 
    - Plumbing: Issues related to water pipes, leaks, faucets, drainage, toilets, and water heaters.
    - Electrical: Problems with wiring, power outages, circuit breakers, electrical panels, and outlets.
    - HVAC: Air conditioning, heating, ventilation, and thermostat-related problems.
    - Roofing: Leaks, missing shingles, damaged roofs, or water seepage from the ceiling.
    - General Maintenance: Any other issues like carpentry, painting, door repairs, or unknown issues.

    Classify the following issue into one of these categories. Be precise and do not assume everything is Plumbing. 

    Issue: "%s"

    Return only the category name.
""".formatted(request.getIssueDescription());

        // Construct the API request
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                        Map.of("role", "system", "content", "You are a service classification assistant."),
                        Map.of("role", "user", "content", prompt)
                },
                "temperature", 0.3,
                "max_tokens", 50
        );

        try {
            // Send request using RestClient
            Map<String, Object> response = restClient.post()
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class); // Map response directly

            // Extract the category from OpenAI response
            if (response != null && response.containsKey("choices")) {
                Map<String, Object> choice = (Map<String, Object>) ((java.util.List<?>) response.get("choices")).get(0);
                Map<String, String> message = (Map<String, String>) choice.get("message");
                String category = message.get("content").trim();

                return new ServiceResponse(category);
            } else {
                return new ServiceResponse("Error: Unexpected response from OpenAI");
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle HTTP errors (e.g., 401 Unauthorized, 429 Rate Limit)
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ServiceResponse("Error: Invalid API Key");
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return new ServiceResponse("Error: Rate limit exceeded. Try again later.");
            } else {
                return new ServiceResponse("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            return new ServiceResponse("Error: " + e.getMessage());
        }
    }

}
