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
import com.malaka.aat.external.dto.group.GroupCreateDto;
import com.malaka.aat.external.dto.group.GroupDto;
import com.malaka.aat.external.enumerators.group.GroupStatus;
import com.malaka.aat.external.model.Group;
import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.repository.GroupRepository;
import com.malaka.aat.external.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


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
            if (!student1.getCourseIds().contains(courseId)) {
                throw new BadRequestException("Student can't be added to the group. Student id: " + student1.getId());
            }
            students.add(student1);
        });

        Group group = new Group();
        group.setCourseId(courseId);
        group.setStudents(students);
        group.setStartDate(dto.getStartDate().atStartOfDay());
        group.setEndDate(dto.getEndDate().atTime(23,  59, 59, 999999999));
        group.setStatus(GroupStatus.CREATED);
        group.setName(dto.getName());
        group.setOrder(getOrderOfGroup(courseId));
        Group save = groupRepository.save(group);
        GroupDto groupDto = convertEntityToDto(save);
        response.setData(groupDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public Integer getOrderOfGroup(String courseId) {
        Optional<Group> groupOptional = groupRepository.findLastCreatedGroupByCourseId(courseId);
        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            return group.getOrder()+1;
        } else {
            return 1;
        }
    }

    public GroupDto convertEntityToDto(Group group) {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(group.getId());
        groupDto.setName(group.getName());
        BaseResponse courseById = malakaInternalClient.getCourseById(group.getCourseId());
        CourseDto courseDto = objectMapper.convertValue(courseById.getData(), CourseDto.class);
        groupDto.setCourseId(group.getCourseId());
        groupDto.setCourseName(courseDto.getName());
        groupDto.setStartDate(group.getStartDate().toLocalDate());
        groupDto.setEndDate(group.getEndDate().toLocalDate());
        groupDto.setStatus(group.getStatus().getValue());
        if (group.getName() != null) {
            groupDto.setName(group.getName());
        } else {
            groupDto.setName(group.getOrder() + "-guruh");
        }
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
}
