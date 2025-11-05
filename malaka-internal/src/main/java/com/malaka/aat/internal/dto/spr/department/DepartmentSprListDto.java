package com.malaka.aat.internal.dto.spr.department;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentSprListDto {
    private String id;
    private String name;
    private DepartmentHeadDto head;
    private FacultyInfoDto faculty;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentHeadDto {
        private String id;
        private String fullName;
        private String pinfl;
        private String phone;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacultyInfoDto {
        private String id;
        private String name;
    }
}
