package com.malaka.aat.apigateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Malaka AAT API Gateway")
                        .description("API Gateway aggregating all microservices endpoints")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Malaka AAT Team")
                                .email("info@malaka.uz")));
    }
}
