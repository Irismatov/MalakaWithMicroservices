package com.malaka.aat.external.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "student_application_corporate")
@DiscriminatorValue("CORPORATE")
public class StudentApplicationCorporate extends StudentApplication{

    @Column(name = "pinfls", length = 5000, nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> pinfls;
    @Column(name = "corporate_name", length = 500)
    private String corporateName;
    @Column(name = "stir_number", length = 50)
    private String stirNumber;


    @Converter
    public static class StringListConverter implements AttributeConverter<List<String>, String> {
        @Override
        public String convertToDatabaseColumn(List<String> list) {
            return list == null ? null : String.join(",", list);
        }

        @Override
        public List<String> convertToEntityAttribute(String joined) {
            return joined == null ? null : Arrays.asList(joined.split(","));
        }
    }
}
