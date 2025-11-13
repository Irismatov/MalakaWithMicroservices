package com.malaka.aat.external.service;


import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.dto.StudentApplicationDto;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.MalakaInternalClient;
import com.malaka.aat.external.dto.student_application.StudentApplicationCorporateCreateDto;
import com.malaka.aat.external.dto.student_application.StudentApplicationIndividualCreateDto;
import com.malaka.aat.external.dto.student_application.StudentApplicationUpdateDto;
import com.malaka.aat.external.enumerators.student_application.StudentApplicationStatus;
import com.malaka.aat.external.enumerators.student_application.StudentApplicationType;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.model.spr.StudentTypeSpr;
import com.malaka.aat.external.repository.RoleRepository;
import com.malaka.aat.external.repository.StudentApplicationRepository;
import com.malaka.aat.external.repository.StudentRepository;
import com.malaka.aat.external.clients.EgovClient;
import com.malaka.aat.external.clients.gcp.EgovGcpResponse;
import com.malaka.aat.external.repository.UserRepository;
import com.malaka.aat.external.repository.spr.StudentTypeSprRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Year;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentApplicationService {

    private final StudentApplicationRepository studentApplicationRepository;
    private final FileService fileService;
    private final MalakaInternalClient malakaInternalClient;
    private final EgovClient egovClient;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentTypeSprRepository studentTypeSprRepository;
    private final StudentApplicationLogService studentApplicationLogService;

    public BaseResponse saveIndividualApplication(StudentApplicationIndividualCreateDto dto) {
        BaseResponse response = new BaseResponse();

        // Validate that the course exists in malaka-internal service
        try {
            log.debug("Validating course existence for courseId: {}", dto.getCourseId());
            BaseResponse courseResponse = malakaInternalClient.getCourseById(dto.getCourseId());

            // Check if the response indicates success (resultCode 0 = SUCCESS)
            if (courseResponse.getResultCode() != ResponseStatus.SUCCESS.getCode()) {
                log.error("Course validation failed for courseId: {}. ResultCode: {}, ResultNote: {}",
                        dto.getCourseId(), courseResponse.getResultCode(), courseResponse.getResultNote());
                throw new NotFoundException("Kurs topilmadi: " + dto.getCourseId());
            }

            log.debug("Course validation successful for courseId: {}", dto.getCourseId());
        } catch (feign.FeignException e) {
            log.error("Error communicating with malaka-internal service: {}", e.getMessage());
            throw new SystemException("Kurs tekshirishda xatolik yuz berdi");
        }

        StudentApplicationIndividual studentApplicationIndividual = StudentApplicationIndividualCreateDto.mapDtoToEntity(dto);

        try {
            File file = fileService.save(dto.getFile());
            studentApplicationIndividual.setFile(file);
        } catch (IOException e) {
            throw new SystemException("Error occurred while saving file");
        }
        String number = generateApplicationNumber();
        studentApplicationIndividual.setNumber(number);
        studentApplicationIndividual.setStatus(StudentApplicationStatus.CREATED);
        StudentApplicationIndividual save = studentApplicationRepository.save(studentApplicationIndividual);
        studentApplicationLogService.save(studentApplicationIndividual, StudentApplicationStatus.CREATED, null);
        StudentApplicationDto studentApplicationDto = convertToDto(save);
        response.setData(studentApplicationDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private String generateApplicationNumber() {
        try {
            Integer count = studentApplicationRepository.countAllApplicationsCreatedThisYear() + 1;
            String year = Year.now().toString();
            char[] charArray = new char[]{'0', '0', '0', '0', '0', '0'};
            int counter = charArray.length - 1;
            while (count > 0) {
                int num = count % 10;
                charArray[counter] = (char) (num + '0');

                counter--;
                count /= 10;
            }
            String number = new String(charArray);
            number = year + number;
            return number;
        } catch (Exception e) {
            throw new SystemException("Error happened while generating application number");
        }
    }


    public BaseResponse saveCorporateApplication(StudentApplicationCorporateCreateDto dto) {
        BaseResponse response = new BaseResponse();

        // Validate that the course exists in malaka-internal service
        try {
            log.debug("Validating course existence for courseId: {}", dto.getCourseId());
            BaseResponse courseResponse = malakaInternalClient.getCourseById(dto.getCourseId());

            // Check if the response indicates success (resultCode 0 = SUCCESS)
            if (courseResponse.getResultCode() != ResponseStatus.SUCCESS.getCode()) {
                log.error("Course validation failed for courseId: {}. ResultCode: {}, ResultNote: {}",
                        dto.getCourseId(), courseResponse.getResultCode(), courseResponse.getResultNote());
                throw new NotFoundException("Kurs topilmadi: " + dto.getCourseId());
            }

            log.debug("Course validation successful for courseId: {}", dto.getCourseId());
        } catch (feign.FeignException e) {
            log.error("Error communicating with malaka-internal service: {}", e.getMessage());
            throw new SystemException("Kurs tekshirishda xatolik yuz berdi");
        }

        StudentApplicationCorporate studentApplicationCorporate = StudentApplicationCorporateCreateDto.mapDtoToEntity(dto);

        try {
            File file = fileService.save(dto.getFile());
            studentApplicationCorporate.setFile(file);
        } catch (IOException e) {
            log.error("Error saving file for corporate application: {}", e.getMessage());
            throw new SystemException("Error occurred while saving file");
        }
        String number = generateApplicationNumber();
        studentApplicationCorporate.setNumber(number);
        studentApplicationCorporate.setStatus(StudentApplicationStatus.CREATED);
        StudentApplicationCorporate save = studentApplicationRepository.save(studentApplicationCorporate);
        studentApplicationLogService.save(studentApplicationCorporate, StudentApplicationStatus.CREATED, null);
        StudentApplicationDto studentApplicationDto = convertToDto(save);
        response.setData(studentApplicationDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }


    public ResponseWithPagination getApplicationsWithPagination(int page, int size) {
        ResponseWithPagination response = new ResponseWithPagination();

        try {
            // Create pageable with sorting by creation date descending
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updtime"));

            // Fetch applications from repository
            Page<StudentApplication> applicationPage = studentApplicationRepository.findAll(pageable);

            // Convert entities to DTOs and create a new Page with DTOs
            Page<StudentApplicationDto> dtoPage = applicationPage.map(this::convertToDto);

            // Use the setData method that handles pagination automatically
            response.setData(dtoPage, page);

            ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
            log.debug("Retrieved {} applications on page {}", dtoPage.getNumberOfElements(), page);

        } catch (Exception e) {
            log.error("Error fetching applications with pagination: {}", e.getMessage(), e);
            ResponseUtil.setResponseStatus(response, ResponseStatus.SYSTEM_ERROR);
        }

        return response;
    }

    public StudentApplication findById(String id) {
        return studentApplicationRepository.
                findById(id).orElseThrow(() -> new NotFoundException("Course not found with id " + id));
    }

    @Transactional
    public ResponseWithPagination updateApplicationStatus(String id, @Valid StudentApplicationUpdateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        StudentApplication application = findById(id);
        StudentApplicationStatus.setStatus(application, dto.getStatus());
        studentApplicationRepository.save(application);
        StudentApplicationStatus studentApplicationStatus = Arrays.stream(StudentApplicationStatus.values()).
                filter(f -> f.getValue() == dto.getStatus())
                .findFirst().orElseThrow(() -> new NotFoundException("Application status not found with value " + dto.getStatus()));
        studentApplicationLogService.save(application, studentApplicationStatus, dto.getDescription());

        if (dto.getStatus() == StudentApplicationStatus.ACCEPTED.getValue()) {
            createStudentsFromStudentApplication(application);
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updtime"));
        Page<StudentApplication> applicationPage = studentApplicationRepository.findAll(pageable);
        // Convert entities to DTOs and create a new Page with DTOs
        Page<StudentApplicationDto> dtoPage = applicationPage.map(this::convertToDto);


        response.setData(dtoPage, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    @Transactional
    public void createStudentsFromStudentApplication(StudentApplication application) {
        StudentTypeSpr studentTypeSpr = studentTypeSprRepository.findByName("EXTERNAL").orElseThrow(() -> new SystemException("Student external type spr not found"));

        if (application instanceof StudentApplicationIndividual individual) {
            User user = getOrCreateUserByPinfl(individual.getPinfl());
            Student student = studentRepository.findByUser(user).orElseGet(Student::new);
            student.setUser(user);
            student.setType(studentTypeSpr);
            if (!student.getCourseIds().contains(individual.getCourseId())) {
                student.getCourseIds().add(individual.getCourseId());
            }
            studentRepository.save(student);
        } else if (application instanceof StudentApplicationCorporate corporate) {
            corporate.getPinfls().forEach(
                    pinfl -> {
                        User user = getOrCreateUserByPinfl(pinfl);
                        Student student = studentRepository.findByUser(user).orElseGet(Student::new);
                        student.setUser(user);
                        student.setType(studentTypeSpr);
                        if (!student.getCourseIds().contains(corporate.getCourseId())) {
                            student.getCourseIds().add(corporate.getCourseId());
                        }
                        studentRepository.save(student);
                    }
            );
        }
    }

    private User getOrCreateUserByPinfl(String pinfl) {
        Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new SystemException("User role not found"));

        EgovGcpResponse info;
        try {
            info = egovClient.getInfo(pinfl);
        } catch (Exception e) {
            throw new SystemException(e.getMessage());
        }

        User user = userRepository.findByPinfl(pinfl).orElseGet(
                () -> {
                    User newUser = new User();
                    newUser.setPinfl(pinfl);
                    newUser.setUsername(pinfl);
                    newUser.getRoles().add(userRole);
                    newUser.setPassword(passwordEncoder.encode("12345678"));
                    newUser.setFirstName(info.getData().get(0).getFirstNameOz());
                    newUser.setLastName(info.getData().get(0).getLastNameOz());
                    newUser.setMiddleName(info.getData().get(0).getMiddleNameOz());
                    newUser = userRepository.save(newUser);
                    return newUser;
                }
        );
        return user;
    }

    private StudentApplicationDto convertToDto(StudentApplication application) {
        StudentApplicationDto dto = new StudentApplicationDto();
        dto.setId(application.getId());
        dto.setCourseId(application.getCourseId());
        dto.setPhone(application.getPhone());
        dto.setStatus(application.getStatus() != null ? application.getStatus().ordinal() : null);
        dto.setFileId(application.getFile() != null ? application.getFile().getId() : null);
        dto.setCreatedDate(application.getInstime());
        dto.setNumber(application.getNumber());

        // Determine type and set specific fields
        if (application instanceof StudentApplicationIndividual) {
            StudentApplicationIndividual individual = (StudentApplicationIndividual) application;
            dto.setApplicationType(StudentApplicationType.INDIVIDUAL.getValue());
            dto.setPinfl(individual.getPinfl());
            dto.setEmail(individual.getEmail());
        } else if (application instanceof StudentApplicationCorporate) {
            StudentApplicationCorporate corporate = (StudentApplicationCorporate) application;
            dto.setApplicationType(StudentApplicationType.CORPORATE.getValue());
            dto.setPinfls(corporate.getPinfls());
            dto.setCorporateName(corporate.getCorporateName());
            dto.setStirNumber(corporate.getStirNumber());
        }

        return dto;
    }

}
