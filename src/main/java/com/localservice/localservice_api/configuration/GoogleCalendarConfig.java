package com.localservice.localservice_api.configuration;

import com.google.api.client.auth.oauth2.Credential;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Configuration
public class GoogleCalendarConfig {
    private static final Logger logger = Logger.getLogger(GoogleCalendarConfig.class.getName());
    private static final String APPLICATION_NAME = "Google Calendar API Spring Boot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // OAuth Scopes
    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/calendar");

    private static GoogleAuthorizationCodeFlow flow;
    private static final ConcurrentHashMap<String, Credential> userCredentials = new ConcurrentHashMap<>();


    @Bean
    public GoogleAuthorizationCodeFlow initFlow() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = GoogleCalendarConfig.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Missing credentials.json in src/main/resources/");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new MemoryDataStoreFactory())  // Stores tokens dynamically in memory
                .setAccessType("offline")
                .build();

        return flow;
    }

    public static String getAuthorizationUrl(String userId) throws IOException {
        //replace this with localhost for local testing
        return flow.newAuthorizationUrl().setRedirectUri("https://booking-app.us-east-1.elasticbeanstalk.com/service-provider/api/calendar/oauth/callback").setState(userId).build();
    }

}
