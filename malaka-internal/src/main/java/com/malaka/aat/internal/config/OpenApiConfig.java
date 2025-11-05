package com.malaka.aat.internal.config;

import com.malaka.aat.core.dto.ResponseStatus;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MalakaAAT API")
                        .version("1.0")
                        .description(buildResponseStatusDocumentation()))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"))
                        // Add ResponseStatusInfo schema so it appears in Swagger Schemas section
                        .addSchemas("ResponseStatusInfo", new Schema<>()
                                .type("object")
                                .description("Response status kodlari va ularning ma'nolari")
                                .addProperty("code", new Schema<>().type("integer").example(0))
                                .addProperty("name", new Schema<>().type("string").example("SUCCESS"))
                                .addProperty("note", new Schema<>().type("string").example("Muvaffaqiyatli yakunlandi"))))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth"));
    }

    /**
     * Build documentation string with all response status codes
     */
    private String buildResponseStatusDocumentation() {
        StringBuilder doc = new StringBuilder();
        doc.append("MalakaAAT - Malaka oshirish tizimi\n\n");
        doc.append("## Response Status Kodlari\n\n");
        doc.append("Barcha API responselar quyidagi status kodlaridan birini qaytaradi:\n\n");
        doc.append("| Kod | Nom | Tavsif |\n");
        doc.append("|-----|-----|--------|\n");

        for (ResponseStatus status : ResponseStatus.values()) {
            doc.append(String.format("| %d | %s | %s |\n",
                status.getCode(),
                status.name(),
                status.getNote()));
        }

        doc.append("\n**Eslatma:** Barcha HTTP responselar 200 OK statusini qaytaradi. ");
        doc.append("Muvaffaqiyat yoki xatolik `resultCode` va `resultNote` fieldlari orqali aniqlanadi.");

        return doc.toString();
    }
}