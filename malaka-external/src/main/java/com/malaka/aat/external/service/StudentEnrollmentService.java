package com.malaka.aat.external.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.model.Group;
import com.malaka.aat.external.repository.GroupRepository;
import com.malaka.aat.external.repository.StudentEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StudentEnrollmentService {

    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final GroupRepository groupRepository;

    public BaseResponse findEnrollmentByCourseId(String courseId) {
        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }
}
