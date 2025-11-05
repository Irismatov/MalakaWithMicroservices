package com.malaka.aat.internal.dto.test;

import com.malaka.aat.internal.model.TestQuestion;
import com.malaka.aat.internal.util.ServiceUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TestQuestionDto {

    private String id;
    private String questionText;
    private Short hasImage;
    private String imgUrl;
    private List<QuestionOptionDto> options;

    public TestQuestionDto(TestQuestion question) {
        this.id = question.getId();
        this.questionText = question.getQuestionText();
        this.hasImage = question.getHasImage();

        if (question.getQuestionImage() != null) {
            // Generate file URL: http://localhost:8585/api/file/{fileId}
            this.imgUrl = ServiceUtil.getProjectBaseUrl() + "/api/file/" + question.getQuestionImage().getId();
        }

        if (question.getOptions() != null) {
            this.options = question.getOptions().stream()
                    .map(QuestionOptionDto::new)
                    .collect(Collectors.toList());
        }
    }
}
