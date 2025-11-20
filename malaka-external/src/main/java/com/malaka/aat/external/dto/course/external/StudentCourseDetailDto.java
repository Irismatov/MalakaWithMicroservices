package com.malaka.aat.external.dto.course.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentCourseDetailDto extends StudentCourseDto {
    private List<Module> modules;
    private List<Topic> topics;

    @Getter
    @Setter
    public static class Module {
        private String id;
        private String name;
        private Integer topicCount;
        private Integer order;
        private String teacherName;
        private int isFinished;
    }

    @Getter
    @Setter
    public static class Topic {
        private String id;
        private String name;
        private Integer order;
        private Integer contentType;
        private int isFinished;
    }

    @Getter
    @Setter
    public static class TopicContent {
        private String id;
        private int isFinished;
    }


    @Getter
    @Setter
    public static class TopicMainContent extends TopicContent {
        private String url;
        private Integer duration;
    }

    @Getter
    @Setter
    public static class TopicLectureOrPresentationContent extends TopicContent {
        private String url;
    }

    @Getter
    @Setter
    public static class TopicTestContent extends TopicContent {
        private Integer totalAttempts;
        private int isAttempted;
        private Integer questionCount;
    }
}
