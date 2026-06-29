package com.sriman.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Processing Platform API")
                        .description("REST APIs for Order Processing Platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tadi Srimannarayana Reddi")
                                .email("your-email@example.com"))
                        .license(new License()
                                .name("MIT License")));
    }
}