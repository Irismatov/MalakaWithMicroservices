package com.malaka.aat.external.dto.info;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetInfoRequest {

    @Size(min = 14, max = 14, message = "Must be 16 character-long")
    @NotBlank(message = "pinfl must be provided")
    private String pinfl;
}
