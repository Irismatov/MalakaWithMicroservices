package com.malaka.aat.internal.dto.test;

import com.malaka.aat.internal.model.QuestionOption;
import com.malaka.aat.internal.util.ServiceUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionOptionDto {

    private String id;
    private String optionText;
    private Short isCorrect;
    private Short hasImage;
    private String imgUrl;

    public QuestionOptionDto(QuestionOption option) {
        this.id = option.getId();
        this.optionText = option.getOptionText();
        this.isCorrect = option.getIsCorrect();
        this.hasImage = option.getHasImage();

        if (option.getImageFile() != null) {
            // Generate file URL: http://localhost:8585/api/file/{fileId}
            this.imgUrl = ServiceUtil.getProjectBaseUrl() + "/api/file/" + option.getImageFile().getId();
        }
    }
}
