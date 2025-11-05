package com.malaka.aat.external.dto.course;

import com.malaka.aat.internal.dto.StateHMet.StateHMetDto;
import com.malaka.aat.internal.dto.module.ModuleDto;
import com.malaka.aat.internal.model.BaseEntity;
import com.malaka.aat.internal.model.Course;
import com.malaka.aat.internal.model.Module;
import com.malaka.aat.internal.util.ServiceUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CourseDto {
    private String id;
    private String name;
    private String description;
    private Integer moduleCount;
    private Long lang;
    private String state;
    private String imgUrl;
    private Long courseType;
    private Long courseFormat;
    private Long courseStudentType;
    private List<ModuleDto> modules;
    private List<StateHMetDto> history;

    public CourseDto(Course course) {
        this.id = course.getId();
        this.name = course.getName();
        this.description = course.getDescription();
        this.moduleCount = course.getModuleCount();
        this.lang = course.getLang().getId();
        if (course.getCourseType() != null) {
            this.courseType = course.getCourseType().getId();
        }
        this.state = course.getState();
        if (course.getCourseFormat() != null) {
            this.courseFormat = course.getCourseFormat().getId();
        }
        if (course.getCourseStudentType() != null) {
            this.courseStudentType = course.getCourseStudentType().getId();
        }

        // Set public image URL if file exists
        if (course.getFile() != null && course.getFile().getPath() != null) {
            this.imgUrl = convertToPublicUrl(course.getFile().getPath());
        } else {
            this.imgUrl = null;
        }

        if (course.getModules() != null) {
            this.modules = course.getModules().stream()
                    .sorted(Comparator.comparing(Module::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(ModuleDto::new)
                    .toList();
        }
        if (course.getStateHMets() != null) {
            this.history = course.getStateHMets().stream()
                    .sorted(Comparator.comparing(BaseEntity::getInstime, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(StateHMetDto::new).toList();
        }
    }

    private String convertToPublicUrl(String absolutePath) {
        if (absolutePath == null) {
            return null;
        }

        String baseUrl = ServiceUtil.getProjectBaseUrl();

        // Extract relative path after the base directory
        // Assuming path format: D:/MalakaFiles/images/2025/01/15/abc123.jpg
        // We want: /uploads/images/2025/01/15/abc123.jpg

        String[] pathParts = absolutePath.replace("\\", "/").split("/");
        StringBuilder relativePath = new StringBuilder();

        // Find where the date structure starts (yyyy/mm/dd pattern)
        boolean foundDateStructure = false;
        for (int i = 0; i < pathParts.length; i++) {
            if (!foundDateStructure && pathParts[i].matches("\\d{4}")) {
                foundDateStructure = true;
            }
            if (foundDateStructure) {
                relativePath.append("/").append(pathParts[i]);
            }
        }

        return baseUrl + "/uploads/images" + relativePath.toString();
    }
}
