package com.localservice.localservice_api.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.localservice.localservice_api.configuration.GoogleCalendarConfig;
import com.localservice.localservice_api.service.GoogleCredentialService;
import com.localservice.localservice_api.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
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
    public void callback(@RequestParam("code") String code, @RequestParam(value = "state", required = false) String userId, HttpServletResponse response) throws IOException {
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing userId. Please log in again.");
            return;
        }

        TokenResponse tokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                .setRedirectUri("https://booking-app.us-east-1.elasticbeanstalk.com/service-provider/api/calendar/oauth/callback")
                .execute();

        Credential credential = googleAuthorizationCodeFlow.createAndStoreCredential(tokenResponse, userId);

        googleCredentialService.storeCredential(userId, credential);

        String jwtToken = jwtService.generateToken(userId);

        String redirectUrl = userId.contains("localhost")
                ? "http://localhost:5173/admin?token=" + jwtToken
                : "https://thepragmaticplumber.netlify.app/admin?token=" + jwtToken;

        response.sendRedirect(redirectUrl);
    }

}
