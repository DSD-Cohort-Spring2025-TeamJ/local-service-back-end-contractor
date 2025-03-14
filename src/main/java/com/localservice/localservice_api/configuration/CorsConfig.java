package com.localservice.localservice_api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://thepragmaticplumber.netlify.app", "https://app.battleoptionsapi.com/")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        // Allow Swagger UI and OpenAPI endpoints
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("http://localhost:5173", "https://thepragmaticplumber.netlify.app")
                .allowedMethods("GET")
                .allowedHeaders("*");

        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("http://localhost:5173", "https://thepragmaticplumber.netlify.app")
                .allowedMethods("GET");
    }

}