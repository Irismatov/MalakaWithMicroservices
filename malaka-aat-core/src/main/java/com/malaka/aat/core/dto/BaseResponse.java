package com.malaka.aat.core.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BaseResponse {
    private int resultCode;
    private String resultNote;
    private Object data;
}
