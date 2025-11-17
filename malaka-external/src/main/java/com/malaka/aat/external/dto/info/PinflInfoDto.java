package com.malaka.aat.external.dto.info;

import com.malaka.aat.external.clients.gcp.EgovGcpResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PinflInfoDto {
    private String pinfl;
    private String fio;

    public PinflInfoDto(EgovGcpResponse info) {
        List<EgovGcpResponse.EgovGcpResponseData> data = info.getData();
        EgovGcpResponse.EgovGcpResponseData first = data.get(0);
        this.pinfl = first.getCurrentPinpp();
        StringBuilder fioStrBuilder = new StringBuilder();
        if (first.getLastNameOz() != null) {
            fioStrBuilder.append(first.getLastNameOz()).append(" ");
        }
        if (first.getFirstNameOz() != null) {
            fioStrBuilder.append(first.getFirstNameOz()).append(" ");
        }
        if (first.getMiddleNameOz() != null) {
            fioStrBuilder.append(first.getMiddleNameOz()).append(" ");
        }

        this.fio = fioStrBuilder.toString();
    }

}
