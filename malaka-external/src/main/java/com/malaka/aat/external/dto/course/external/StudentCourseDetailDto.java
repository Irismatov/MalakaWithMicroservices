package com.malaka.aat.external.dto.course.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentCourseDetailDto extends StudentCourseDto {
    public static Integer MAIN_CONTENT = 0;
    public static Integer LECTURE = 1;
    public static Integer PRESENTATION = 2;
    public static Integer TEST = 3;


    private List<Module> modules;

    @Getter
    @Setter
    public static class Module {
        private String id;
        private String name;
        private Integer topicCount;
        private Integer order;
        private String teacherName;
        private int isFinished;
        private int isStarted;
        private List<Topic> topics;
    }

    @Getter
    @Setter
    public static class Topic {
        private String id;
        private String name;
        private Integer order;
        private int isFinished;
        private int isStarted;
        private List<TopicContent> contents;
    }

    @Getter
    @Setter
    public static class TopicContent {
        private String id;
        private int isFinished;
        private int isStarted;
        private Integer type;
    }


    @Getter
    @Setter
    public static class TopicMainContent extends TopicContent {
        private Integer contentType;
    }

    @Getter
    @Setter
    public static class TopicMainContentVideAudio extends TopicMainContent {
        private Integer duration;
        private String url;
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
        private Integer durationInMinutes;
    }
}
