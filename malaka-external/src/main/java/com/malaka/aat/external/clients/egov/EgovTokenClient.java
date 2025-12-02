package com.malaka.aat.external.clients.egov;

import com.malaka.aat.core.exception.custom.ClientException;
import com.malaka.aat.core.util.EncryptionUtil;
import com.malaka.aat.external.clients.egov.token.EgovTokenRequest;
import com.malaka.aat.external.clients.egov.token.EgovTokenResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
public class EgovTokenClient {

    private static final Logger logger = LoggerFactory.getLogger(EgovTokenClient.class);


    @Value("${clients.egov.token.url}")
    private String tokenUrl;

    @Value("${clients.egov.token.token-path}")
    private String tokenPath;

    @Value("${clients.egov.token.basic-auth.username}")
    private String basicAuthUsername;

    @Value("${clients.egov.token.basic-auth.password}")
    private String basicAuthPassword;

    @Value("${clients.egov.token.username}")
    private String tokenUsername;

    @Value("${clients.egov.token.password}")
    private String tokenPassword;

    @Value("${clients.egov.token.grant-type}")
    private String grantType;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        String authHeader = "Basic " + EncryptionUtil.encodeBasicAuth(basicAuthUsername, basicAuthPassword);

        webClient = webClientBuilder
                .baseUrl(tokenUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        authHeader)
                .build();
    }


    @Cacheable(value = "egovTokens")
    public String getToken() {
        EgovTokenRequest egovTokenRequest = new EgovTokenRequest();
        egovTokenRequest.setGrant_type(grantType);
        egovTokenRequest.setUsername(tokenUsername);
        egovTokenRequest.setPassword(tokenPassword);

        logger.info("Sending a request for token to the url {} with body: {} ", tokenPath, egovTokenRequest);

        EgovTokenResponse tokenResponse = webClient
                .post()
                .uri(tokenPath)
                .bodyValue(egovTokenRequest)
                .retrieve()
                .bodyToMono(EgovTokenResponse.class)
                .timeout(Duration.ofSeconds(10))
                .block();

        if (tokenResponse == null) {
            throw new ClientException("Token is null");
        }

        logger.info("Received a response from a request to the url {} with body: {} ", tokenPath, tokenResponse);
        return tokenResponse.getAccess_token();
    }
}
