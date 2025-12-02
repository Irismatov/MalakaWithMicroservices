package com.malaka.aat.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaka.aat.core.dao.CourseLastGroupOrderProjection;
import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.malaka_internal.MalakaInternalClient;
import com.malaka.aat.external.dto.course.external.CourseDto;
import com.malaka.aat.external.dto.group.GroupCreateDto;
import com.malaka.aat.external.dto.group.GroupDto;
import com.malaka.aat.external.dto.group.GroupUpdateDto;
import com.malaka.aat.external.enumerators.group.GroupStatus;
import com.malaka.aat.external.model.Group;
import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.User;
import com.malaka.aat.external.repository.GroupRepository;
import com.malaka.aat.external.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@RequiredArgsConstructor
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final MalakaInternalClient malakaInternalClient;
    private final StudentRepository studentRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public BaseResponse save(GroupCreateDto dto) {
        BaseResponse response = new BaseResponse();

        validateCourseTime(dto.getStartDate(), dto.getEndDate());

        String courseId = dto.getCourseId();
        try {
            BaseResponse responseFromCourseRequest = malakaInternalClient.getCourseById(courseId);
            if (responseFromCourseRequest.getResultCode() != 0) {
                return responseFromCourseRequest;
            }
        } catch (Exception e) {
            throw new SystemException("Call to internal service failed");
        }

        Set<Student> students = new HashSet<>();
        dto.getStudents().forEach(student -> {
            Student student1 = studentRepository.findById(student).orElseThrow(() -> new NotFoundException("Student not found with id: " + student));
//            if (!student1.getCourseIds().contains(courseId)) {
//                throw new BadRequestException("Student can't be added to the group. Student id: " + student1.getId());
//            }
            students.add(student1);
        });

        Group group = new Group();
        group.setCourseId(courseId);
        group.setStudents(students);
        group.setStartDate(dto.getStartDate().atStartOfDay());
        group.setEndDate(dto.getEndDate().atTime(23,  59, 59, 999999999));
        group.setStatus(GroupStatus.CREATED);
        group.setOrder(getOrderOfNewGroup(courseId));
        Group save = groupRepository.save(group);
        GroupDto groupDto = convertEntityToDto(save);
        response.setData(groupDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public List<CourseLastGroupOrderProjection> getCourseLastGroupOrders(List<String> courseIds) {
        List<CourseLastGroupOrderProjection> courseLastGroupOrders = groupRepository.findCourseLastGroupOrders(courseIds);
        return courseLastGroupOrders;
    }

    public Integer getOrderOfNewGroup(String courseId) {
        Optional<Group> groupOptional = groupRepository.findLastCreatedGroupByCourseId(courseId);
        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            return group.getOrder()+1;
        } else {
            return 1;
        }
    }

    private void validateCourseTime(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new  BadRequestException("End date must not be before start date");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("Start date must be not before now");
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void updateStatusesWhenTimePasses() {
        System.out.print("Updating group statuses at " +  LocalDateTime.now());
        groupRepository.updateToExpired();
        groupRepository.updateCreatedToStarted();
    }

    @Transactional
    public void updateStatusWhenTimePassed(Group group) {
        if (group.getStartDate().isBefore(LocalDateTime.now())) {
            group.setStatus(GroupStatus.STARTED);
            groupRepository.save(group);
        }
        if (group.getEndDate().isBefore(LocalDateTime.now())) {
            group.setStatus(GroupStatus.EXPIRED);
            groupRepository.save(group);
        }
    }


    public GroupDto convertEntityToDto(Group group) {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(group.getId());
        BaseResponse courseById = malakaInternalClient.getCourseById(group.getCourseId());
        CourseDto courseDto = objectMapper.convertValue(courseById.getData(), CourseDto.class);
        groupDto.setCourseId(group.getCourseId());
        groupDto.setCourseName(courseDto.getName());
        groupDto.setStartDate(group.getStartDate().toLocalDate());
        groupDto.setEndDate(group.getEndDate().toLocalDate());
        groupDto.setStatus(group.getStatus().getValue());
        List<GroupDto.Student> studentDtoList = group.getStudents().stream().map(e -> {
            GroupDto.Student studentDto = new GroupDto.Student();
            studentDto.setId(e.getId());
            StringBuilder fio = new StringBuilder();
            User user = e.getUser();
            if (user.getLastName() != null) {
                fio.append(user.getLastName());
            }
            if (user.getFirstName() != null) {
                fio.append(" ").append(user.getFirstName());
            }
            if (user.getMiddleName() != null) {
                fio.append(" ").append(user.getMiddleName());
            }
            studentDto.setFio(fio.toString());
            return studentDto;
        }).toList();
        groupDto.setStudents(studentDtoList);
        groupDto.setName(group.getOrder() + "-guruh");
        return groupDto;
    }

    public ResponseWithPagination getCoursesWithPagination(int page, int size) {
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest =  PageRequest.of(page, size, Sort.by("updtime"));
        Page<Group> all = groupRepository.findAll(pageRequest);
        Page<GroupDto> groupDtos = all.map(this::convertEntityToDto);
        response.setData(groupDtos, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    @Transactional
    public BaseResponse updateGroup(String id, GroupUpdateDto dto) {

        Group group = groupRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Group not found with id: " + id));
        updateStatusWhenTimePassed(group);

        if (group.getStatus() != GroupStatus.CREATED) {
            throw new BadRequestException("Group can't be updated at this state");
        }

        List<String> students = dto.getStudents();
        students.forEach(student -> {
            Optional<Student> studentOptional =
                    group.getStudents().stream().filter(e -> e.getId().equals(student)).findFirst();
            if (studentOptional.isEmpty()) {
                Student student1 = studentRepository.findById(student).orElseThrow(() -> new NotFoundException("Student not found with id: " + student));
                group.getStudents().add(student1);
            }
        });
        group.getStudents().removeIf(student -> !students.contains(student.getId()));
        Group savedGroup = groupRepository.save(group);
        GroupDto groupDto = convertEntityToDto(savedGroup);
        BaseResponse response = new BaseResponse();
        response.setData(groupDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    @Transactional
    public BaseResponse deleteGroup(String id) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new NotFoundException("Group not found with id: " + id));
        updateStatusWhenTimePassed(group);
        if (group.getStatus() != GroupStatus.CREATED) {
            throw new  BadRequestException("Group can't be deleted at this state");
        }
        groupRepository.delete(group);
        BaseResponse  response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }
}
