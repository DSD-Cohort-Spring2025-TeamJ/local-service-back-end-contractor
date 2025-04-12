package com.localservice.localservice_api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
        
// avoid duplication
private final String[] origins;

public CorsConfig(@Value("${app.security.origins}") String origins) {
this.origins = origins;
}
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        // Allow Swagger UI and OpenAPI endpoints
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins(origins)
                .allowedMethods("GET")
                .allowedHeaders("*");

        registry.addMapping("/swagger-ui/**")
                .allowedOrigins(origins)
                .allowedMethods("GET");
    }

}