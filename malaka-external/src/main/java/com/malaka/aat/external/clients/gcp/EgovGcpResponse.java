package com.malaka.aat.external.clients.gcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@ToString
@Getter
@Setter
public class EgovGcpResponse {
    @JsonProperty("result")
    private Integer result;
    @JsonProperty("data")
    private List<EgovGcpResponseData> data;
    @JsonProperty("comments")
    private String comments;



    @ToString
    @Getter
    @Setter
    public static class EgovGcpResponseData {
        @JsonProperty("transaction_id")
        private String transactionId;
        @JsonProperty("current_pinpp")
        private String currentPinpp;
        @JsonProperty("pinpps")
        private List<String> pinpps;
        @JsonProperty("current_document")
        private String currentDocument;
        @JsonProperty("documents")
        private List<Document> documents;
        @JsonProperty("surnamelat")
        private String lastNameOz;
        @JsonProperty("namelat")
        private String firstNameOz;
        @JsonProperty("patronymlat")
        private String middleNameOz;
        @JsonProperty("surnamecyr")
        private String lastNameUz;
        @JsonProperty("namecyr")
        private String firstNameUz;
        @JsonProperty("patronymcyr")
        private String middleNameUz;
        @JsonProperty("engsurname")
        private String lastNameEn;
        @JsonProperty("engname")
        private String firstNameEn;
        @JsonProperty("birth_date")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;
        @JsonProperty("birthplace")
        private String birthPlace;
        @JsonProperty("birthcountry")
        private String birthCountry;
        @JsonProperty("birthCountryId")
        private String birthCountryId;
        @JsonProperty("livestatus")
        private String liveStatus;
        @JsonProperty("nationality")
        private String nationality;
        @JsonProperty("nationalityid")
        private String nationalityId;
        @JsonProperty("citizenship")
        private String citizenship;
        @JsonProperty("citizenshipid")
        private String citizenshipId;
        @JsonProperty("sex")
        private String sex;
    }

    @ToString
    @Getter
    @Setter
    public static class Document {
        @JsonProperty("document")
        private String document;
        @JsonProperty("type")
        private String type;
        @JsonProperty("docgiveplace")
        private String docGivePlace;
        @JsonProperty("docgiveplaceid")
        private String docGivePlaceId;
        @JsonProperty("datebegin")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateBegin;
        @JsonProperty("dateend")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateEnd;
        @JsonProperty("status")
        private Integer status;
    }
}
