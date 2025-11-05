package com.malaka.aat.internal.dto.StateHMet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.malaka.aat.internal.model.spr.StateHMet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StateHMetDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    private String state;

    public StateHMetDto(StateHMet stateHMet) {
        this.time = stateHMet.getInstime();
        this.state = stateHMet.getStateCd();
    }
}
