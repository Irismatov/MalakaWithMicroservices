package com.malaka.aat.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.MalakaInternalClient;
import com.malaka.aat.external.dto.course.external.CourseDto;
import com.malaka.aat.external.dto.enrollment.StudentEnrollmentDetailDto;
import com.malaka.aat.external.dto.module.ModuleDto;
import com.malaka.aat.external.enumerators.student_enrollment.StudentEnrollmentStatus;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.repository.GroupRepository;
import com.malaka.aat.external.repository.StudentEnrollmentDetailRepository;
import com.malaka.aat.external.repository.StudentEnrollmentRepository;
import com.malaka.aat.external.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

            if (lastStudentEnrollmentDetail.getContentStep() == 3) {
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
}
