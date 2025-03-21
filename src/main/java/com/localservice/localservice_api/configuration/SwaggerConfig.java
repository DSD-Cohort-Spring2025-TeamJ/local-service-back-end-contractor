package com.localservice.localservice_api.configuration;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Server prodServer = new Server()
                .url("https://booking-app.us-east-1.elasticbeanstalk.com/service-provider")
                .description("AWS Elastic Beanstalk (Production)");

        Server localServer = new Server()
                .url("http://localhost:8080/service-provider")
                .description("Local environment");

        return new OpenAPI()
                .info(new Info()
                        .title("Local Service API")
                        .version("1.0")
                        .description("API documentation for Local Contract Service"))
                .servers(List.of(prodServer, localServer));
    }
}
