package com.malaka.aat.external.clients.one_id;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@RequiredArgsConstructor
@Component
public class OneIdClient {

    @Value("${clients.one-id.client-id}")
    private String clientId;

    @Value("${clients.one-id.client-secret}")
    private String clientSecret;

    @Value("${clients.one-id.redirect-uri}")
    private String redirectUri;

    @Value("${clients.one-id.token.grant-type}")
    private String tokenGrantType;

    @Value("${clients.one-id.token.url}")
    private String tokenUrl;

    @Value("${clients.one-id.info.url}")
    private String infoUrl;

    @Value("${clients.one-id.info.grant-type}")
    private String infoGrantType;

    @Value("${clients.one-id.info.scope}")
    private String scope;

    private final WebClient.Builder webClientBuilder;

    private WebClient tokenWebClient;
    private WebClient infoWebClient;

    @PostConstruct
    public void init() {
        tokenWebClient = webClientBuilder.baseUrl(tokenUrl).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
        infoWebClient = webClientBuilder.baseUrl(infoUrl).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }

    public TokenResponse getToken(String code) {
        log.info("Sending a request to one-id token with a code: {}", code);

        TokenResponse response = tokenWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", tokenGrantType)
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("state", "1")
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
        log.info("Retrieved response from one-id token request: {}", response);
        return response;
    }

    public OneIdInfoResponse getOneIdInfo(String token) {
        log.info("Sending a request to one-id info with a token: {}", token);

        OneIdInfoResponse response = infoWebClient.post()
                .uri(
                        uriBuilder -> uriBuilder
                                .queryParam("grant_type", infoGrantType)
                                .queryParam("client_id", clientId)
                                .queryParam("client_secret", clientSecret)
                                .queryParam("access_token", token)
                                .queryParam("scope", scope)
                                .build()
                )
                .retrieve()
                .bodyToMono(OneIdInfoResponse.class)
                .block();
        log.info("Retrieved response from one-id info request: {}", response);
        return response;
    }



}
