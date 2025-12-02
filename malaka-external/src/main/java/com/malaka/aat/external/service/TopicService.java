package com.malaka.aat.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.AuthException;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.malaka_internal.MalakaInternalClient;
import com.malaka.aat.external.dto.course.external.CourseDto;
import com.malaka.aat.external.dto.course.internal.QuestionOptionDto;
import com.malaka.aat.external.dto.course.internal.TestQuestionDto;
import com.malaka.aat.external.dto.course.internal.TopicDto;
import com.malaka.aat.external.dto.module.ModuleDto;
import com.malaka.aat.external.dto.test.attempt.TestAttemptRequestDto;
import com.malaka.aat.external.dto.test.attempt.TestAttemptRequestDtoItem;
import com.malaka.aat.external.dto.test.attempt.TestAttemptResponseDto;
import com.malaka.aat.external.dto.test.attempt.TestAttemptResponseDtoItem;
import com.malaka.aat.external.dto.test.without_answer.TestDto;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


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
    private final StudentTestAttemptRepository studentTestAttemptRepository;
    private final StudentEnrollmentService studentEnrollmentService;

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
        BaseResponse responseFromInternal = malakaInternalClient.getTestByTopicId(topicId);
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
               if ( moduleDto.getTopics().get(j).getId().equals(topicId) && studentEnrollmentDetail.getContentStep() >= contentStep) {
                    return;
                }
            }
        }

        throw new AuthException("Student is not authorized to get the content of this topic");
    }

    @Transactional
    public BaseResponse testAttempt(String groupId, String topicId, TestAttemptRequestDto testAttemptDto) {
        validateIfTopicAccessibleToStudent(groupId, topicId, 4);
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new NotFoundException("Student not found for the current user"));

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("The group doesn't exist with id: " + groupId));
        List<StudentTestAttempt> testAttempts = studentTestAttemptRepository.findByGroupAndTopicId(group, topicId);
        BaseResponse responseFromInternal = malakaInternalClient.getCourseById(group.getCourseId());
        if (responseFromInternal.getResultCode() != 0) {
            return responseFromInternal;
        }
        com.malaka.aat.external.dto.course.internal.CourseDto courseDto = objectMapper.convertValue(responseFromInternal.getData(), com.malaka.aat.external.dto.course.internal.CourseDto.class);
        List<com.malaka.aat.external.dto.course.internal.ModuleDto> modules = courseDto.getModules();
        com.malaka.aat.external.dto.course.internal.ModuleDto moduleDto = modules.stream().filter(
                m -> {
                    return m.getTopics().stream().anyMatch(
                            t -> t.getId().equals(topicId)
                    );
                }
        ).findFirst().orElseThrow(() -> new NotFoundException("The topic doesn't exist with id: " + topicId));
        TopicDto topicDto = moduleDto.getTopics().stream().filter(t -> t.getId().equals(topicId)).findFirst().orElseThrow();
        com.malaka.aat.external.dto.course.internal.TestDto testDto = topicDto.getTestDto();


        validateTestAttempt(testAttemptDto, testDto, testAttempts);
        StudentEnrollment studentEnrollment = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, courseDto.getId(), group)
                .orElseThrow(() -> new NotFoundException("Student enrollment not found for the current user"));
        StudentEnrollmentDetail lastStudentEnrollmentDetail = studentEnrollmentDetailRepository.findLastByStudentEnrollment(studentEnrollment).orElseThrow(
                () -> new NotFoundException("Student enrollment detail not found for the current user"));

        int correctAnswers = calculateCorrectAnswers(testDto, testAttemptDto);
        StudentTestAttempt studentTestAttempt = new  StudentTestAttempt();
        studentTestAttempt.setStudent(student);
        studentTestAttempt.setCorrectAnswers(correctAnswers);
        studentTestAttempt.setTestId(testDto.getId());
        studentTestAttempt.setTopicId(topicId);
        studentTestAttempt.setGroup(group);
        int percentage = convertCorrectAnswerQuantityToPercentage(correctAnswers, testDto.getQuestions().size());
        studentTestAttempt.setCorrectAnswerPercentage(percentage);
        studentTestAttempt.setAttemptNumber(testAttempts.size()+1);
        studentTestAttempt.setTotalQuestions(testDto.getQuestions().size());
        if (percentage >= 70) {
            studentEnrollmentService.updateAndSaveStepOfModule(
                    moduleDto.getTopicCount(),
                    courseDto.getModuleCount(),
                    studentEnrollment,
                    lastStudentEnrollmentDetail
            );
            studentTestAttempt.setIsSuccess((short) 1);
            studentTestAttemptRepository.save(studentTestAttempt);
        } else {
            studentTestAttempt.setIsSuccess((short) 0);
            testAttempts.add(studentTestAttempt);
            studentTestAttemptRepository.save(studentTestAttempt);
            if (testAttempts.size() >= testDto.getAttemptLimit()) {
                if (testAttempts.stream().filter(a -> a.getIsSuccess().equals(0) ).findFirst().isEmpty()) {
                    lastStudentEnrollmentDetail.setIsActive((short) 0);
                    studentEnrollmentDetailRepository.save(lastStudentEnrollmentDetail);
                    StudentEnrollmentDetail newStudentEnrollmentDetail = new StudentEnrollmentDetail();
                    newStudentEnrollmentDetail.setContentStep(1);
                    newStudentEnrollmentDetail.setModuleStep(lastStudentEnrollmentDetail.getModuleStep());
                    newStudentEnrollmentDetail.setTopicStep(lastStudentEnrollmentDetail.getTopicStep());
                    newStudentEnrollmentDetail.setStudentEnrollment(studentEnrollment);
                    newStudentEnrollmentDetail.setStudentEnrollment(studentEnrollment);
                    newStudentEnrollmentDetail.setIsActive((short) 1);
                    studentEnrollmentDetailRepository.save(newStudentEnrollmentDetail);
                    studentTestAttemptRepository.deleteAll(testAttempts);
                }
            }
        }


        BaseResponse response = new BaseResponse();
        List<TestAttemptResponseDtoItem> list = testAttempts.stream().map(this::mapTestAttemptEntityToDto).toList();
        response.setData(list);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private int convertCorrectAnswerQuantityToPercentage(int correctAnswers, int questionCount) {
        if (questionCount == 0) {
            return 0;
        }
        double percentage = ((double) correctAnswers / questionCount) * 100;
        return (int) Math.round(percentage);
    }

    public void validateTestAttempt(TestAttemptRequestDto testAttemptDto,
                                    com.malaka.aat.external.dto.course.internal.TestDto testDto,
                                    List<StudentTestAttempt> testAttempts
    ) {
        if (testAttempts.size() >= testDto.getAttemptLimit()) {
            throw new BadRequestException("Student already reached its limit");
        }

        List<TestAttemptRequestDtoItem> answers = testAttemptDto.getAnswers();
        if (answers.size() > testDto.getQuestions().size()) {
            throw new BadRequestException("The size of answers must not be greater than the size of questions");
        }
        List<String> questionIds = answers.stream().map(TestAttemptRequestDtoItem::getQuestionId).toList();
        questionIds.forEach(questionId1 -> {
            int size = questionIds.stream().filter(questionId1::equals).toList().size();
            if (size > 1) {
                throw new BadRequestException("Question id must be unique among answers");
            }
        });
    }

    public int calculateCorrectAnswers(com.malaka.aat.external.dto.course.internal.TestDto testDto , TestAttemptRequestDto testAttemptDto) {
        AtomicInteger correctAnswers = new AtomicInteger(0);

        testAttemptDto.getAnswers().forEach(answer -> {
            TestQuestionDto testQuestionDto = testDto.getQuestions().
                    stream().filter
                            (q -> q.getId().equals(answer.getQuestionId()))
                    .findFirst().orElseThrow(() ->
                            new NotFoundException("Test question not found with id: " + answer.getQuestionId()));
            QuestionOptionDto questionOptionDto = testQuestionDto.getOptions().stream().filter(o ->
                    o.getId().equals(answer.getOptionId())).findFirst().orElseThrow(() -> new NotFoundException("Option not found with id: " + answer.getOptionId()));
            if (questionOptionDto.getIsCorrect().equals((short) 1)) {
                int i = correctAnswers.incrementAndGet();
                correctAnswers.set(i);
            }
        });
        return correctAnswers.get();
    }

    public BaseResponse testAttemptList(String groupId, String topicId) {
        BaseResponse response = new BaseResponse();
        validateIfTopicAccessibleToStudent(groupId, topicId, 4);

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id: " + groupId));
        BaseResponse responseFromInternal = malakaInternalClient.getTestByTopicId(topicId);
        if (responseFromInternal.getResultCode() != 0) {
            return responseFromInternal;
        }
        com.malaka.aat.external.dto.test.with_answer.TestDto testDto = objectMapper.convertValue(responseFromInternal.getData(),  com.malaka.aat.external.dto.test.with_answer.TestDto.class);
        List<StudentTestAttempt> testAttemptEntities = studentTestAttemptRepository.findByGroupAndTopicId(group, topicId);
        List<TestAttemptResponseDtoItem> itemDtos = testAttemptEntities.stream().map(this::mapTestAttemptEntityToDto)
                .sorted(Comparator.comparing(TestAttemptResponseDtoItem::getAttemptNumber)).toList();
        TestAttemptResponseDto dto = new TestAttemptResponseDto();
        dto.setAttempts(itemDtos);
        dto.setTotalAttempts(testDto.getAttemptLimit());
        dto.setAttemptsLeft(testDto.getAttemptLimit() -  itemDtos.size());

        response.setData(dto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public TestAttemptResponseDtoItem mapTestAttemptEntityToDto(StudentTestAttempt entity) {
        TestAttemptResponseDtoItem dto = new TestAttemptResponseDtoItem();
        dto.setAttemptNumber(entity.getAttemptNumber());
        dto.setCorrectAnswers(entity.getCorrectAnswers());
        dto.setTotalQuestions(entity.getTotalQuestions());
        dto.setTime(entity.getInstime());
        dto.setCorrectAnswerPercentage(entity.getCorrectAnswerPercentage());
        return dto;
    }

    public BaseResponse getCorrectAnswers(String topicId) {
        BaseResponse response = new BaseResponse();

        BaseResponse internalResponse = malakaInternalClient.getTestByTopicId(topicId);
        com.malaka.aat.external.dto.test.with_answer.TestDto testDto = objectMapper.convertValue(internalResponse.getData(), com.malaka.aat.external.dto.test.with_answer.TestDto.class);
        List<TestAttemptRequestDtoItem> list = testDto.getQuestions().stream().map(e -> {
            TestAttemptRequestDtoItem request = new TestAttemptRequestDtoItem();
            request.setQuestionId(e.getId());
            request.setOptionId(
                    e.getOptions().stream().filter(o -> o.getIsCorrect() == 1).findFirst().orElseThrow(() -> new NotFoundException("OptionNotFound")).getId()
            );
            return request;
        }).toList();

        response.setData(list);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }
}
