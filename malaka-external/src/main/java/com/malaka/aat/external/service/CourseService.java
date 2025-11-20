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
import com.malaka.aat.external.dto.course.external.CourseDto;
import com.malaka.aat.external.dto.course.external.StudentCourseDetailDto;
import com.malaka.aat.external.dto.course.external.StudentCourseDto;
import com.malaka.aat.external.dto.enrollment.StudentEnrollmentDetailDto;
import com.malaka.aat.external.dto.module.ModuleDto;
import com.malaka.aat.external.dto.topic.TopicDto;
import com.malaka.aat.external.enumerators.course.CourseStateForStudent;
import com.malaka.aat.external.enumerators.group.GroupStatus;
import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentStatus;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.repository.*;
import com.malaka.aat.external.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        StudentEnrollment enrollment = enrollmentOptional.orElseGet(StudentEnrollment::new);
        List<String> moduleIds = studentEnrollmentDetailRepository.findModuleIdsByStudentEnrollment(enrollment);
        List<String> topicIds = studentEnrollmentDetailRepository.findTopicIdsByStudentEnrollment(enrollment);
        ;
        List<String> contentIds = studentEnrollmentDetailRepository.findContentIdsByStudentEnrollment(enrollment);

        List<ModuleDto> modules = courseDto.getModules();
        List<StudentCourseDetailDto.Module> moduleDtos = new ArrayList<>();
        modules.forEach(e -> {
                    StudentCourseDetailDto.Module moduleDto = new StudentCourseDetailDto.Module();
                    moduleDto.setId(e.getId());
                    moduleDto.setName(e.getName());
                    moduleDto.setTopicCount(e.getTopicCount());
                    moduleDto.setOrder(e.getOrder());
                    moduleDto.setTeacherName(e.getTeacherName());

                    List<TopicDto> topics = e.getTopics();
                    topics.forEach(t -> {
                        StudentCourseDetailDto.Topic topic = new StudentCourseDetailDto.Topic();
                        topic.setId(t.getId());
                        topic.setName(t.getName());
                        topic.setOrder(t.getOrder());
                        topic.setContentType(topic.getContentType());
                        List<StudentCourseDetailDto.TopicContent> topicContents = new  ArrayList<>();
                        if (topic.getContentType() == 2 || topic.getContentType() == 3) {
                            topicContents.add(null);
                        } else {
                            StudentCourseDetailDto.TopicMainContent  topicMainContent = new StudentCourseDetailDto.TopicMainContent();
                            topicMainContent.setId(t.getContentFileId());
                            topicMainContent.setUrl("url should be set");
                            // add minutes
                            if (contentIds.contains(t.getContentFileId())) {
                                topicMainContent.setIsFinished(1);
                            }

                            topicContents.add(topicMainContent);
                        }
                        StudentCourseDetailDto.TopicLectureOrPresentationContent topicLecture = new StudentCourseDetailDto.TopicLectureOrPresentationContent();
                        topicLecture.setId(t.getLectureFileId());
                        topicLecture.setUrl("url should be set");
                        if (contentIds.contains(t.getLectureFileId())) {
                            topicLecture.setIsFinished(1);
                        }
                        topicContents.add(topicLecture);
                        StudentCourseDetailDto.TopicLectureOrPresentationContent topicPresentation = new StudentCourseDetailDto.TopicLectureOrPresentationContent();
                        topicPresentation.setId(t.getLectureFileId());
                        topicPresentation.setUrl("url should be set");
                        if (contentIds.contains(t.getPresentationFileId())) {
                            topicLecture.setIsFinished(1);
                        }
                        topicContents.add(topicPresentation);
                        StudentCourseDetailDto.TopicTestContent topicTest = new StudentCourseDetailDto.TopicTestContent();
                        topicTest.setId(t.getTestId());
                        List<StudentTestAttempt> studentAttempts = studentTestAttemptRepository
                                .findByStudentAndGroupAndTestId(student, group, t.getTestId());
                        if (!studentAttempts.isEmpty()) {
                            topicTest.setIsAttempted(1);
                            if (studentAttempts.stream().filter(sa -> sa.getIsSuccess() == 1).findFirst().isPresent()) {
                                topicTest.setIsFinished(1);
                                moduleDto.setIsFinished(1);
                                topic.setIsFinished(1);
                            }
                        }
                        topicContents.add(topicTest);
                        topic.setContents(topicContents);
                    });

                    moduleDtos.add(moduleDto);
                }
        );
        dto.setModules(moduleDtos);
        return dto;
    }


    public BaseResponse getCoursesWithoutPagination() {
        return malakaInternalClient.getCourses();
    }
}
