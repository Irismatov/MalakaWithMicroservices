package com.malaka.aat.external.service;


import com.malaka.aat.external.enumerators.student_application.StudentApplicationStatus;
import com.malaka.aat.external.model.StudentApplication;
import com.malaka.aat.external.model.spr.StudentApplicationStatusLog;
import com.malaka.aat.external.repository.StudentApplicationRepository;
import com.malaka.aat.external.repository.spr.StudentApplicationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class StudentApplicationLogService {

    private final StudentApplicationLogRepository studentApplicationLogRepository;


    public StudentApplicationStatusLog save (StudentApplication studentApplication, StudentApplicationStatus status, String description) {
        StudentApplicationStatusLog studentApplicationStatusLog = new StudentApplicationStatusLog();
        studentApplicationStatusLog.setApplication(studentApplication);
        studentApplicationStatusLog.setStatusCode(String.valueOf(status.getValue()));
        studentApplicationStatusLog.setStatusName(status.name());
        studentApplicationStatusLog.setDescription(description);
        int theOrderOfNewLog = getTheOrderOfNewLog(studentApplication);
        studentApplicationStatusLog.setOrder(theOrderOfNewLog);
        return studentApplicationLogRepository.save(studentApplicationStatusLog);
    }

    public int getTheOrderOfNewLog(StudentApplication studentApplication) {
        Optional<StudentApplicationStatusLog> lastLog = studentApplicationLogRepository.findLastByApplication(studentApplication);
        return lastLog.map(studentApplicationStatusLog -> studentApplicationStatusLog.getOrder() + 1).orElse(1);
    }

}
