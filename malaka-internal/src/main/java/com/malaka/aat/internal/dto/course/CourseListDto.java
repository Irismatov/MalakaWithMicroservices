package com.malaka.aat.internal.dto.course;

import com.malaka.aat.internal.model.Course;
import com.malaka.aat.internal.util.ServiceUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseListDto {
    private String id;
    private String name;
    private String description;
    private String imgUrl;
    private int moduleCount;

    public CourseListDto(Course course) {
        this.name = course.getName();
        this.description = course.getDescription();
        this.imgUrl = convertToPublicUrl(course.getFile().getPath());
        this.moduleCount = course.getModuleCount();
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
