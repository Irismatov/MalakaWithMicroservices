package com.malaka.aat.external.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.Pagination;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.core.util.ServiceUtil;
import com.malaka.aat.external.dto.user.StudentFioListDto;
import com.malaka.aat.external.dto.user.StudentListDto;
import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.spr.StudentTypeSpr;
import com.malaka.aat.external.repository.StudentRepository;
import com.malaka.aat.external.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentTypeSprService studentTypeSprService;

    public BaseResponse getStudentsByType(Long type) {
        BaseResponse response = new BaseResponse();

        StudentTypeSpr typeSpr = studentTypeSprService.findById(type);

        List<Student> students = studentRepository.findByType(typeSpr);
        List<StudentFioListDto> list = students.stream().map(StudentFioListDto::new).toList();
        response.setData(list);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getStudentsByCourseIdAndType(String courseId, Long type) {
        BaseResponse response = new BaseResponse();
        StudentTypeSpr typeSpr = studentTypeSprService.findById(type);
        List<Student> students = studentRepository.findByType(typeSpr);
        List<StudentFioListDto> list = students.stream().filter(
                        s -> s.getCourseIds()
                                .contains(courseId)).map(StudentFioListDto::new)
                .toList();
        response.setData(list);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination getStudentsWithPagination(Integer page, Integer size, Integer typeId, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updtime"));
        Page<Student> studentsPage = studentRepository.findAllByTypeAndSearch(typeId, search, pageable);
        List<StudentListDto> list = studentsPage.get().map(this::convertEntityToListDto).toList();

        Pagination pagination = new Pagination();
        ServiceUtil.setPaginationValues(studentsPage, pagination);

        ResponseWithPagination response = new ResponseWithPagination();
        response.setData(list);
        response.setPagination(pagination);
        return response;
    }

    private StudentListDto convertEntityToListDto(Student student) {
        StudentListDto studentListDto = new StudentListDto();
        studentListDto.setId(student.getId());
        studentListDto.setEmail(student.getUser().getEmail());
        studentListDto.setPinfl(student.getUser().getPinfl());
        studentListDto.setGender(student.getUser().getGender().getValue());

        StringBuilder fio = new StringBuilder();
        if (student.getUser().getLastName() != null) {
            fio.append(student.getUser().getLastName());
        }
        if (student.getUser().getLastName() != null) {
            fio.append(" ");
            fio.append(student.getUser().getLastName());
        }
        if (student.getUser().getMiddleName() != null) {
            fio.append(" ");
            fio.append(student.getUser().getMiddleName());
        }
        studentListDto.setFio(fio.toString());

        return studentListDto;
    }
}
