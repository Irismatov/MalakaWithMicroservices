package com.malaka.aat.internal.model;

import com.malaka.aat.internal.model.spr.CourseFormatSpr;
import com.malaka.aat.internal.model.spr.CourseStudentTypeSpr;
import com.malaka.aat.internal.model.spr.CourseTypeSpr;
import com.malaka.aat.internal.model.spr.StateHMet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.util.List;

@Table(name = "course")
@Entity
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE course SET is_deleted = 1 WHERE id = ?")
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, length = 50)
    private String id;
    @Column(name = "name", length = 200, columnDefinition = "VARCHAR(200)")
    private String name;
    @Column(name = "description", length = 1500, columnDefinition = "VARCHAR(1500)")
    private String description;
    @Column(name = "state", length = 5)
    private String state;
    @Column(name = "module_count",  nullable = false)
    private Integer moduleCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", referencedColumnName = "id", nullable = false)
    private File file;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_type_id", referencedColumnName = "id", nullable = false)
    private CourseTypeSpr courseType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_format_id", referencedColumnName = "id", nullable = false)
    private CourseFormatSpr courseFormat;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_student_type_id", referencedColumnName = "id", nullable = false)
    private CourseStudentTypeSpr courseStudentType;
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Module> modules;
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StateHMet> stateHMets;
}
