package com.localservice.localservice_api.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.localservice.localservice_api.configuration.GoogleCalendarConfig;
import com.localservice.localservice_api.service.GoogleCalendarService;
import com.localservice.localservice_api.service.GoogleCredentialService;
import com.localservice.localservice_api.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/calendar/oauth")
public class GoogleAuthController {

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;
    private final JwtService jwtService;
    private final GoogleCredentialService googleCredentialService;
    private final GoogleCalendarService googleCalendarService;

    public GoogleAuthController(GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow, JwtService jwtService, GoogleCredentialService googleCredentialService, GoogleCalendarService googleCalendarService) {
        this.googleAuthorizationCodeFlow = googleAuthorizationCodeFlow;
        this.jwtService = jwtService;
        this.googleCredentialService = googleCredentialService;
        this.googleCalendarService = googleCalendarService;
    }

    @GetMapping("/login")
    public String login(@RequestParam("userId") String userId) throws IOException {
        return GoogleCalendarConfig.getAuthorizationUrl(userId);
    }

    @GetMapping("/callback")
    public void callback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String userId,
            HttpServletResponse response
    ) throws IOException {
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing userId. Please log in again.");
            return;
        }

        TokenResponse tokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                .setRedirectUri("https://booking-app.us-east-1.elasticbeanstalk.com/service-provider/api/calendar/oauth/callback")
                .execute();

        Credential credential = googleAuthorizationCodeFlow.createAndStoreCredential(tokenResponse, userId);

        googleCredentialService.storeCredential(userId, credential);

        boolean syncSuccess;
        try {
            googleCalendarService.syncUnsyncedAppointmentsToCalendar(userId);
            syncSuccess = true;
        } catch (IOException | GeneralSecurityException ex) {
            syncSuccess = false;
            System.err.println("Calendar sync failed for user " + userId + ": " + ex.getMessage());
        }

        String jwtToken = jwtService.generateToken(userId);

        String htmlResponse = getHtmlResponse(syncSuccess, jwtToken);

        response.setContentType("text/html");
        response.getWriter().write(htmlResponse);
    }

    private static String getHtmlResponse(boolean syncSuccess, String jwtToken) {
        String syncMessage = syncSuccess
                ? "Calendar sync completed successfully."
                : "Authentication successful, but calendar sync failed. You can sync manually later.";

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head><title>Auth Success</title></head>\n" +
                "<body>\n" +
                "<script>\n" +
                "window.opener.postMessage({ token: '" + jwtToken + "' }, '*');\n" +
                "window.close();\n" +
                "</script>\n" +
                "<p>" + syncMessage + "</p>\n" +
                "<p>Authentication successful. You may close this window.</p>\n" +
                "</body>\n" +
                "</html>";
    }

}
