package com.malaka.aat.external.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.EgovClient;
import com.malaka.aat.external.clients.gcp.EgovGcpResponse;
import com.malaka.aat.external.dto.info.GetInfoRequest;
import com.malaka.aat.external.dto.info.PinflInfoDto;
import com.malaka.aat.external.model.InfoPinpp;
import com.malaka.aat.external.repository.InfoPinppRespository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InfoService {

    private final EgovClient egovClient;
    private static final Logger logger = LoggerFactory.getLogger(InfoService.class);
    private final InfoPinppRespository  infoPinppRespository;



    public BaseResponse getInfoFromPinfl(GetInfoRequest request) {
        BaseResponse response = new BaseResponse();
        EgovGcpResponse info;
        try {
            info = egovClient.getInfo(request.getPinfl());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new SystemException(e.getMessage());
        }
        saveInfoPinpp(info);
        PinflInfoDto dto = new PinflInfoDto(info);
        response.setData(dto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public void saveInfoPinpp(EgovGcpResponse info) {
        List<EgovGcpResponse.EgovGcpResponseData> data = info.getData();
        EgovGcpResponse.EgovGcpResponseData first = data.get(0);
        InfoPinpp infoPinpp = infoPinppRespository.findByPinpp(first.getCurrentPinpp()).orElseGet(
                InfoPinpp::new
        );
        infoPinpp.setPinpp(first.getCurrentPinpp());
        infoPinpp.setFirstName(first.getFirstNameOz());
        infoPinpp.setLastName(first.getLastNameOz());
        infoPinpp.setMiddleName(first.getMiddleNameOz());
        infoPinppRespository.save(infoPinpp);
    }

}
