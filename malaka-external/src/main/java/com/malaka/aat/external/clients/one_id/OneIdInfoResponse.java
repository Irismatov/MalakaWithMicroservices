package com.malaka.aat.external.clients.one_id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter
@Setter
public class OneIdInfoResponse {
    private String pin;
    private String user_id;
    private String full_name;
    private String sur_name;
    private String first_name;
    private String mid_name;
    private String birth_date;
    private String user_type;
    private String ret_cd;
    private String auth_method;
}
