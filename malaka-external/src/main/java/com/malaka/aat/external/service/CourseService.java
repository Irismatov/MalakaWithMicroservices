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
import com.malaka.aat.external.dto.course.external.StudentCourseDto;
import com.malaka.aat.external.dto.enrollment.StudentEnrollmentDetailDto;
import com.malaka.aat.external.enumerators.course.CourseStateForStudent;
import com.malaka.aat.external.enumerators.group.GroupStatus;
import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentStatus;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.repository.GroupRepository;
import com.malaka.aat.external.repository.StudentEnrollmentDetailRepository;
import com.malaka.aat.external.repository.StudentEnrollmentRepository;
import com.malaka.aat.external.repository.StudentRepository;
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
        List<Group> groups = groupRepository.findByStudentsContains(student);
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
        StudentEnrollment studentEnrollment = studentEnrollmentRepository.findByStudentAndCourseIdAndGroup(student, courseDto.getId(), group)
                .orElseThrow(() -> new NotFoundException("Enrollment not found for the user"));
        StudentEnrollmentDetail studentEnrollmentDetail = studentEnrollmentDetailRepository.findLastByStudentEnrollment(studentEnrollment).orElseThrow(() -> new SystemException("Student enrollment detail not found"));
        courseDto.setStudentEnrollment(new StudentEnrollmentDetailDto(studentEnrollmentDetail));
        response.setData(courseDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getCoursesWithoutPagination() {
        return malakaInternalClient.getCourses();
    }
}
