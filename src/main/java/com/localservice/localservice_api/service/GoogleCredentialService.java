package com.localservice.localservice_api.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.localservice.localservice_api.entity.GoogleCredential;
import com.localservice.localservice_api.repository.GoogleCredentialRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class GoogleCredentialService {

    private final GoogleCredentialRepository googleCredentialRepository;

    public GoogleCredentialService(GoogleCredentialRepository googleCredentialRepository) {
        this.googleCredentialRepository = googleCredentialRepository;
    }

    public void storeCredential(String userId, Credential credential) {
        findByUserId(userId, credential, googleCredentialRepository);
    }

    public static void findByUserId(String userId, Credential credential, GoogleCredentialRepository googleCredentialRepository) {
        GoogleCredential entity = googleCredentialRepository.findByUserId(userId);
        if (entity == null) {
            entity = new GoogleCredential();
            entity.setUserId(userId);
        }

        entity.setAccessToken(credential.getAccessToken());
        entity.setRefreshToken(credential.getRefreshToken());
        entity.setTokenExpiry(
                credential.getExpirationTimeMilliseconds() != null
                        ? Instant.ofEpochMilli(credential.getExpirationTimeMilliseconds()).atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : LocalDateTime.now().plusHours(1)
        );
        googleCredentialRepository.save(entity);
    }

    public Credential getStoredCredential(String userId) {
        GoogleCredential entity = googleCredentialRepository.findByUserId(userId);
        if (entity == null) {
            throw new RuntimeException("No stored credentials for user: " + userId);
        }

        return new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(entity.getAccessToken())
                .setRefreshToken(entity.getRefreshToken())
                .setExpirationTimeMilliseconds(
                        entity.getTokenExpiry()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                );
    }
}
