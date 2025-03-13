package com.localservice.localservice_api.repository;

import com.localservice.localservice_api.entity.PromptConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptConfigRepository extends JpaRepository<PromptConfig, Long> {

    // Fetch the latest prompt dynamically
    @Query("SELECT p FROM PromptConfig p ORDER BY p.id DESC LIMIT 1")
    PromptConfig findLatestPrompt();
}
