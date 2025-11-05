package com.malaka.aat.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for displaying response status codes in Swagger documentation
 * This class is used to make ResponseStatus enum values visible in Swagger UI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response status kodlari va ularning ma'nolari")
public class ResponseStatusInfo {

    @Schema(description = "Response kodi", example = "0")
    private Integer code;

    @Schema(description = "Response nomi", example = "SUCCESS")
    private String name;

    @Schema(description = "Response tavsifi", example = "Muvaffaqiyatli yakunlandi")
    private String note;
}
