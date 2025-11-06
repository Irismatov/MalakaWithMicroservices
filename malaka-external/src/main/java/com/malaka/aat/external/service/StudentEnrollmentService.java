package com.malaka.aat.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.MalakaInternalClient;
import com.malaka.aat.external.dto.course.CourseDto;
import com.malaka.aat.external.dto.enrollment.StudentEnrollmentDetailDto;
import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentStatus;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.repository.GroupRepository;
import com.malaka.aat.external.repository.StudentEnrollmentDetailRepository;
import com.malaka.aat.external.repository.StudentEnrollmentRepository;
import com.malaka.aat.external.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

        if (studentEnrollmentOptional.isPresent()) {
            StudentEnrollment studentEnrollment = studentEnrollmentOptional.get();

            // Existing enrollment - increment steps
            StudentEnrollmentDetail lastStudentEnrollmentDetail = studentEnrollmentDetailRepository
                    .findLastByStudentEnrollment(studentEnrollmentOptional.get())
                    .orElseThrow(() -> new SystemException("Student enrollment detail not found"));

            newStudentEnrollmentDetail = new StudentEnrollmentDetail();
            newStudentEnrollmentDetail.setStudentEnrollment(studentEnrollment);

            // Calculate new steps based on the course structure
            int currentModuleStep = lastStudentEnrollmentDetail.getModuleStep();
            int currentTopicStep = lastStudentEnrollmentDetail.getTopicStep();
            int currentContentStep = lastStudentEnrollmentDetail.getContentStep();

            // Get the current module to check topic count
            if (course.getModules() != null && !course.getModules().isEmpty()) {
                // Find current module (moduleStep is 1-based index)
                com.malaka.aat.external.dto.module.ModuleDto currentModule = course.getModules().stream()
                        .filter(m -> m.getOrder().equals(currentModuleStep))
                        .findFirst()
                        .orElseThrow(() -> new SystemException("Module not found for step: " + currentModuleStep));

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
                    if (newTopicStep > currentModule.getTopicCount()) {
                        newTopicStep = 1;
                        newModuleStep = currentModuleStep + 1;

                        // If module step exceeds total modules, we've completed the course
                        if (newModuleStep > course.getModuleCount()) {
                            studentEnrollment.setStatus(StudentEnrollmentStatus.FINISHED);
                            studentEnrollmentRepository.save(studentEnrollment);
                            newModuleStep = course.getModuleCount();
                            newTopicStep = currentModule.getTopicCount();
                            newContentStep = contentsPerTopic;

                        }
                    }
                }

                newStudentEnrollmentDetail.setModuleStep(newModuleStep);
                newStudentEnrollmentDetail.setTopicStep(newTopicStep);
                newStudentEnrollmentDetail.setContentStep(newContentStep);
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
        }

        // Save the new enrollment detail
        StudentEnrollmentDetail savedDetail = studentEnrollmentDetailRepository.save(newStudentEnrollmentDetail);

        // Convert to DTO to avoid Hibernate proxy serialization issues
        StudentEnrollmentDetailDto dto = new StudentEnrollmentDetailDto(savedDetail);

        // Prepare response
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(dto);
        ResponseUtil.setResponseStatus(baseResponse, ResponseStatus.SUCCESS);
        return baseResponse;
    }
}
