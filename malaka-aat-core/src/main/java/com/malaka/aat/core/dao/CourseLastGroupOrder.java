package com.malaka.aat.core.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseLastGroupOrder {
    private String courseId;
    private Integer maxOrderNumber;
}