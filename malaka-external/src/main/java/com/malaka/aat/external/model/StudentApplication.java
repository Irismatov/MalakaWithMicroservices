package com.malaka.aat.external.model;

import com.malaka.aat.external.enumerators.student_application.StudentApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Arrays;
import java.util.List;


@Entity
@Table(name = "student_application")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "application_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("BASE")
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE student_application SET is_deleted = 1 WHERE id = ?")
public class StudentApplication extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;
    @Column(name = "number", length = 50, nullable = false)
    private String number;
    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "status", columnDefinition = " INT default 0")
    private StudentApplicationStatus status;
    @Column(name = "course_id", length = 50, nullable = false)
    private String courseId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;
    @Column(name = "phone", length = 50)
    private String phone;


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
