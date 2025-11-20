package com.malaka.aat.internal.dto.topic;

import com.malaka.aat.internal.dto.test.TestDto;
import com.malaka.aat.internal.model.Topic;
import com.malaka.aat.internal.util.ServiceUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopicDto {

    private String id;
    private String name;
    private String moduleId;
    private String moduleName;
    private Integer order;
    private Integer contentType;
    private String contentFileUrl;
    private String lectureFileUrl;
    private String presentationFileUrl;
    private TestDto testDto;
    private String contentFileId;
    private String lectureFileId;
    private String presentationFileId;
    private String testId;
    private Integer questionCount;
    private Integer attemptLimit;
    private Integer durationInMinutes;

    public TopicDto(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        this.order = topic.getOrder();

        if (topic.getContentType() != null) {
            this.contentType = topic.getContentType().getValue();
        }

        if (topic.getModule() != null) {
            this.moduleId = topic.getModule().getId();
            this.moduleName = topic.getModule().getName();
        }

        if (topic.getContentFile() != null) {
            setUrlForContentFile(topic);
            this.contentFileId = topic.getContentFile().getId();
        }

        if (topic.getLectureFile() != null) {
            this.lectureFileUrl = ServiceUtil.getProjectBaseUrl() + "/api/topic/" + this.id + "/lecture";
            this.lectureFileId = topic.getLectureFile().getId();
        }

        if (topic.getPresentationFile() != null) {
            this.presentationFileUrl = ServiceUtil.getProjectBaseUrl() + "/api/topic/" + this.id + "/presentation";
            this.presentationFileId = topic.getPresentationFile().getId();
        }

        if (topic.getTest() != null) {
            this.testDto = new TestDto(topic.getTest());
            this.testId = topic.getTest().getId();
            this.questionCount = topic.getTest().getQuestions().size();
            this.attemptLimit = topic.getTest().getAttemptLimit();
            this.durationInMinutes = topic.getTest().getDurationInMinutes();
        }
    }

    private void setUrlForContentFile(Topic topic) {
        switch (topic.getContentType()) {
            case VIDEO -> {
                this.contentFileUrl = ServiceUtil.getProjectBaseUrl() + "/api/topic/" + topic.getId() + "/content/stream";
            }
            case AUDIO -> {
                this.contentFileUrl = ServiceUtil.getProjectBaseUrl() + "/api/topic/" + topic.getId() + "/content";
            }
        }
    }
}
