package com.malaka.aat.internal.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.Pagination;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.*;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.internal.enumerators.topic.TopicContentType;
import com.malaka.aat.internal.repository.TopicRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.malaka.aat.internal.dto.course.*;
import com.malaka.aat.internal.dto.module.ModuleDto;
import com.malaka.aat.internal.enumerators.course.CourseState;
import com.malaka.aat.internal.enumerators.model.ModuleState;
import com.malaka.aat.internal.model.*;
import com.malaka.aat.internal.model.Module;
import com.malaka.aat.internal.model.spr.*;
import com.malaka.aat.internal.repository.CourseRepository;
import com.malaka.aat.internal.repository.ModuleRepository;
import com.malaka.aat.internal.repository.UserRepository;
import com.malaka.aat.internal.repository.spr.DepartmentSprRepository;
import com.malaka.aat.internal.repository.spr.FacultySprRepository;
import com.malaka.aat.internal.service.spr.LangSprService;
import com.malaka.aat.internal.util.FileValidationUtil;
import com.malaka.aat.internal.util.ServiceUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {


    private final CourseRepository courseRepository;
    private final FileService fileService;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final FacultySprRepository facultySprRepository;
    private final DepartmentSprRepository departmentSprRepository;
    private final StateHMetService stateHMetService;
    private final SessionService sessionService;
    private final LangSprService langSprService;
    private final com.malaka.aat.internal.service.spr.CourseFormatSprService courseFormatSprService;
    private final com.malaka.aat.internal.service.spr.CourseTypeSprService courseTypeSprService;
    private final com.malaka.aat.internal.service.spr.CourseStudentTypeSprService courseStudentTypeSprService;

    @Lazy
    @Autowired
    private CourseService self;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private StateHModuleService stateHModuleService;

    @Transactional
    public BaseResponse save(CourseCreateDto dto) {
        try {
            BaseResponse response = new BaseResponse();

            if (isNameExists(dto.getName())) {
                throw new AlreadyExistsException("Course already exists with name " + dto.getName());
            }

            // Validate that the file is an image
            FileValidationUtil.validateImageFile(dto.getFile());

            Course course = CourseCreateDto.mapDtoToEntity(dto);

            // Set course type from ID
            CourseTypeSpr courseType = courseTypeSprService.findById(dto.getCourseType());
            course.setCourseType(courseType);

            // Set course format from ID
            CourseFormatSpr courseFormat = courseFormatSprService.findById(dto.getCourseFormat());
            course.setCourseFormat(courseFormat);

            // Set course student type from ID
            CourseStudentTypeSpr courseStudentType = courseStudentTypeSprService.findById(dto.getCourseStudentType());
            course.setCourseStudentType(courseStudentType);

            LangSpr lang = langSprService.findById(dto.getLang());
            course.setLang(lang);

            CourseState.setState(course, CourseState.CREATED);
            File file = fileService.save(dto.getFile());
            course.setFile(file);
            Course savedCourse = courseRepository.save(course);
            savedCourse.setFile(file);
            stateHMetService.saveStateForCourse(course, CourseState.findByValue(course.getState()), null);


            CourseDto courseDto = new CourseDto(savedCourse);
            response.setData(courseDto);
            ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
            return response;
        } catch (IOException e) {
            throw new SystemException();
        }
    }

    private boolean isNameExists(String name) {
        Optional<Course> byName = courseRepository.findByName(name);
        return byName.isPresent();
    }


    public Resource getImage(String id) {
        try {
            Course course = findById(id);
            File file = course.getFile();
            Path path = Paths.get(file.getPath());
            Resource urlResource = new UrlResource(path.toUri());
            if (!urlResource.exists()) {
                throw new NotFoundException("Resource not found" + urlResource.getURL());
            }
            return urlResource;
        } catch (IOException exception) {
            throw new SystemException();
        }
    }

    public Course findById(String id) {
        return courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found with id: " + id));
    }

    @PreAuthorize("hasAnyRole('FACULTY_HEAD', 'ADMIN', 'SUPER_ADMIN')")
    public Course getCourseToFacultyHead(String id) {
        return courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found with id: " + id));
    }

    @PostAuthorize("hasAnyRole('METHODIST', 'ADMIN', 'SUPER_ADMIN') or (returnObject.insuser == authentication.name)")
    public Course getCourseToMethodist(String id) {

        return courseRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Course not found with id: " + id)
                );
    }

    public BaseResponse update(String id, CourseUpdateDto dto) {
        try {
            BaseResponse response = new BaseResponse();
            Course course = self.findById(id);

            // Validate image file if provided
            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                FileValidationUtil.validateImageFile(dto.getFile());
                File file = fileService.save(dto.getFile());
                course.setFile(file);
            }

            CourseUpdateDto.setFieldsToEntity(course, dto);

            // Update course type if provided
            if (dto.getCourseType() != null) {
                CourseTypeSpr courseType = courseTypeSprService.findById(dto.getCourseType());
                course.setCourseType(courseType);
            }

            // Update course format if provided
            if (dto.getCourseFormat() != null) {
                CourseFormatSpr courseFormat = courseFormatSprService.findById(dto.getCourseFormat());
                course.setCourseFormat(courseFormat);
            }

            // Update course student type if provided
            if (dto.getCourseStudentType() != null) {
                CourseStudentTypeSpr courseStudentType = courseStudentTypeSprService.findById(dto.getCourseStudentType());
                course.setCourseStudentType(courseStudentType);
            }

            if (dto.getLang() != null) {
                LangSpr lang = langSprService.findById(dto.getLang());
                course.setLang(lang);
            }

            course = courseRepository.save(course);
            CourseDto courseDto = new CourseDto(course);
            ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
            response.setData(courseDto);
            return response;
        } catch (IOException e) {
            throw new SystemException();
        }
    }

    public BaseResponse updateCourseState(String id, CourseStateUpdateDto dto) {
        BaseResponse response = new BaseResponse();
        Course course;

        switch (dto.getState()) {
            case "002" -> {
                // SEND TO TEACHER
                course = self.getCourseToMethodist(id);

                List<Module> modules = course.getModules();
                if (modules.size() != course.getModuleCount()) {
                    throw new BadRequestException("Cannot set '002' state to the course All modules must be created");
                }

                CourseState.setState(course, dto.getState());
                course = courseRepository.save(course);
            }
            case "003" -> {
                // SEND TO FACULTY HEAD
                course = self.getCourseToMethodist(id);

                // Validate that ALL modules are in SENT state (002) before sending to faculty head
                boolean allModulesSent = course.getModules().stream()
                        .allMatch(module -> "002".equals(module.getModuleState()));

                if (!allModulesSent) {
                    throw new BadRequestException(
                            "Cannot send course to faculty head. All modules must be in SENT state (002)"
                    );
                }

                // Update course state
                CourseState.setState(course, dto.getState());

                // Set all modules to APPROVED state (003) when course is sent to faculty head
                for (Module module : course.getModules()) {
                    ModuleState.setState(module, ModuleState.APPROVED);
                }

                // Save modules with updated states
                moduleRepository.saveAll(course.getModules());
                course = courseRepository.save(course);
            }
            case "007" -> {
                course = self.getCourseToMethodist(id);
                CourseState.setState(course, dto.getState());
                course = courseRepository.save(course);
            }
            default -> {
                if (dto.getState().equals("004") && dto.getDescription() == null) {
                    throw new BadRequestException("Description is required");
                }
                course = self.getCourseToFacultyHead(id);
                CourseState.setState(course, dto.getState());
                course = courseRepository.save(course);
            }
        }

        stateHMetService.saveStateForCourse(course, CourseState.findByValue(course.getState()), dto.getDescription());
        response.setData(new CourseDto(course));
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    @Transactional
    public BaseResponse addSingleModuleToCourse(String courseId, CourseAddSingleModuleDto dto) {
        BaseResponse response = new BaseResponse();

        // Find the course
        Course course = findById(courseId);

        // Check if order is higher than module count
        if (dto.getOrder() > course.getModuleCount()) {
            throw new BadRequestException(
                    "Order cannot exceed course module count. Maximum allowed order: " + course.getModuleCount()
            );
        }

        if (course.getModules().stream().anyMatch(m -> m.getName().equalsIgnoreCase(dto.getName()))) {
            throw new AlreadyExistsException(
                    "Module with name: " + dto.getName() + " already exists in the course"
            );
        }

        // Check if the order is already taken by another module in this course
        Optional<Module> existingModuleWithOrder = moduleRepository.findByCourseIdAndOrder(courseId, dto.getOrder());
        if (existingModuleWithOrder.isPresent()) {
            throw new BadRequestException(
                    "Module with order " + dto.getOrder() + " already exists in this course"
            );
        }

        // Create module
        Module module = new Module();
        module.setName(dto.getName());
        module.setTopicCount(dto.getTopicCount());
        module.setOrder(dto.getOrder());
        module.setCourse(course);
        module.setModuleState(ModuleState.NEW.getValue());

        // Validate and set teacher
        User teacher = userRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + dto.getTeacherId()));

        // Validate teacher has TEACHER role
        boolean hasTeacherRole = teacher.getRoles().stream()
                .anyMatch(role -> "TEACHER".equals(role.getName()));

        if (!hasTeacherRole) {
            throw new BadRequestException("User with id " + dto.getTeacherId() + " does not have TEACHER role");
        }

        module.setTeacher(teacher);

        // Set faculty if provided
        if (dto.getFacultyId() != null && !dto.getFacultyId().isBlank()) {
            FacultySpr faculty = facultySprRepository.findById(dto.getFacultyId())
                    .orElseThrow(() -> new NotFoundException("Faculty not found with id: " + dto.getFacultyId()));
            module.setFaculty(faculty);
        }

        // Set department if provided
        if (dto.getDepartmentId() != null && !dto.getDepartmentId().isBlank()) {
            DepartmentSpr department = departmentSprRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found with id: " + dto.getDepartmentId()));
            module.setDepartment(department);
        }


        Module savedModule = moduleRepository.save(module);

        StateHModule stateHModule = stateHModuleService.createStateHModule(savedModule, ModuleState.NEW, null);
        savedModule.getStateHistory().add(stateHModule);
        // Get course with all modules and return CourseDto
        Course courseWithModules = savedModule.getCourse();
        courseWithModules.getModules().add(savedModule);


        CourseDto courseDto = new CourseDto(courseWithModules);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        response.setData(courseDto);
        return response;
    }

    public ResponseWithPagination getCourses(int page, int size, CourseFilterDto filterDto) {
        ResponseWithPagination response = new ResponseWithPagination();

        User currentUser = sessionService.getCurrentUser();

        // Check user roles
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || "SUPER_ADMIN".equals(role.getName()));
        boolean isMethodist = currentUser.getRoles().stream()
                .anyMatch(role -> "METHODIST".equals(role.getName()));
        boolean isTeacher = currentUser.getRoles().stream()
                .anyMatch(role -> "TEACHER".equals(role.getName()));
        boolean isFacultyHead = currentUser.getRoles().stream()
                .anyMatch(role -> "FACULTY_HEAD".equals(role.getName()));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updtime"));
        Page<Course> coursePage;
        if (isAdmin) {
            coursePage = courseRepository.getCoursesFilteredForAdmin(
                    filterDto.getName(),
                    filterDto.getCourseFormat(),
                    filterDto.getCourseType(),
                    filterDto.getCourseStudentType(),
                    filterDto.getState(),
                    pageRequest
            );
        } else if (isMethodist) {
            coursePage = courseRepository.getCoursesFilteredForAdmin(
                    filterDto.getName(),
                    filterDto.getCourseFormat(),
                    filterDto.getCourseType(),
                    filterDto.getCourseStudentType(),
                    filterDto.getState(),
                    pageRequest);
        } else if (isTeacher) {
            coursePage = courseRepository.getCoursesFilteredForTeacher(
                    currentUser.getId(),
                    filterDto.getName(),
                    filterDto.getCourseFormat(),
                    filterDto.getCourseType(),
                    filterDto.getCourseStudentType(),
                    filterDto.getState(),
                    pageRequest
            );
        } else if (isFacultyHead) {
            coursePage = courseRepository.getCoursesFilteredForFacultyHead(
                    filterDto.getName(),
                    filterDto.getCourseFormat(),
                    filterDto.getCourseType(),
                    filterDto.getCourseStudentType(),
                    filterDto.getState(),
                    pageRequest);
        } else {
            throw new AuthException("You are not authorized to perform this action");
        }


        List<CourseListDto> listDto = coursePage.get().map(CourseListDto::new).toList();
        response.setData(listDto);
        Pagination pagination = new Pagination();
        pagination.setCurrentPage(page);
        pagination.setTotalElements(coursePage.getTotalElements());
        pagination.setTotalPages(coursePage.getTotalPages());
        pagination.setNumberOfElements(coursePage.getNumberOfElements());
        response.setPagination(pagination);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }


    public BaseResponse getCourseById(String courseId) {
        BaseResponse response = new BaseResponse();

        // Find the course with all its relationships
        Course course = findById(courseId);

        User currentUser = sessionService.getCurrentUser();
        boolean isTeacher = currentUser.getRoles().stream()
                .anyMatch(role -> "TEACHER".equals(role.getName()));

        if (isTeacher) {
            course.setModules(
                    course.getModules().stream().filter(m -> m.getTeacher().getId().equals(currentUser.getId())).collect(Collectors.toList())
            );
        }

        // Convert to DTO (includes all modules and details)
        CourseDto courseDto = new CourseDto(course);

        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        response.setData(courseDto);
        return response;
    }


    @Transactional
    public BaseResponse delete(String id) {
        BaseResponse response = new BaseResponse();

        // Find the course
        Course course = findById(id);

        // Check if status is "001" (CREATED)
        if (!"001".equals(course.getState()) && !"005".equals(course.getState())) {
            throw new BadRequestException(
                    "Course can only be deleted when status is '001' (CREATED). Current status: " + course.getState()
            );
        }

        courseRepository.delete(course);
        response = getCourses(0, 10, new CourseFilterDto());
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getCoursesWithoutPagination() {
        BaseResponse response = new BaseResponse();
        List<Course> all = courseRepository.findAllNotCancelledCourses();
        List<CourseExternalListDto> list = all.stream().map(CourseExternalListDto::new).toList();
        response.setData(list);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getCoursesForStudents(List<String> ids) {
        BaseResponse response = new BaseResponse();
        List<Course> courses = courseRepository.findByIds(ids);
        List<CourseDto> list = courses.stream().map(CourseDto::new).toList();
        response.setData(list);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getCourseByTopicId(String topicId) {
        try {
            BaseResponse response = new BaseResponse();
            Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new NotFoundException("Topic not found with id: " + topicId));
            Course course = topic.getModule().getCourse();
            CourseDto courseDto = new CourseDto(course);
            response.setData(courseDto);
            ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
            return response;
        } catch (Exception e) {
            throw new NotFoundException("Course not found for topic id " + topicId);
        }
    }

    public BaseResponse getCourseNameById(String id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found with id: " + id));
        BaseResponse response = new BaseResponse();
        response.setData(course.getName());
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseEntity<?> getCourseContent(String courseId, String moduleId, String topicId, String contentId) {
        // validations
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));
        List<Module> modules = course.getModules();
        Module module = modules.stream().filter(m -> m.getId().equals(moduleId)).findFirst().orElseThrow(() -> new NotFoundException("Module not found with id: " + moduleId));
        List<Topic> topics = module.getTopics();
        Topic topic = topics.stream().filter(t -> t.getId().equals(topicId)).findFirst().orElseThrow(() -> new NotFoundException("Topic not found with id: " + topicId));
        Path path;
        String filename;
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (topic.getContentFile() != null && topic.getContentFile().getId().equals(contentId)) {
            File contentFile = topic.getContentFile();
            path = Paths.get(contentFile.getPath());
            filename = contentFile.getOriginalName();
            if (contentFile.getContentType() != null && !contentFile.getContentType().isEmpty()) {
                mediaType = MediaType.parseMediaType(contentFile.getContentType());
            } else if (topic.getContentType() == TopicContentType.VIDEO) {
                mediaType = MediaType.parseMediaType("video/*");
                path = Paths.get(contentFile.getPath());
            } else if (topic.getContentType() == TopicContentType.AUDIO) {
                mediaType = MediaType.parseMediaType("audio/*");
            }

        } else if (topic.getLectureFile().getId().equals(contentId)) {
            mediaType = MediaType.APPLICATION_PDF;
            path = Paths.get(topic.getLectureFile().getPath());
            filename =  topic.getLectureFile().getOriginalName();
        } else if (topic.getPresentationFile().getId().equals(contentId)) {
            mediaType = MediaType.APPLICATION_PDF;
            path = Paths.get(topic.getPresentationFile().getPath());
            filename =  topic.getPresentationFile().getOriginalName();
        } else {
            throw new NotFoundException("Content not found with id " + contentId);
        }

        Resource resource = new FileSystemResource(path);


        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
