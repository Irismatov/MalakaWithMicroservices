package com.malaka.aat.internal.dto.module;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.malaka.aat.internal.model.spr.StateHModule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
public class ModuleHDto {
    private String state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    private String description;

    public ModuleHDto(StateHModule stateHModule) {
        this.state = stateHModule.getStateCd();
        this.time = stateHModule.getInstime();
        this.description = stateHModule.getDescriptions();

    }
}
