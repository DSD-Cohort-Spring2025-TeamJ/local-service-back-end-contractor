package com.localservice.localservice_api.configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Logger;

@Configuration
public class GoogleCalendarConfig {
    private static final Logger logger = Logger.getLogger(GoogleCalendarConfig.class.getName());
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/calendar");

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = GoogleCalendarConfig.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Missing credentials.json in src/main/resources/");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new MemoryDataStoreFactory())
                .setAccessType("offline")
                .build();
    }

    public String getAuthorizationUrl(GoogleAuthorizationCodeFlow flow, String userId) {
        return flow.newAuthorizationUrl()
                .setRedirectUri("https://booking-app.us-east-1.elasticbeanstalk.com/service-provider/api/calendar/oauth/callback")
                .setState(userId)
                .build();
    }
}

