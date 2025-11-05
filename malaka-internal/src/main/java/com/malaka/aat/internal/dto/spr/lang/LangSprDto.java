package com.malaka.aat.internal.dto.spr.lang;

import com.malaka.aat.internal.model.spr.LangSpr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LangSprDto {
    private Long id;
    private String name;

    public LangSprDto(LangSpr langSpr) {
        if (langSpr != null) {
            this.id = langSpr.getId();
            this.name = langSpr.getName();
        }
    }
}
