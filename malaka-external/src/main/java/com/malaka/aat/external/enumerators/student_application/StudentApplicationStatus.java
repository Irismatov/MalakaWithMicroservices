package com.malaka.aat.external.enumerators.student_application;

import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.external.model.StudentApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum  StudentApplicationStatus {
    CREATED(0),
    ACCEPTED(1),
    REJECTED(2);

    private final int value;

    public static void setStatus(StudentApplication application, int status) {
        validateIfUpdatable(application, status);

        Optional<StudentApplicationStatus> first = Arrays.stream(StudentApplicationStatus.values()).filter(s -> s.getValue() == status).findFirst();
        if (first.isPresent()) {
            application.setStatus(first.get());
        } else {
            throw new NotFoundException(("Application status not found with value: " + status));
        }
    }

    private static void validateIfUpdatable(StudentApplication application, int toStatus) {

        if (application.getStatus().getValue() != 0 && (toStatus == 2 || toStatus == 1)) {
            throw new BadRequestException("Application status cannot be updated to " + toStatus);
        }
    }
}
