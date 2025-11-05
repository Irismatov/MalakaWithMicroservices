package com.malaka.aat.external.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.EgovClient;
import com.malaka.aat.external.clients.EgovTokenClient;
import com.malaka.aat.external.clients.gcp.EgovGcpResponse;
import com.malaka.aat.external.dto.info.PinflInfoDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InfoService {

    private final EgovClient egovClient;
    private final EgovTokenClient egovTokenClient;
    private static final Logger logger = LoggerFactory.getLogger(InfoService.class);



    public BaseResponse getInfoFromPinfl(String pinfl) {
        BaseResponse response = new BaseResponse();
        EgovGcpResponse info;
        try {
            info = egovClient.getInfo(pinfl);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new SystemException(e.getMessage());
        }
        PinflInfoDto dto = new PinflInfoDto(info);
        response.setData(dto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

}
