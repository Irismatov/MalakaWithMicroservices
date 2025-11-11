package com.malaka.aat.internal.enumerators.course;

import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.internal.model.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;
import java.util.Optional;


@AllArgsConstructor
@Getter
public enum CourseState {

    CREATED("001", "Course yangi jo'natildi"),
    SENT_TO_TEACHER("002", "Course o'qituvchiga modullarni to'ldirish uchun jo'natilgan"),
    SENT_TO_FACULTY_HEAD("003", "Course fakultet boshlig'iga tasdiqlaash uchun jo'natilgan"),
    REJECTED("004", "Course qaytarilgan"),
    CANCELLED("005", "Course bekor qilingan"),
    APPROVED("006", "Course fakultet boshlig'i tomonidan tasdiqlangan"),
    RESENT("007", "Course qayta jo'natilgan"),
    READY_TO_SEND_TO_FACULTY_HEAD("008", "Course fakultet boshlig'iga jo'natish uchun tayyor");

    private final String value;
    private final String description;

    public static void setState(Course course, CourseState state) {
        course.setState(state.getValue());
    }

    public static CourseState findByValue(String value) {
        Optional<CourseState> first = Arrays.stream(CourseState.values()).filter(c -> c.value.equals(value)).findFirst();
        return first.orElseThrow(() -> new NotFoundException("Course state not found with value: " + value));
    }

    public static void setState(Course course, String state) {
        CourseState courseState = findByValue(state);
        validateAndSet(course, courseState);
    }

    private static void validateAndSet(Course course, CourseState state) {
        switch (state) {
            case CREATED -> {
                if (course.getState() != null) {
                    throw new BadRequestException("Can't update course: " + course.getId() + " to a state: " + state);
                }
            }
            case SENT_TO_TEACHER -> {
                if (!course.getState().equals("001") || course.getModules().size() != course.getModuleCount()) {
                    throw new BadRequestException("Can't update course: " + course.getId() + " to a state: " + state);
                }
            }
            case SENT_TO_FACULTY_HEAD -> {
                if (!course.getState().equals("008")) {
                    throw new BadRequestException("Can't update course: " + course.getId() + " to a state: " + state);
                }
            }
            case READY_TO_SEND_TO_FACULTY_HEAD -> {
                if (!course.getState().equals("002")) {
                    throw new BadRequestException("Can't update course: " + course.getId() + " to a state: " + state);
                }
            }
            case REJECTED -> {
                if (!course.getState().equals("003") && !course.getState().equals("007")) {
                    throw new BadRequestException("Can't update course: " + course.getId() + " to a state: " + state);
                }
            }
            case CANCELLED -> {
                if (!course.getState().equals("006")) {
                    throw new BadRequestException("Can't update course: " + course.getId() + " to a state: " + state);
                }
            }
            case APPROVED -> {
                if (!course.getState().equals("003") &&  !course.getState().equals("007")) {
                    throw new BadRequestException("Can't update course: " + course.getId() + " to a state: " + state);
                }
            }
            case RESENT -> {
                if (!course.getState().equals("004")) {
                    throw new BadRequestException("Can't update course: " + course.getId() + " to a state: " + state);
                }
            }
        }
        course.setState(state.getValue());
    }

}