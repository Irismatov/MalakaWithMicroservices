package com.malaka.aat.internal.dto.spr.faculty;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacultySprListDto {
    private String id;
    private String name;
    private FacultyHeadDto head;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacultyHeadDto {
        private String id;
        private String fullName;
        private String pinfl;
        private String phone;
    }
}
