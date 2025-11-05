package com.malaka.aat.external.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.dto.user.StudentListDto;
import com.malaka.aat.external.model.Student;
import com.malaka.aat.external.model.spr.StudentTypeSpr;
import com.malaka.aat.external.repository.StudentRepository;
import com.malaka.aat.external.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
        List<StudentListDto> list = students.stream().map(StudentListDto::new).toList();
        response.setData(list);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getStudentsByCourseIdAndType(String courseId, Long type) {
        BaseResponse response = new BaseResponse();
        StudentTypeSpr typeSpr = studentTypeSprService.findById(type);
        List<Student> students = studentRepository.findByType(typeSpr);
        List<StudentListDto> list = students.stream().filter(
                        s -> s.getCourseIds()
                                .contains(courseId)).map(StudentListDto::new)
                .toList();
        response.setData(list);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }
}
