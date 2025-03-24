package com.localservice.localservice_api.repository;

import com.localservice.localservice_api.entity.GoogleCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleCredentialRepository extends JpaRepository<GoogleCredential, Long> {
    GoogleCredential findByUserId(String userId);
}
