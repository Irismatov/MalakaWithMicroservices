package com.malaka.aat.external.clients;

import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.ClientException;
import com.malaka.aat.core.exception.custom.EgovClientException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.external.clients.gcp.EgovGcpRequest;
import com.malaka.aat.external.clients.gcp.EgovGcpResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class EgovClient {

    private static final Logger logger = LoggerFactory.getLogger(EgovClient.class);


    private final EgovTokenClient egovTokenClient;
    @Value("${clients.egov.main-url}")
    private String url;

    @Value("${clients.egov.mvd-request.address-path}")
    private String mvdAddressPath;

    @Value("${clients.egov.gcp-request.path}")
    private String gcpPath;

    @Value("${clients.egov.gcp-request.lang-id}")
    private String gcpLangId;

    @Value("${clients.egov.gcp-request.is_consent}")
    private String gcpIsConsent;

    @Value("${clients.egov.gcp-request.is_photo}")
    private String gcpIsPhoto;

    @Value("${clients.egov.gcp-request.sender}")
    private String gcpSender;

    @Value("${clients.egov.gcp-request.transaction-id}")
    private String gcpTransactionId;

    @Autowired
    private WebClient.Builder webClientBuilder;


    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = webClientBuilder
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    @Cacheable(value = "egovGcpResponses", key = "#pinpp")
    public EgovGcpResponse getInfo(String pinpp) {

        String token;
        try {
            token = egovTokenClient.getToken();
        } catch (Exception e) {
            logger.error("Error occurred when getting token: {}", e.getMessage());
            throw new EgovClientException(e.getMessage());
        }

        EgovGcpRequest egovGcpRequest = new EgovGcpRequest();
        egovGcpRequest.setTransactionId(gcpTransactionId);
        egovGcpRequest.setIsConsent(gcpIsConsent);
        egovGcpRequest.setLangId(gcpLangId);
        egovGcpRequest.setSender(gcpSender);
        egovGcpRequest.setIsPhoto(gcpIsPhoto);
        egovGcpRequest.setPinpp(pinpp);
        egovGcpRequest.setBirthDate(extractBirthDateFromPinpp(pinpp));

        logger.info("Sending request to {}, body: {}", gcpPath, egovGcpRequest);
        System.out.println("Sending request: " + egovGcpRequest);

        EgovGcpResponse egovGcpResponse = webClient.post()
                .uri(gcpPath)
                .header("Authorization", "Bearer " + token)
                .bodyValue(egovGcpRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .map(body -> new BadRequestException("API error: " + response.statusCode() + " - " + body)))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .map(body -> new BadRequestException("API error: " + response.statusCode() + " - " + body)))
                .bodyToMono(EgovGcpResponse.class)
                .block();
        System.out.println("Response from egov: " + egovGcpResponse);

        validateGcpResponse(egovGcpResponse);

        logger.info("Received response from url: {} with body: {}", gcpPath, egovGcpResponse);
        return egovGcpResponse;
    }

    private void validateGcpResponse(EgovGcpResponse egovGcpResponse) {
        if (egovGcpResponse == null) {
            throw new ClientException("Invalid response from egovGcpResponse");
        }

        Integer result = egovGcpResponse.getResult();
        if (result != 1) {
            throw new ClientException("Gcp API is not returning a success response: " + egovGcpResponse);
        }

        List<EgovGcpResponse.EgovGcpResponseData> data = egovGcpResponse.getData();
        if (data == null || data.isEmpty() || data.get(0) == null) {
            throw new ClientException("Invalid response from egovGcpResponse: " + egovGcpResponse);
        }
    }


    private LocalDate extractBirthDateFromPinpp(String pinpp) {
        String birthdate = pinpp.substring(1, 7);

        int day = Integer.parseInt(birthdate.substring(0, 2));
        int month = Integer.parseInt(birthdate.substring(2, 4));
        int year = Integer.parseInt(birthdate.substring(4, 6));
        year += (year < LocalDate.now().getYear() % 100) ? 2000 : 1900;

        return LocalDate.of(year, month, day);
    }



}
