package com.malaka.aat.internal.model;


import com.malaka.aat.internal.model.spr.DepartmentSpr;
import com.malaka.aat.internal.model.spr.FacultySpr;
import com.malaka.aat.internal.model.spr.StateHMet;
import com.malaka.aat.internal.model.spr.StateHModule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Table(name = "module")
@Entity
@Getter
@Setter
@SQLRestriction("is_deleted = 0")
@SQLDelete(sql = "UPDATE module SET is_deleted = 1 WHERE id = ?")
public class Module extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;
    @Column(name = "name", length = 200, columnDefinition = "VARCHAR(200) CCSID 1208")
    private String name;
    @Column(name = "topic_count")
    private Integer topicCount;
    @Column(name = "module_order")
    private Integer order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private User teacher;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private FacultySpr faculty;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentSpr department;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "module")
    private List<Topic> topics;
    @JoinColumn(name = "module_state")
    private String moduleState;
    @OneToMany(mappedBy = "module", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<StateHModule> stateHistory = new ArrayList<>();
}
