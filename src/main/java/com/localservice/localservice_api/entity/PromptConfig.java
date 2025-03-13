package com.localservice.localservice_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prompt_config")
@Getter
@Setter
public class PromptConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prompt_text", columnDefinition = "TEXT", nullable = false)
    private String promptText;
}
