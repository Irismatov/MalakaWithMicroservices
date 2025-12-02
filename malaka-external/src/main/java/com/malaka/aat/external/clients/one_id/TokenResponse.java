package com.malaka.aat.external.clients.one_id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter
@Setter
public class TokenResponse {
    private String scope;
    private long expires_in;
    private String token_type;
    private String refresh_token;
    private String access_token;
}
