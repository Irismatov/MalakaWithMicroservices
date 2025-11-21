package com.malaka.aat.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.MalakaInternalClient;
import com.malaka.aat.external.dto.course.LastEnrollmentDetail;
import com.malaka.aat.external.dto.course.external.CourseDto;
import com.malaka.aat.external.dto.course.external.StudentCourseDetailDto;
import com.malaka.aat.external.dto.course.external.StudentCourseDto;
import com.malaka.aat.external.dto.module.ModuleDto;
import com.malaka.aat.external.dto.topic.TopicDto;
import com.malaka.aat.external.enumerators.course.CourseContentType;
import com.malaka.aat.external.enumerators.group.GroupStatus;
import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentDetailType;
import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentStatus;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.repository.*;
import com.malaka.aat.external.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseService {


    private final SessionService sessionService;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final MalakaInternalClient malakaInternalClient;
    private final ObjectMapper objectMapper;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final StudentEnrollmentDetailRepository studentEnrollmentDetailRepository;
    private final StudentTestAttemptRepository studentTestAttemptRepository;

    public ResponseWithPagination getCoursesWithPagination(int page, int size) {
        ResponseWithPagination response = new ResponseWithPagination();


        PageRequest pageRequest = ServiceUtil.preparePageRequest(page, size);


        List<StudentCourseDto> studentCourses = getStudentCourses();
        int start = Math.min((int) pageRequest.getOffset(), studentCourses.size());
        int end = Math.min((start + pageRequest.getPageSize()), studentCourses.size());
        List<StudentCourseDto> pageContent = studentCourses.subList(start, end);
        Page<StudentCourseDto> courseDtos = new PageImpl<>(pageContent, pageRequest, studentCourses.size());
        response.setData(courseDtos, page);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }


    private List<StudentCourseDto> getStudentCourses() {
        User currentUser = sessionService.getCurrentUser();

        Optional<Student> student = studentRepository.findByUser(currentUser);
        if (student.isEmpty()) {
            return new ArrayList<>();
        }
        List<Group> groupsList = groupRepository.findByStudentsContains(student.get());
        List<String> courseIds = groupsList.stream().map(Group::getCourseId).toList();

        if (courseIds.isEmpty()) {
            return new ArrayList<>();
        }

        BaseResponse response = malakaInternalClient.getStudentCourses(courseIds);

        // Handle different response formats
        Object data = response.getData();
        List<CourseDto> courseDtos;

        // If data is directly a List
        @SuppressWarnings("unchecked")
        List<Object> dataList = (List<Object>) data;
        courseDtos = dataList.stream()
                .map(obj -> objectMapper.convertValue(obj, CourseDto.class))
                .toList();

        List<StudentCourseDto> dtos = new ArrayList<>();
        courseDtos.stream().map(this::convertCourseDtoToStudentCourse).forEach(dtos::addAll);
        return dtos;
    }

    private List<StudentCourseDto> convertCourseDtoToStudentCourse(CourseDto courseDto) {
        List<StudentCourseDto> studentCourseDtos = new ArrayList<>();


        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new SystemException("Student not found "));
        List<Group> groups = groupRepository.findByStudentsContainsAndCourseId(student, courseDto.getId());
        groups.forEach(group -> {
            StudentCourseDto studentCourseDto = new StudentCourseDto();
            studentCourseDto.setId(courseDto.getId());
            studentCourseDto.setName(courseDto.getName());
            studentCourseDto.setDescription(courseDto.getDescription());
            studentCourseDto.setImgUrl(courseDto.getImgUrl());
            studentCourseDto.setDescription(courseDto.getDescription());
            studentCourseDto.setModuleCount(courseDto.getModuleCount());
            studentCourseDto.setDescription(courseDto.getDescription());
            studentCourseDto.setGroupId(group.getId());
            studentCourseDto.setGroupName(group.getOrder() + "-guruh");
            studentCourseDto.setStartDate(group.getStartDate().toLocalDate());
            studentCourseDto.setEndDate(group.getEndDate().toLocalDate());
            studentCourseDto.setGroupStatus(group.getStatus().getValue());

            Optional<StudentEnrollment> enrollmentOptional = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, courseDto.getId(), group);
            if (enrollmentOptional.isPresent()) {
                studentCourseDto.setIsStarted(1);
                StudentEnrollment enrollment = enrollmentOptional.get();
                if (enrollment.getStatus() == StudentEnrollmentStatus.FINISHED) {
                    studentCourseDto.setIsFinished(1);
                }
            }

            if (group.getStatus() == GroupStatus.EXPIRED) {
                studentCourseDto.setIsExpired(1);
            }

            studentCourseDtos.add(studentCourseDto);
        });
        return studentCourseDtos;
    }

    public BaseResponse getCourseById(String groupId) {
        BaseResponse response = new BaseResponse();
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser
                (currentUser).orElseThrow(() -> new NotFoundException("Student not found for user with pinfl: " + currentUser.getPinfl()));
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found for user with id: " + groupId));
        if (!group.getStudents().contains(student)) {
            throw new BadRequestException("The user does not belong to this group");
        }

        BaseResponse course = malakaInternalClient.getCourseById(group.getCourseId());
        CourseDto courseDto = objectMapper.convertValue(course.getData(), CourseDto.class);


        StudentCourseDetailDto studentCourseDetailDto = convertCourseDtoToStudentCourseDetail(student, group, courseDto);
        response.setData(studentCourseDetailDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private StudentCourseDetailDto convertCourseDtoToStudentCourseDetail(Student student, Group group, CourseDto courseDto) {
        StudentCourseDetailDto dto = new StudentCourseDetailDto();
        dto.setId(courseDto.getId());
        dto.setName(courseDto.getName());
        dto.setDescription(courseDto.getDescription());
        dto.setImgUrl(courseDto.getImgUrl());
        dto.setDescription(courseDto.getDescription());
        dto.setModuleCount(courseDto.getModuleCount());
        dto.setDescription(courseDto.getDescription());
        dto.setGroupId(group.getId());
        dto.setGroupName(group.getOrder() + "-guruh");
        dto.setStartDate(group.getStartDate().toLocalDate());
        dto.setEndDate(group.getEndDate().toLocalDate());
        dto.setGroupStatus(group.getStatus().getValue());



        Optional<StudentEnrollment> enrollmentOptional = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, courseDto.getId(), group);
        if (enrollmentOptional.isPresent()) {
            dto.setIsStarted(1);
            StudentEnrollment enrollment = enrollmentOptional.get();
            if (enrollment.getStatus() == StudentEnrollmentStatus.FINISHED) {
                dto.setIsFinished(1);
            }
        }

        if (group.getStatus() == GroupStatus.EXPIRED) {
            dto.setIsExpired(1);
        }

        List<String> moduleFinishIds;
        List<String> topicFinishIds;
        List<String> contentFinishIds;
        List<String> moduleStartIds;
        List<String> topicStartIds;
        List<String> contentStartIds;
        if (enrollmentOptional.isPresent()) {
            StudentEnrollment enrollment = enrollmentOptional.get();
            moduleFinishIds = studentEnrollmentDetailRepository.findModuleIdsByStudentEnrollment(enrollment, StudentEnrollmentDetailType.FINISH);
            topicFinishIds = studentEnrollmentDetailRepository.findTopicIdsByStudentEnrollment(enrollment, StudentEnrollmentDetailType.FINISH);
            contentFinishIds = studentEnrollmentDetailRepository.findContentIdsByStudentEnrollmentAndType(enrollment, StudentEnrollmentDetailType.FINISH);

            moduleStartIds = studentEnrollmentDetailRepository.findModuleIdsByStudentEnrollment(enrollment, StudentEnrollmentDetailType.START);
            topicStartIds = studentEnrollmentDetailRepository.findTopicIdsByStudentEnrollment(enrollment, StudentEnrollmentDetailType.START);
            contentStartIds = studentEnrollmentDetailRepository.findContentIdsByStudentEnrollmentAndType(enrollment, StudentEnrollmentDetailType.START);
        } else {
            moduleFinishIds = new ArrayList<>();
            topicFinishIds = new ArrayList<>();
            contentFinishIds = new ArrayList<>();
            moduleStartIds = new ArrayList<>();
            topicStartIds = new ArrayList<>();
            contentStartIds = new ArrayList<>();
        }

        List<ModuleDto> modules = courseDto.getModules();
        List<StudentCourseDetailDto.Module> moduleDtos = new ArrayList<>();
        modules.forEach(e -> {
                    StudentCourseDetailDto.Module moduleDto = new StudentCourseDetailDto.Module();
                    moduleDto.setId(e.getId());
                    moduleDto.setName(e.getName());
                    moduleDto.setTopicCount(e.getTopicCount());
                    moduleDto.setOrder(e.getOrder());
                    moduleDto.setTeacherName(e.getTeacherName());
                    if (moduleStartIds.contains(e.getId())) {
                        moduleDto.setIsStarted(1);
                    }

                    List<TopicDto> topics = e.getTopics();
                    List<StudentCourseDetailDto.Topic>  topicDtos = new ArrayList<>();
                    topics.forEach(t -> {
                        StudentCourseDetailDto.Topic topicDto = new StudentCourseDetailDto.Topic();
                        topicDto.setId(t.getId());
                        topicDto.setName(t.getName());
                        topicDto.setOrder(t.getOrder());
                        List<StudentCourseDetailDto.TopicContent> topicContents = new  ArrayList<>();
                        StudentCourseDetailDto.TopicMainContent topicMainContent;

                        if (moduleDto.getIsStarted() == 1 && topicStartIds.contains(t.getId())) {
                            topicDto.setIsStarted(1);
                        }

                        if (t.getContentType() == 2 || t.getContentType() == 3) {
                            topicMainContent = new StudentCourseDetailDto.TopicMainContent();
                        } else {
                            StudentCourseDetailDto.TopicMainContentVideAudio topicMainContentAudioVideo = new StudentCourseDetailDto.TopicMainContentVideAudio();
                            topicMainContentAudioVideo.setId(t.getContentFileId());
                            topicMainContentAudioVideo.setUrl("url should be set");
                            topicMainContentAudioVideo.setDuration(9999);
                            topicMainContent = topicMainContentAudioVideo;
                        }
                        if (contentFinishIds.contains(t.getContentFileId())) {
                            topicMainContent.setIsFinished(1);
                        }
                        if (topicDto.getIsStarted() == 1 && contentStartIds.contains(t.getContentFileId())) {
                            topicMainContent.setIsStarted(1);
                        }
                        topicMainContent.setContentType(t.getContentType());
                        topicMainContent.setType(CourseContentType.MAIN_CONTENT.getValue());
                        topicContents.add(topicMainContent);
                        StudentCourseDetailDto.TopicLectureOrPresentationContent topicLecture = new StudentCourseDetailDto.TopicLectureOrPresentationContent();
                        topicLecture.setId(t.getLectureFileId());
                        topicLecture.setUrl("url should be set");
                        if (contentFinishIds.contains(t.getLectureFileId())) {
                            topicLecture.setIsFinished(1);
                        }
                        if (topicDto.getIsStarted() == 1 && contentStartIds.contains(t.getLectureFileId())) {
                            topicLecture.setIsStarted(1);
                        }
                        topicLecture.setType(CourseContentType.LECTURE.getValue());
                        topicContents.add(topicLecture);
                        StudentCourseDetailDto.TopicLectureOrPresentationContent topicPresentation = new StudentCourseDetailDto.TopicLectureOrPresentationContent();
                        topicPresentation.setId(t.getLectureFileId());
                        topicPresentation.setUrl("url should be set");
                        if (contentFinishIds.contains(t.getPresentationFileId())) {
                            topicLecture.setIsFinished(1);
                        }
                        if (topicDto.getIsStarted() == 1 && contentStartIds.contains(t.getPresentationFileId())) {
                            topicPresentation.setIsStarted(1);
                        }
                        topicPresentation.setType(CourseContentType.PRESENTATION.getValue());
                        topicContents.add(topicPresentation);
                        StudentCourseDetailDto.TopicTestContent topicTest = new StudentCourseDetailDto.TopicTestContent();
                        topicTest.setId(t.getTestId());
                        topicTest.setTotalAttempts(t.getAttemptLimit());
                        topicTest.setQuestionCount(t.getQuestionCount());
                        List<StudentTestAttempt> studentAttempts = studentTestAttemptRepository
                                .findByStudentAndGroupAndTestId(student, group, t.getTestId());
                        if (topicDto.getIsStarted() == 1 && !studentAttempts.isEmpty()) {
                            topicTest.setIsAttempted(1);
                            topicTest.setIsStarted(1);
                            if (studentAttempts.stream().filter(sa -> sa.getIsSuccess() == 1).findFirst().isPresent()) {
                                topicTest.setIsFinished(1);
                                moduleDto.setIsFinished(1);
                                topicDto.setIsFinished(1);
                            }
                        }
                        topicTest.setDurationInMinutes(t.getDurationInMinutes());
                        topicTest.setType(CourseContentType.TEST.getValue());
                        topicContents.add(topicTest);
                        topicDto.setContents(topicContents);
                        topicDtos.add(topicDto);
                    });
                    moduleDto.setTopics(topicDtos);
                    moduleDtos.add(moduleDto);
                }
        );
        dto.setModules(moduleDtos);
        return dto;
    }


    public BaseResponse getCoursesWithoutPagination() {
        return malakaInternalClient.getCourses();
    }

    public ResponseEntity<?> getCourseModuleTopicContent(String groupId, String moduleId, String topicId, String contentId) {

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id " + groupId));
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new NotFoundException("Current user is not a student"));
        if (group.getStatus() != GroupStatus.STARTED) {
            throw new BadRequestException("Group state is not started state");
        }
        if (!group.getStudents().contains(student)) {
            throw new BadRequestException("Student does not belong to this group");
        }
        StudentEnrollment enrollment = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, group.getCourseId(), group).orElseThrow(() -> new BadRequestException("Corse has not been started yet"));
        List<String> moduleIdsStart = studentEnrollmentDetailRepository.findModuleIdsByStudentEnrollment(enrollment, StudentEnrollmentDetailType.START);
        if (!moduleIdsStart.contains(moduleId)) {
            throw new  BadRequestException("Student is not eligible to get the content");
        }
        List<String> topicIdsStart = studentEnrollmentDetailRepository.findTopicIdsByStudentEnrollment(enrollment, StudentEnrollmentDetailType.START);
        if (!topicIdsStart.contains(topicId)) {
            throw new  BadRequestException("Student is not eligible to get the content");
        }
        List<String> contentIds = studentEnrollmentDetailRepository.findContentIdsByStudentEnrollmentAndType(enrollment, StudentEnrollmentDetailType.START);
        if (!contentIds.contains(contentId)) {
            throw new  BadRequestException("Student is not eligible to get the content");
        }

        return malakaInternalClient.getCourseModuleTopicContent(
                group.getCourseId(),  moduleId, topicId, contentId
        );
    }

    public BaseResponse getLastEnrollmentDetail(String groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found with id " + groupId));
        User currentUser = sessionService.getCurrentUser();
        Student student = studentRepository.findByUser(currentUser).orElseThrow(() -> new NotFoundException("Current user is not a student"));
        if (!group.getStudents().contains(student)) {
            throw new BadRequestException("Student does not belong to this group");
        }
        if (group.getStatus() != GroupStatus.STARTED) {
            throw new BadRequestException("Group state is not started state");
        }
        StudentEnrollment enrollment = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, group.getCourseId(), group).orElseThrow(() -> new BadRequestException("Corse has not been started yet"));
        StudentEnrollmentDetail studentEnrollmentDetail = studentEnrollmentDetailRepository.findLastByStudentEnrollmentAndType(enrollment, StudentEnrollmentDetailType.START).orElseThrow(() -> new BadRequestException("Student has not started any task yet"));
        LastEnrollmentDetail detail = new LastEnrollmentDetail();
        detail.setTopicId(studentEnrollmentDetail.getTopicId());
        detail.setModuleId(studentEnrollmentDetail.getModuleId());
        detail.setContentId(studentEnrollmentDetail.getContentId());
        detail.setContentType(studentEnrollmentDetail.getContentType().getValue());

        BaseResponse response = new BaseResponse();
        response.setData(detail);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }
}
