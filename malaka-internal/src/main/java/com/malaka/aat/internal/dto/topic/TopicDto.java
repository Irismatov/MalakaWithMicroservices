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
        }

        if (topic.getLectureFile() != null) {
            this.lectureFileUrl = ServiceUtil.getProjectBaseUrl() + "/api/topic/" + this.id + "/lecture";
        }

        if (topic.getPresentationFile() != null) {
            this.presentationFileUrl = ServiceUtil.getProjectBaseUrl() + "/api/topic/" + this.id + "/presentation";
        }

        if (topic.getTest() != null) {
            this.testDto = new TestDto(topic.getTest());
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
