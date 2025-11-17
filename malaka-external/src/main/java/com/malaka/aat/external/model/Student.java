package com.malaka.aat.external.model;

import com.malaka.aat.external.enumerators.student.Gender;
import com.malaka.aat.external.model.spr.StudentTypeSpr;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "student")
public class Student extends BaseEntity {

    @Column(name = "id", length = 50, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    private StudentTypeSpr type;
    @Convert(converter = StringListConverter.class)
    private List<String> courseIds = new ArrayList<>();
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "nationality")
    private String nationality;
    private String email;
    @Column(name = "gender")
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    @Converter
    public static class StringListConverter implements AttributeConverter<List<String>, String> {
        @Override
        public String convertToDatabaseColumn(List<String> list) {
            return list == null ? null : String.join(",", list);
        }

        @Override
        public List<String> convertToEntityAttribute(String joined) {
            if (joined == null || joined.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(joined.split(",")));
        }
    }
}
