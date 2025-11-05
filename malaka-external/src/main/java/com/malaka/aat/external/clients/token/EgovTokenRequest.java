package com.malaka.aat.external.clients.token;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class EgovTokenRequest {
    private String grant_type;
    private String username;
    private String password;
}
