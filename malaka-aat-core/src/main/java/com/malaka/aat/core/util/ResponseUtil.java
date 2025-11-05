package com.malaka.aat.core.util;


import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;

public class ResponseUtil {
    public static void setResponseStatus(BaseResponse response, ResponseStatus status) {
        response.setResultCode(status.getCode());
        response.setResultNote(status.getNote());
    }
}
