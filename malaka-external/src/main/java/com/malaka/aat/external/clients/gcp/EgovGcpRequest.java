package com.malaka.aat.external.clients.gcp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@Setter
public class EgovGcpRequest {
    @JsonProperty("langId")
    private String langId;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("is_consent")
    private String isConsent;
    @JsonProperty("pinpp")
    private String pinpp;
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @JsonProperty("is_photo")
    private String isPhoto;
    @JsonProperty("Sender")
    private String sender;
}
