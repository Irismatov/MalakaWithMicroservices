package com.malaka.aat.internal.dto.module;

import com.malaka.aat.internal.model.Module;
import com.malaka.aat.internal.dto.topic.TopicDto;
import com.malaka.aat.internal.model.Topic;
import com.malaka.aat.internal.model.spr.StateHMet;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class ModuleDto {
    private String id;
    private String name;
    private Integer topicCount;
    private Integer order;
    private String teacherId;
    private String teacherName;
    private String departmentId;
    private String departmentName;
    private String facultyId;
    private String facultyName;
    private String state;
    private String courseId;
    private String courseName;
    private String rejectionDescription;
    private List<TopicDto> topics;

    public ModuleDto(Module module) {
        this.id = module.getId();
        this.name = module.getName();
        this.topicCount = module.getTopicCount();
        this.order = module.getOrder();
        this.teacherId = module.getTeacher().getId();
        this.teacherName =
                (module.getTeacher().getFirstName() != null ? module.getTeacher().getFirstName() : "") + " " +
                        (module.getTeacher().getLastName() != null ? module.getTeacher().getLastName() : "");
        this.departmentName = module.getDepartment().getName();
        this.facultyId = module.getFaculty().getId();
        this.facultyName = module.getFaculty().getName();
        this.departmentId = module.getDepartment().getID();
        this.departmentName = module.getDepartment().getName();
        this.state = module.getModuleState();

        if (module.getCourse() != null) {
            this.courseId = module.getCourse().getId();
            this.courseName = module.getCourse().getName();
        }

        if (module.getTopics() != null) {
            this.topics = module.getTopics().stream()
                    .sorted(Comparator.comparing(Topic::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(TopicDto::new).toList();
        }

        if (module.getModuleState().equals("004")) {
            List<StateHMet> stateHis = module.getStateHMets().stream().sorted(Comparator.comparing(StateHMet::getInstime)).toList();
            if (!stateHis.isEmpty()) {
                StateHMet stateHMet = stateHis.get(stateHis.size() - 1);
                this.rejectionDescription = stateHMet.getDescriptions();
            }
        }
    }
}
