package com.malaka.aat.core.dao;

public interface CourseLastGroupOrderProjection {
    // Note: Column names must match the SQL result names/aliases
    String getCourseId();
    Integer getMaxOrderNumber(); // Match the MAX() result
}