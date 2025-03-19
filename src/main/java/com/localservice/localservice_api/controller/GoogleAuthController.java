package com.localservice.localservice_api.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.localservice.localservice_api.configuration.GoogleCalendarConfig;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/calendar/oauth")
public class GoogleAuthController {

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    public GoogleAuthController(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
    }

    @GetMapping("/login")
    public String login(@RequestParam("userId") String userId) throws IOException {
        return GoogleCalendarConfig.getAuthorizationUrl(userId);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, @RequestParam(value = "state", required = false) String userId) throws IOException {
        if (userId == null) {
            return "Error: Missing userId. Please log in again.";
        }
        // Exchange authorization code for an access token
        TokenResponse tokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                .setRedirectUri("https://booking-app.us-east-1.elasticbeanstalk.com/service-provider/api/calendar/oauth/callback")
                .execute();

        // Convert TokenResponse into Credential and store it
        Credential credential = googleAuthorizationCodeFlow.createAndStoreCredential(tokenResponse, userId);

        // Store user token dynamically
        GoogleCalendarConfig.storeUserToken(userId, credential);

        return "Login successful for user: " + userId + ". You can now create events.";
    }


}
