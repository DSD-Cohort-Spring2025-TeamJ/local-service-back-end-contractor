package com.localservice.localservice_api.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.localservice.localservice_api.configuration.GoogleCalendarConfig;
import com.localservice.localservice_api.service.GoogleCredentialService;
import com.localservice.localservice_api.service.JwtService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/calendar/oauth")
public class GoogleAuthController {

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;
    private final JwtService jwtService;
    private final GoogleCredentialService googleCredentialService;

    public GoogleAuthController(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow, JwtService jwtService, GoogleCredentialService googleCredentialService) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
        this.jwtService = jwtService;
        this.googleCredentialService = googleCredentialService;
    }

    @GetMapping("/login")
    public String login(@RequestParam("userId") String userId) throws IOException {
        return GoogleCalendarConfig.getAuthorizationUrl(userId);
    }

    @GetMapping("/callback")
    public @ResponseBody String callback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String userId
    ) throws IOException {
        if (userId == null) {
            return "{\"error\": \"Missing userId. Please log in again.\"}";
        }

        String redirectUri = userId.contains("localhost")
                ? "http://localhost:5173/admin"
                : "https://thepragmaticplumber.netlify.app/admin";

        TokenResponse tokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        Credential credential = googleAuthorizationCodeFlow.createAndStoreCredential(tokenResponse, userId);

        googleCredentialService.storeCredential(userId, credential);

        String jwtToken = jwtService.generateToken(userId);

        return String.format("{\"token\": \"%s\"}", jwtToken);
    }

}
