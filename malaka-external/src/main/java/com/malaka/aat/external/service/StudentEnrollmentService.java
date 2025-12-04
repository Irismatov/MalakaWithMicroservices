package com.malaka.aat.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.malaka_internal.MalakaInternalClient;
import com.malaka.aat.external.dto.course.external.CourseDto;
import com.malaka.aat.external.dto.course.internal.TestDto;
import com.malaka.aat.external.dto.enrollment.StudentEnrollmentDetailDto;
import com.malaka.aat.external.dto.module.ModuleDto;
import com.malaka.aat.external.dto.topic.TopicDto;
import com.malaka.aat.core.enumerators.CourseContentType;
import com.malaka.aat.external.enumerators.TestAttemptState;
import com.malaka.aat.external.enumerators.TestAttemptType;
import com.malaka.aat.external.enumerators.group.GroupStatus;
import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentDetailType;
import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentStatus;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StudentEnrollmentService {

    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final GroupRepository groupRepository;
    private final SessionService sessionService;
    private final StudentRepository studentRepository;
    private final StudentEnrollmentDetailRepository studentEnrollmentDetailRepository;
    private final MalakaInternalClient malakaInternalClient;
    private final ObjectMapper objectMapper;
    private final StudentTestAttemptRepository studentTestAttemptRepository;


    @Transactional
    public BaseResponse updateOrCreateStudentEnrollment(String groupId) {
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new NotFoundException("Student not found"));


        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isEmpty() || !group.get().getStudents().contains(student)) {
            throw new BadRequestException("This student can't create or update a student enrollment");
        }
        Optional<StudentEnrollment> studentEnrollmentOptional = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, group.get().getCourseId(), group.get());

        BaseResponse response = malakaInternalClient.getCourseById(group.get().getCourseId());
        CourseDto course = objectMapper.convertValue(response.getData(), CourseDto.class);

        StudentEnrollmentDetail newStudentEnrollmentDetail;


        StudentEnrollmentDetail savedDetail;
        if (studentEnrollmentOptional.isPresent()) {
            StudentEnrollment studentEnrollment = studentEnrollmentOptional.get();

            // Existing enrollment - increment steps
            StudentEnrollmentDetail lastStudentEnrollmentDetail = studentEnrollmentDetailRepository
                    .findLastByStudentEnrollment(studentEnrollmentOptional.get())
                    .orElseThrow(() -> new SystemException("Student enrollment detail not found"));

            lastStudentEnrollmentDetail.setIsActive((short) 0);

            if (lastStudentEnrollmentDetail.getContentStep() == 4) {
                throw new BadRequestException("Enrollment detail can only be updated with passing a test at this point");
            }

            studentEnrollmentDetailRepository.save(lastStudentEnrollmentDetail);

            int currentModuleStep = lastStudentEnrollmentDetail.getModuleStep();

            if (course.getModules() != null && !course.getModules().isEmpty()) {
                com.malaka.aat.external.dto.module.ModuleDto currentModule = course.getModules().stream()
                        .filter(m -> m.getOrder().equals(currentModuleStep))
                        .findFirst()
                        .orElseThrow(() -> new SystemException("Module not found for step: " + currentModuleStep));
                savedDetail =
                        updateAndSaveStepOfModule(currentModule.getTopicCount(), course.getModuleCount(), studentEnrollment, lastStudentEnrollmentDetail);

            } else {
                throw new BadRequestException("Course has no modules");
            }
        } else {
            // New enrollment - start from step 1
            StudentEnrollment studentEnrollment = new StudentEnrollment();
            studentEnrollment.setStudent(student);
            studentEnrollment.setCourseId(group.get().getCourseId());
            studentEnrollment.setGroup(group.get());
            studentEnrollment.setStatus(StudentEnrollmentStatus.STARTED);
            StudentEnrollment savedStudentEnrollment = studentEnrollmentRepository.save(studentEnrollment);

            newStudentEnrollmentDetail = new StudentEnrollmentDetail();
            newStudentEnrollmentDetail.setStudentEnrollment(savedStudentEnrollment);
            newStudentEnrollmentDetail.setModuleStep(1);
            newStudentEnrollmentDetail.setTopicStep(1);
            newStudentEnrollmentDetail.setContentStep(1);
            newStudentEnrollmentDetail.setIsActive((short) 1);
            savedDetail = studentEnrollmentDetailRepository.save(newStudentEnrollmentDetail);
        }


        StudentEnrollmentDetailDto dto = new StudentEnrollmentDetailDto(savedDetail);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(dto);
        ResponseUtil.setResponseStatus(baseResponse, ResponseStatus.SUCCESS);
        return baseResponse;
    }

    @Transactional
    public StudentEnrollmentDetail updateAndSaveStepOfModule(
            int topicCount,
            int moduleCount,
            StudentEnrollment studentEnrollment,
            StudentEnrollmentDetail oldStudentEnrollmentDetail) {

        StudentEnrollmentDetail newStudentEnrollmentDetail = new StudentEnrollmentDetail();

        int currentContentStep = oldStudentEnrollmentDetail.getContentStep();
        int currentTopicStep = oldStudentEnrollmentDetail.getTopicStep();
        int currentModuleStep = oldStudentEnrollmentDetail.getModuleStep();

        // Determine content count per topic (lecture + presentation + content = 3)
        int contentsPerTopic = 4;

        // Increment content step
        int newContentStep = currentContentStep + 1;
        int newTopicStep = currentTopicStep;
        int newModuleStep = currentModuleStep;

        // If content step exceeds limit, reset to 1 and increment topic
        if (newContentStep > contentsPerTopic) {
            newContentStep = 1;
            newTopicStep = currentTopicStep + 1;

            // If topic step exceeds the module's topic count, reset to 1 and increment module
            if (newTopicStep > topicCount) {
                newTopicStep = 1;
                newModuleStep = currentModuleStep + 1;

                // If module step exceeds total modules, we've completed the course
                if (newModuleStep > moduleCount) {
                    studentEnrollment.setStatus(StudentEnrollmentStatus.FINISHED);
                    studentEnrollmentRepository.save(studentEnrollment);
                    newModuleStep = moduleCount;
                    newTopicStep = topicCount;
                    newContentStep = contentsPerTopic;

                }
            }
        }

        newStudentEnrollmentDetail.setStudentEnrollment(studentEnrollment);
        newStudentEnrollmentDetail.setModuleStep(newModuleStep);
        newStudentEnrollmentDetail.setTopicStep(newTopicStep);
        newStudentEnrollmentDetail.setContentStep(newContentStep);
        newStudentEnrollmentDetail.setIsActive((short) 1);
        StudentEnrollmentDetail save = studentEnrollmentDetailRepository.save(newStudentEnrollmentDetail);
        return save;
    }

    @Transactional
    public BaseResponse startCourse(String groupId) {

        //validations
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new BadRequestException("Current user is not a student"));

        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Group not found with id " + groupId)
        );

        if (group.getStudents().stream().noneMatch(e -> e.getId().equals(student.getId()))) {
            throw new BadRequestException("Current user does not belong to this group");
        }


        if (group.getStatus() != GroupStatus.STARTED) {
            throw new  BadRequestException("The course can't be started at this group state");
        }

        CourseDto courseDto;
        try {
            BaseResponse responseFromInternalService = malakaInternalClient.getCourseById(group.getCourseId());
            if (responseFromInternalService.getResultCode() != 0) {
                return responseFromInternalService;
            }
            courseDto = objectMapper.convertValue(responseFromInternalService.getData(), CourseDto.class);
        } catch (Exception e) {
            throw new SystemException("Error occurred calling an internal service");
        }

        Optional<StudentEnrollment> oldEnrollment = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, group.getCourseId(), group);
        if (oldEnrollment.isPresent()) {
            throw new BadRequestException("Course has already been started");
        }

        // main logic
        StudentEnrollment enrollment = new StudentEnrollment();
        enrollment.setStudent(student);
        enrollment.setCourseId(group.getCourseId());
        enrollment.setGroup(group);
        enrollment.setStatus(StudentEnrollmentStatus.STARTED);
        enrollment = studentEnrollmentRepository.save(enrollment);


        // find first topic && first content
//        ModuleDto firstModule = courseDto.getModules().stream()
//                .filter(e -> e.getOrder() == 1).findFirst()
//                .orElseThrow(() -> new SystemException("Course module not found at order 1"));
//        TopicDto firstTopic = firstModule.getTopics().stream()
//                .filter(e -> e.getOrder() == 1).findFirst()
//                .orElseThrow(() -> new SystemException("Module topic not found at order 1"));
//
//
//
//        StudentEnrollmentDetail detail = new  StudentEnrollmentDetail();
//        detail.setStudentEnrollment(enrollment);
//        detail.setModuleId(firstModule.getId());
//        detail.setTopicId(firstTopic.getId());
//        detail.setContentId(firstTopic.getContentFileId());
//        studentEnrollmentDetailRepository.save(detail);


        // response
        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse startTask(String groupId, String moduleId, String topicId, String contentId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id: " + groupId));

        User currentUser = sessionService.getCurrentUser();

        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new BadRequestException("Current user is not a student"));
        if (!group.getStudents().contains(student)) {
            throw new BadRequestException("Current user does not belong to this group");
        }

        if (group.getStatus() != GroupStatus.STARTED) {
            throw new BadRequestException("Task can't be started at this group state");
        }

        StudentEnrollment enrollment = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, group.getCourseId(), group).orElseThrow(
                () -> new BadRequestException("Course must have been started before starting a task")
        );
        Optional<StudentEnrollmentDetail> enrollmentDetailFinishOptional = studentEnrollmentDetailRepository.findByModuleIdAndTopicIdAndContentIdAndType(
                enrollment, moduleId, topicId, contentId, StudentEnrollmentDetailType.START
        );

        if (enrollmentDetailFinishOptional.isPresent()) {
            BaseResponse response = new BaseResponse();
            ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
            response.setData("Task is already started");
            return response;
        }

        BaseResponse internalResponse = malakaInternalClient.getCourseById(enrollment.getCourseId());
        if (internalResponse.getResultCode() != 0) {
            return internalResponse;
        }
        CourseDto courseDto = objectMapper.convertValue(internalResponse.getData(), CourseDto.class);
        ModuleDto moduleDto = courseDto.getModules().stream().filter(e -> e.getId().equals(moduleId)).findFirst().orElseThrow(
                () -> new NotFoundException("Module not found with id " + moduleId)
        );

        TopicDto topicDto = moduleDto.getTopics().stream().filter(e -> e.getId().equals(topicId)).findFirst().orElseThrow(
                () -> new NotFoundException("Topic not found with id " + topicId)
        );


        boolean isEligible = isStudentEligibleToStart(enrollment, courseDto, contentId, topicDto, moduleDto);
        if (!isEligible) {
            throw new BadRequestException("Student is not eligible to start a task");
        }

        StudentEnrollmentDetail detail = new  StudentEnrollmentDetail();
        detail.setModuleId(moduleId);
        detail.setTopicId(topicId);
        detail.setContentId(contentId);
        detail.setStudentEnrollment(enrollment);
        detail.setType(StudentEnrollmentDetailType.START);

        if (topicDto.getContentFileId().equals(contentId)) {
            detail.setContentType(CourseContentType.MAIN_CONTENT);
        } else if (topicDto.getLectureFileId().equals(contentId)) {
            detail.setContentType(CourseContentType.LECTURE);
        } else if (topicDto.getPresentationFileId().equals(contentId)) {
            detail.setContentType(CourseContentType.PRESENTATION);
        } else if (topicDto.getTestId().equals(contentId)) {
            detail.setContentType(CourseContentType.TEST);
        }


        studentEnrollmentDetailRepository.save(detail);

        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private boolean isStudentEligibleToStart(StudentEnrollment enrollment, CourseDto courseDto, String contentId, TopicDto currentTopicDto, ModuleDto currentModuleDto) {
        List<ModuleDto> modules = courseDto.getModules();
        ModuleDto firstModule = modules.stream().filter(e -> e.getOrder() == 1).findFirst().orElseThrow();
        TopicDto firstModuleFirstTopic = firstModule.getTopics().stream().filter(e -> e.getOrder() == 1).findFirst().orElseThrow();
        if (firstModuleFirstTopic.getContentType() == 0 || firstModuleFirstTopic.getContentType() == 1) {
            if (firstModuleFirstTopic.getContentFileId().equals(contentId)) {
                return true;
            }
        } else {
            if (firstModuleFirstTopic.getLectureFileId().equals(contentId)) {
                return true;
            }
        }

        List<String> contentIds = studentEnrollmentDetailRepository.
                findContentIdsByStudentEnrollmentAndType(enrollment, StudentEnrollmentDetailType.FINISH);

        if (currentTopicDto.getContentFileId() != null && currentTopicDto.getContentFileId().equals(contentId)) {
            // here logic is a bit incorrect
            ModuleDto moduleDto = courseDto.getModules().stream().filter(e -> e.getOrder() == currentModuleDto.getOrder() - 1).findFirst().orElseThrow();
            TopicDto topicDto = moduleDto.getTopics().stream().filter(e -> Objects.equals(e.getOrder(), moduleDto.getTopicCount())).findFirst().orElseThrow();
            if (contentIds.contains(topicDto.getContentFileId())) {
                return true;
            }
        } else if (currentTopicDto.getLectureFileId().equals(contentId) && contentIds.contains(currentTopicDto.getContentFileId())) {
            return true;
        } else if (currentTopicDto.getPresentationFileId().equals(contentId) && contentIds.contains(currentTopicDto.getLectureFileId())) {
            return true;
        } else if (currentTopicDto.getTestId().equals(contentId) && contentIds.contains(currentTopicDto.getPresentationFileId())) {
            return true;
        }

        return false;
    }

    public BaseResponse finishTask(String groupId, String moduleId, String topicId, String contentId) {

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id " + groupId));
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new NotFoundException("Current user is not a student"));
        if (group.getStatus() != GroupStatus.STARTED) {
            throw new BadRequestException("Group state is not started state");
        }
        if (!group.getStudents().contains(student)) {
            throw new BadRequestException("Student does not belong to this group");
        }
        StudentEnrollment enrollment = studentEnrollmentRepository
                .findByStudentAndCourseIdAndGroup(student, group.getCourseId(), group)
                .orElseThrow(() -> new BadRequestException("Corse has not been started yet"));

        Optional<StudentEnrollmentDetail> enrollmentDetailFinishOptional = studentEnrollmentDetailRepository.findByModuleIdAndTopicIdAndContentIdAndType(
                enrollment, moduleId, topicId, contentId, StudentEnrollmentDetailType.FINISH
        );

        if (enrollmentDetailFinishOptional.isPresent()) {
            BaseResponse response = new BaseResponse();
            ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
            response.setData("Task is already finished");
            return response;
        }

        StudentEnrollmentDetail studentEnrollmentDetailStart = studentEnrollmentDetailRepository.findByModuleIdAndTopicIdAndContentIdAndType(
                enrollment, moduleId, topicId, contentId, StudentEnrollmentDetailType.START
        ).orElseThrow(() -> new NotFoundException("Student is not eligible to finish the task"));

        if (studentEnrollmentDetailStart.getContentType() == CourseContentType.TEST) {
            throw new BadRequestException("Task can't be finished at this endpoint");
        }

        StudentEnrollmentDetail newStudentEnrollmentDetail = new StudentEnrollmentDetail();
        newStudentEnrollmentDetail.setStudentEnrollment(enrollment);
        newStudentEnrollmentDetail.setModuleId(moduleId);
        newStudentEnrollmentDetail.setTopicId(topicId);
        newStudentEnrollmentDetail.setContentId(contentId);
        newStudentEnrollmentDetail.setContentType(studentEnrollmentDetailStart.getContentType());
        newStudentEnrollmentDetail.setType(StudentEnrollmentDetailType.FINISH);
        studentEnrollmentDetailRepository.save(newStudentEnrollmentDetail);
        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    @Transactional
    public BaseResponse startTest(String groupId, String moduleId, String topicId, String testId) {
        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);


        // validation
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id " + groupId));
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new NotFoundException("Current user is not a student"));
        if (!group.getStudents().contains(student)) {
            throw new  BadRequestException("Student does not belong to this group");
        }
        StudentEnrollment enrollment = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, group.getCourseId(), group).orElseThrow(
                () -> new BadRequestException("Course has not been started yet")
        );

        BaseResponse responseInternal = malakaInternalClient.getCourseById(enrollment.getCourseId());
        com.malaka.aat.external.dto.course.internal.CourseDto courseDto = objectMapper.convertValue(responseInternal.getData(), com.malaka.aat.external.dto.course.internal.CourseDto.class);
        com.malaka.aat.external.dto.course.internal.ModuleDto moduleDto = courseDto.getModules().stream().filter(e -> e.getId().equals(moduleId)).findFirst().orElseThrow(() -> new NotFoundException("Module not found with id " + moduleId));
        com.malaka.aat.external.dto.course.internal.TopicDto topicDto = moduleDto.getTopics().stream().filter(e -> e.getId().equals(topicId)).findFirst().orElseThrow(() -> new NotFoundException("Topic not found with id " + topicId));
        TestDto testDto = topicDto.getTestDto();
        if (!testDto.getId().equals(testId)) {
            throw new  BadRequestException("Test not found with id " + testId);
        }


        List<String> moduleIdsByStudentEnrollment = studentEnrollmentDetailRepository.findModuleIdsByStudentEnrollment(enrollment, StudentEnrollmentDetailType.START);
        String s2 = moduleIdsByStudentEnrollment.stream().filter(e -> e.equals(moduleId)).findFirst().orElseThrow(() -> new NotFoundException("Module has not been started yet"));
        List<String> topicIdsByStudentEnrollment = studentEnrollmentDetailRepository.findTopicIdsByStudentEnrollment(enrollment, StudentEnrollmentDetailType.START);
        String s1 = topicIdsByStudentEnrollment.stream().filter(e -> e.equals(topicId)).findFirst().orElseThrow(() -> new NotFoundException("Topic has not been started yet"));
        List<String> contentIdsByStudentEnrollment = studentEnrollmentDetailRepository.findContentIdsByStudentEnrollmentAndType(enrollment, StudentEnrollmentDetailType.START);
        String s = contentIdsByStudentEnrollment.stream().filter(e -> e.equals(testId)).findFirst().orElseThrow(() -> new NotFoundException("Test has not been started yet"));

        List<StudentTestAttempt> attempts = studentTestAttemptRepository.findByGroupAndModuleIdAndTopicIdAndTestIdAndTypeOrderedByOrder(
          group, moduleId, topicId, testId, TestAttemptType.TOPIC_TEST
        );
        Optional<StudentTestAttempt> testAttemptNotFinishedOpt = attempts.stream().filter(
                e -> e.getState() == TestAttemptState.STARTED
        ).findFirst();

        if (testAttemptNotFinishedOpt.isPresent()) {
            StudentTestAttempt studentTestAttemptNotFinished = testAttemptNotFinishedOpt.get();
            LocalDateTime instime = studentTestAttemptNotFinished.getInstime();
            LocalDateTime endTime = instime.plusMinutes(testDto.getDurationInMinutes());
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(endTime)) {
                return response;
            }

            studentTestAttemptNotFinished.setState(TestAttemptState.FINISHED);
            studentTestAttemptNotFinished.setEndTime(endTime);
            calculateAndSetStudentTestAttemptResults(studentTestAttemptNotFinished);
            studentTestAttemptRepository.save(studentTestAttemptNotFinished);
        }


        // Main logic
        StudentTestAttempt studentTestAttempt = new StudentTestAttempt();
        studentTestAttempt.setStudent(student);
        studentTestAttempt.setModuleId(moduleId);
        studentTestAttempt.setTopicId(topicId);
        studentTestAttempt.setTestId(testId);
        studentTestAttempt.setGroup(group);
        studentTestAttempt.setTotalQuestions(testDto.getQuestions().size());
        studentTestAttempt.setType(TestAttemptType.TOPIC_TEST);
        studentTestAttempt.setState(TestAttemptState.STARTED);
        studentTestAttempt.setIsSuccess((short) 0);
        studentTestAttempt.setAttemptNumber(attempts.size() + 1);
        studentTestAttemptRepository.save(studentTestAttempt);

        return response;
    }

    private void calculateAndSetStudentTestAttemptResults(StudentTestAttempt studentTestAttempt) {
        List<StudentTestAttemptDetail> details = studentTestAttempt.getDetails();
        long count = details.stream().filter(e -> e.getIsCorrect() == 1).count();
        studentTestAttempt.setCorrectAnswers((int) count);

        double percentage = ((double) count / studentTestAttempt.getTotalQuestions()) * 100;
        studentTestAttempt.setCorrectAnswerPercentage((int) Math.round(percentage));
        if (percentage >= 70) {
            studentTestAttempt.setIsSuccess((short) 1);
        } else {
            studentTestAttempt.setIsSuccess((short) 0);
        }
    }
}
