package com.malaka.aat.external.service;


import com.malaka.aat.core.dto.*;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.EgovClientException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.MalakaInternalClient;
import com.malaka.aat.external.dto.student_application.StudentApplicationCorporateCreateDto;
import com.malaka.aat.external.dto.student_application.StudentApplicationIndividualCreateDto;
import com.malaka.aat.external.dto.student_application.StudentApplicationUpdateDto;
import com.malaka.aat.external.enumerators.student.Gender;
import com.malaka.aat.external.enumerators.student_application.StudentApplicationStatus;
import com.malaka.aat.external.enumerators.student_application.StudentApplicationType;
import com.malaka.aat.external.model.*;
import com.malaka.aat.external.model.spr.StudentApplicationStatusLog;
import com.malaka.aat.external.model.spr.StudentTypeSpr;
import com.malaka.aat.external.repository.*;
import com.malaka.aat.external.clients.EgovClient;
import com.malaka.aat.external.clients.gcp.EgovGcpResponse;
import com.malaka.aat.external.repository.spr.StudentTypeSprRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentApplicationService {

    @Value("${app.projectUrl}")
    private String projectUrl;

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
    private final SessionService sessionService;
    private final InfoPinppRespository infoPinppRespository;

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
        StudentApplicationDto studentApplicationDto = convertToDto(save, false);
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
        StudentApplicationDto studentApplicationDto = convertToDto(save, false);
        response.setData(studentApplicationDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }


    public ResponseWithPagination getApplicationsWithPagination(int page, int size, Integer status, boolean isInternal) {
        ResponseWithPagination response = new ResponseWithPagination();

        try {
            // Create pageable with sorting by creation date descending
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updtime"));
            User currentUser = sessionService.getCurrentUser();
            boolean isAdmin = currentUser.getRoles().stream().anyMatch(f ->
                    f.getName().equals("ADMIN") || f.getName().equals("SUPER_ADMIN"));

            StudentApplicationStatus applicationStatus = null;
            if (status != null) {
                applicationStatus = Arrays.stream(StudentApplicationStatus.values())
                        .filter(e -> e.getValue() == status)
                        .findFirst().orElseThrow(() -> new NotFoundException("Application status not found: " + status));

            }

            // Fetch applications from repository
            Page<StudentApplication> applicationPage;
            if (isAdmin) {
                applicationPage = studentApplicationRepository.findByInsuserAndStatus(null, applicationStatus, pageable);
            } else {
                applicationPage = studentApplicationRepository.findByInsuserAndStatus(currentUser.getId(), applicationStatus, pageable);
            }

            // Convert entities to DTOs and create a new Page with DTOs
            Page<StudentApplicationDto> dtoPage = applicationPage.map(e -> this.convertToDto(e, isInternal));

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
    public ResponseWithPagination updateApplicationStatus(String id, @Valid StudentApplicationUpdateDto dto, boolean isInternal) {
        ResponseWithPagination response = new ResponseWithPagination();

        StudentApplication application = findById(id);

        StudentApplicationStatus studentApplicationStatus = Arrays.stream(StudentApplicationStatus.values()).
                filter(f -> f.getValue() == dto.getStatus())
                .findFirst().orElseThrow(() -> new NotFoundException("Application status not found with value " + dto.getStatus()));

        if (studentApplicationStatus == StudentApplicationStatus.REJECTED && dto.getDescription() == null) {
            throw new BadRequestException("Description is required");
        }
        StudentApplicationStatus.setStatus(application, dto.getStatus());
        studentApplicationRepository.save(application);


        studentApplicationLogService.save(application, studentApplicationStatus, dto.getDescription());

        if (dto.getStatus() == StudentApplicationStatus.ACCEPTED.getValue()) {
            createStudentsFromStudentApplication(application);
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updtime"));
        Page<StudentApplication> applicationPage = studentApplicationRepository.findAll(pageable);
        // Convert entities to DTOs and create a new Page with DTOs
        Page<StudentApplicationDto> dtoPage = applicationPage.map(e -> this.convertToDto(e, isInternal));


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
            setUserDetailsFromEgov(individual.getPinfl(), user);
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
                        setUserDetailsFromEgov(pinfl, user);
                        if (!student.getCourseIds().contains(corporate.getCourseId())) {
                            student.getCourseIds().add(corporate.getCourseId());
                        }
                        studentRepository.save(student);
                    }
            );
        }
    }

    private void setUserDetailsFromEgov(String pinfl, User user) {
        try {
            EgovGcpResponse info = egovClient.getInfo(pinfl);
            List<EgovGcpResponse.EgovGcpResponseData> data = info.getData();
            EgovGcpResponse.EgovGcpResponseData egovGcpResponseData = data.get(0);
            user.setBirthDate(egovGcpResponseData.getBirthDate());
            user.setNationality(egovGcpResponseData.getNationality());

            fileService.saveBase64FileAsImageForUser(pinfl, egovGcpResponseData.getPhoto());

            switch (egovGcpResponseData.getSex()) {
                case "1" -> {
                    user.setGender(Gender.MALE);
                }
                case "2" -> {
                    user.setGender(Gender.FEMALE);
                }
                default -> {
                    user.setGender(Gender.UNKNOWN);
                }
            }
            String currentDocument = egovGcpResponseData.getCurrentDocument();
            Optional<EgovGcpResponse.Document> documentOptional = egovGcpResponseData.getDocuments().stream().filter(e -> e.getDocument().equals(currentDocument)).findFirst();
            if (documentOptional.isPresent()) {
                EgovGcpResponse.Document document = documentOptional.get();
                Passport passport = new Passport();
                passport.setStatus(document.getStatus());
                passport.setSerNumber(document.getDocument());
                passport.setDocGivenPlace(document.getDocGivePlace());
                passport.setGivenDate(document.getDateBegin());
                passport.setExpiryDate(document.getDateEnd());
                passport.setType(document.getType());
                passport.setIsCurrent((short) 1);
                user.getPassports().forEach(e -> e.setIsDeleted((short) 1));
                user.getPassports().add(passport);
                passport.setUser(user);
            }

        } catch (Exception e) {
            throw new SystemException("Error happened setting student data from egov");
        }
    }

    private User getOrCreateUserByPinfl(String pinfl) {
        Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new SystemException("User role not found"));

        EgovGcpResponse info;
        try {
            info = egovClient.getInfo(pinfl);
        } catch (Exception e) {
            throw new EgovClientException(e.getMessage());
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

    private StudentApplicationDto convertToDto(StudentApplication application, boolean isInternal) {
        StudentApplicationDto dto = new StudentApplicationDto();
        dto.setId(application.getId());
        dto.setCourseId(application.getCourseId());
        dto.setPhone(application.getPhone());
        dto.setStatus(application.getStatus() != null ? application.getStatus().ordinal() : null);
        dto.setFileUrl(getFileUrl(application.getId(), isInternal));
        dto.setCreatedDate(application.getInstime());
        dto.setNumber(application.getNumber());
        BaseResponse responseFromInternal = malakaInternalClient.getCourseNameById(application.getCourseId());
        if (responseFromInternal.getResultCode() != 0) {
            throw new SystemException("Error happened fetching course");
        }
        dto.setCourseName((String) responseFromInternal.getData());
        if (application.getStatus() == StudentApplicationStatus.REJECTED) {
            List<StudentApplicationStatusLog> history = application.getHistory();
            history.sort(Comparator.comparing(StudentApplicationStatusLog::getInstime));
            dto.setRejectionReason(history.get(history.size() - 1).getDescription());
        }

        // Determine type and set specific fields
        if (application instanceof StudentApplicationIndividual individual) {
            dto.setApplicationType(StudentApplicationType.INDIVIDUAL.getValue());
            String pinfl = individual.getPinfl();
            dto.setEmail(individual.getEmail());
            StudentApplicationStudentInfo studentInfo = new StudentApplicationStudentInfo();
            studentInfo.setPinfl(pinfl);
            studentInfo.setFio(getFioFromPinpp(studentInfo.getPinfl()));
            dto.setStudent(studentInfo);
        } else if (application instanceof StudentApplicationCorporate corporate) {
            dto.setApplicationType(StudentApplicationType.CORPORATE.getValue());
            List<StudentApplicationStudentInfo> list = corporate.getPinfls().stream().map(pinfl -> {
                StudentApplicationStudentInfo studentInfo = new StudentApplicationStudentInfo();
                studentInfo.setPinfl(pinfl);
                studentInfo.setFio(getFioFromPinpp(pinfl));
                return studentInfo;
            }).toList();
            dto.setStudents(list);
            dto.setCorporateName(corporate.getCorporateName());
            dto.setStirNumber(corporate.getStirNumber());
        }

        return dto;
    }

    private String getFileUrl(String id, boolean isInternal) {
        StringBuilder fileUrl = new StringBuilder(projectUrl);
        if (isInternal) {
            fileUrl.append("/api/application/").append(id).append("/file");
        } else {
            fileUrl.append("/api/external/application/").append(id).append("/file");
        }
        return fileUrl.toString();
    }

    private String getFioFromPinpp(String pinpp) {
        Optional<InfoPinpp> byPinpp = infoPinppRespository.findByPinpp(pinpp);
        InfoPinpp infoPinpp = byPinpp.orElseGet(() -> {
            InfoPinpp newPinpp = new InfoPinpp();
            newPinpp.setPinpp(pinpp);
            EgovGcpResponse info;
            try {
                info = egovClient.getInfo(pinpp);
            } catch (Exception e) {
                throw new EgovClientException(e.getMessage());
            }
            EgovGcpResponse.EgovGcpResponseData egovGcpResponseData = info.getData().get(0);
            newPinpp.setFirstName(egovGcpResponseData.getFirstNameOz());
            newPinpp.setLastName(egovGcpResponseData.getLastNameOz());
            newPinpp.setMiddleName(egovGcpResponseData.getMiddleNameOz());
            return infoPinppRespository.save(newPinpp);
        });
        String firstName = "";
        String lastName = "";
        String middleName = "";
        if (infoPinpp.getFirstName() != null) {
            firstName = infoPinpp.getFirstName();
        }
        if (infoPinpp.getLastName() != null) {
            lastName = infoPinpp.getLastName();
        }
        if (infoPinpp.getMiddleName() != null) {
            middleName = infoPinpp.getMiddleName();
        }
        return lastName + " " + firstName + " " + middleName;

    }

    public File getApplicationFile(String id) {
        StudentApplication studentApplication = studentApplicationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Student application not found with id: " + id));

        if (studentApplication.getFile() == null) {
            throw new NotFoundException("Student application has no file");
        }
        return studentApplication.getFile();
    }
}
