package com.malaka.aat.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.AuthException;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.MalakaInternalClient;
import com.malaka.aat.external.dto.course.CourseDto;
import com.malaka.aat.external.dto.module.ModuleDto;
import com.malaka.aat.external.dto.test.TestDto;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.repository.GroupRepository;
import com.malaka.aat.external.repository.StudentEnrollmentDetailRepository;
import com.malaka.aat.external.repository.StudentEnrollmentRepository;
import com.malaka.aat.external.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class TopicService {

    private final MalakaInternalClient  malakaInternalClient;
    private final SessionService sessionService;
    private final StudentRepository studentRepository;
    private final ObjectMapper objectMapper;
    private final GroupRepository groupRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final StudentEnrollmentDetailRepository studentEnrollmentDetailRepository;

    public ResponseEntity<Resource> streamTopicContent(String groupId, String topicId, String rangeHeader) {
        validateIfTopicAccessibleToStudent(groupId, topicId, 1);

        // Pass the Range header to the Feign client
        return malakaInternalClient.streamContentFile(topicId, rangeHeader);
    }

    public ResponseEntity<Resource> getLectureFile(String groupId, String topicId) {
        validateIfTopicAccessibleToStudent(groupId, topicId, 2);

        return malakaInternalClient.lectureFile(topicId);
    }

    public ResponseEntity<Resource> getPresentationFile(String groupId, String topicId) {
        validateIfTopicAccessibleToStudent(groupId, topicId, 3);

        return malakaInternalClient.presentationFile(topicId);
    }

    public BaseResponse getTopicTest(String groupId, String topicId) {
        validateIfTopicAccessibleToStudent(groupId, topicId, 4);
        BaseResponse response = new BaseResponse();
        BaseResponse responseFromInternal = malakaInternalClient.getTest(topicId);
        if (responseFromInternal.getResultCode() != 0) {
            return responseFromInternal;
        }
        TestDto testDto = objectMapper.convertValue(responseFromInternal.getData(), TestDto.class);
        response.setData(testDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }


    private void validateIfTopicAccessibleToStudent(String groupId, String topicId, Integer contentStep) {
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new NotFoundException("Student not found for the current user"));
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("The group doesn't exist with id: " + groupId));
        if (!group.getStudents().contains(student)) {
            throw new BadRequestException("Student doesnt' belong tho this group");
        }
        BaseResponse response = malakaInternalClient.getCourseByTopicId(topicId);
        if (response.getResultCode() != 0) {
            throw new SystemException(response.getResultNote());
        }
        CourseDto courseDto = objectMapper.convertValue(response.getData(), CourseDto.class);
        StudentEnrollment studentEnrollment = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, courseDto.getId(), group)
                .orElseThrow(() -> new NotFoundException("Student enrollment not found for the current user"));
        StudentEnrollmentDetail studentEnrollmentDetail = studentEnrollmentDetailRepository.findLastByStudentEnrollment(studentEnrollment)
                .orElseThrow(() -> new SystemException("Student enrollment detail not found for the current student enrollment"));
        for (int i = 0; i < studentEnrollmentDetail.getModuleStep(); i++) {
            ModuleDto moduleDto = courseDto.getModules().get(i);
            int max;
            if (i+1 == studentEnrollmentDetail.getModuleStep()) {
                max = studentEnrollmentDetail.getTopicStep();
            } else {
                max = moduleDto.getTopics().size();
            }
            for (int j = 0; j < max; j++) {
               if ( moduleDto.getTopics().get(j).getId().equals(topicId)) {
                    return;
                }
            }
        }

        throw new AuthException("Student is not authorized to get the content of this topic");
    }
}
