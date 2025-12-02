package com.malaka.aat.external.clients.egov.token;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class EgovTokenResponse {
    private String access_token;
    private String scope;
    private String token_type;
    private Long expires_in;
}
