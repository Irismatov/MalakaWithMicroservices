package com.malaka.aat.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.external.clients.MalakaInternalClient;
import com.malaka.aat.external.dto.course.CourseDto;
import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.User;
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

    public ResponseEntity<Resource> streamTopicContent(String topicId, String rangeHeader) {
        validateIfTopicAccessibleToStudent(topicId);

        // Pass the Range header to the Feign client
        return malakaInternalClient.streamContentFile(topicId, rangeHeader);
    }

    public ResponseEntity<Resource> getPresentationFile(String topicId) {
        validateIfTopicAccessibleToStudent(topicId);

        return malakaInternalClient.presentationFile(topicId);
    }

    public ResponseEntity<Resource> getLectureFile(String topicId) {
        validateIfTopicAccessibleToStudent(topicId);

        return malakaInternalClient.lectureFile(topicId);
    }


    private void validateIfTopicAccessibleToStudent(String topicId) {
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new NotFoundException("Student not found for the current user"));
        BaseResponse response = malakaInternalClient.getCourseByTopicId(topicId);
        if (response.getResultCode() != 0) {
            throw new SystemException(response.getResultNote());
        }
        CourseDto courseDto = objectMapper.convertValue(response.getData(), CourseDto.class);
        if (!student.getCourseIds().contains(courseDto.getId())) {
            throw new BadRequestException("The student does not belong to the the course of the module");
        }
    }
}
