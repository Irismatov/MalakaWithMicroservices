package com.malaka.aat.internal.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.AlreadyExistsException;
import com.malaka.aat.core.exception.custom.AuthException;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.internal.enumerators.course.CourseState;
import com.malaka.aat.internal.model.spr.StateHModule;
import com.malaka.aat.internal.repository.CourseRepository;
import com.malaka.aat.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.malaka.aat.internal.dto.course.CourseDto;
import com.malaka.aat.internal.dto.module.ModuleDto;
import com.malaka.aat.internal.dto.module.ModuleStateUpdateDto;
import com.malaka.aat.internal.dto.module.ModuleUpdateDto;
import com.malaka.aat.internal.dto.topic.TopicCreateDto;
import com.malaka.aat.internal.dto.topic.TopicDto;
import com.malaka.aat.internal.enumerators.model.ModuleState;
import com.malaka.aat.internal.model.*;
import com.malaka.aat.internal.model.Module;
import com.malaka.aat.internal.model.spr.DepartmentSpr;
import com.malaka.aat.internal.model.spr.FacultySpr;
import com.malaka.aat.internal.repository.ModuleRepository;
import com.malaka.aat.internal.repository.TopicRepository;
import com.malaka.aat.internal.repository.spr.DepartmentSprRepository;
import com.malaka.aat.internal.repository.spr.FacultySprRepository;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseService courseService;
    private final FacultySprRepository facultySprRepository;
    private final DepartmentSprRepository departmentSprRepository;
    private final TopicRepository topicRepository;
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final StateHModuleService stateHModuleService;
    private final CourseRepository courseRepository;
    private final StateHMetService stateHMetService;

    public BaseResponse update(String moduleId, ModuleUpdateDto dto) {
        BaseResponse response = new BaseResponse();

        // Find the module
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found with id: " + moduleId));

        if (!module.getCourse().getState().equals("001") && !module.getCourse().getState().equals("004")) {
            throw new BadRequestException("Can't update module state if the course is not in NEW or REJECTED state");
        }

        // Update basic fields
        if (!module.getName().equals(dto.getName())) {
            Optional<Module> existingModule = moduleRepository
                    .findByNameAndCourseId(dto.getName(), module.getCourse().getId());
            if (existingModule.isPresent()) {
                throw new AlreadyExistsException("Module with name '" + dto.getName() + "' already exists in this course");
            }
            module.setName(dto.getName());
        }

        if (!module.getTopicCount().equals(dto.getTopicCount())) {
            module.setTopicCount(dto.getTopicCount());
        }

        if (!module.getTeacher().getId().equals(dto.getTeacherId())) {
            // Validate and update teacher
            User teacher = userRepository.findById(dto.getTeacherId()).orElseThrow(() ->
                    new NotFoundException("Taeacher not found with id " + dto.getTeacherId()));
            boolean hasTeacherRole = teacher.getRoles().stream()
                    .anyMatch(role -> "TEACHER".equalsIgnoreCase(role.getName()));

            if (!hasTeacherRole) {
                throw new BadRequestException("User with ID " + dto.getTeacherId() + " does not have TEACHER role");
            }
            module.setTeacher(teacher);
        }

        if (!module.getFaculty().getId().equals(dto.getFacultyId())) {
            FacultySpr faculty = facultySprRepository.findById(dto.getFacultyId())
                    .orElseThrow(() -> new NotFoundException("Faculty not found with id: " + dto.getFacultyId()));
            module.setFaculty(faculty);
        }

        if (!module.getDepartment().getID().equals(dto.getDepartmentId())) {
            DepartmentSpr department = departmentSprRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found with id: " + dto.getDepartmentId()));
            module.setDepartment(department);
        }

        // Save updated module
        Module updatedModule = moduleRepository.save(module);

        // Get course with all modules and return CourseDto
        Course course = courseService.findById(updatedModule.getCourse().getId());
        com.malaka.aat.internal.dto.course.CourseDto courseDto = new com.malaka.aat.internal.dto.course.CourseDto(course);

        response.setData(courseDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse delete(String moduleId) {
        BaseResponse response = new BaseResponse();

        // Find the module
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found with id: " + moduleId));

        // Store course ID before deleting
        String courseId = module.getCourse().getId();

        // Delete the module (soft delete via @SQLDelete annotation)
        moduleRepository.delete(module);

        // Get course with remaining modules and return CourseDto
        Course course = courseService.findById(courseId);
        com.malaka.aat.internal.dto.course.CourseDto courseDto = new com.malaka.aat.internal.dto.course.CourseDto(course);

        response.setData(courseDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('TEACHER', 'METHODIST', 'ADMIN', 'SUPER_ADMIN')")
    public BaseResponse updateState(String moduleId, ModuleStateUpdateDto dto) {
        BaseResponse response = new BaseResponse();

        // Find the module
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found with id: " + moduleId));

        // Get current user
        String currentUserId = sessionService.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Current user not found"));

        // Check authorization based on state transition
        String targetState = dto.getState();

        if ("002".equals(targetState)) {
            // SENT state: Only teacher or admin can set this
            boolean isTeacher = module.getTeacher() != null && module.getTeacher().getId().equals(currentUserId);
            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(role -> "ADMIN".equals(role.getName()) || "SUPER_ADMIN".equals(role.getName()));

            if (!isTeacher && !isAdmin) {
                throw new AuthException("Only the assigned teacher or admin can mark module as SENT");
            }

            // Validate module completion before allowing SENT state
            validateModuleCompletion(module);
        } else if ("003".equals(targetState)) {
            // APPROVED or REJECTED: Only methodist or admin can set this
            boolean isMethodistOrAdmin = user.getRoles().stream()
                    .anyMatch(role -> "METHODIST".equals(role.getName()) ||
                            "ADMIN".equals(role.getName()) ||
                            "SUPER_ADMIN".equals(role.getName()));

            if (!isMethodistOrAdmin) {
                throw new AuthException("Only methodist or admin can approve or reject modules");
            }
        } else if ("004".equals(targetState)) {
            // APPROVED or REJECTED: Only methodist or admin can set this
            boolean isMethodistOrAdmin = user.getRoles().stream()
                    .anyMatch(role -> "METHODIST".equals(role.getName()) ||
                            "ADMIN".equals(role.getName()) ||
                            "SUPER_ADMIN".equals(role.getName()));

            if (dto.getDescription() == null) {
                throw new BadRequestException("Description is required");
            }

            String state = module.getCourse().getState();
            if (state.equals(CourseState.READY_TO_SEND_TO_FACULTY_HEAD.getValue())) {
                Course course = module.getCourse();
                course.setState(CourseState.SENT_TO_TEACHER.getValue());
                Course course1 = courseRepository.save(course);
                module.setCourse(course1);
            }

            if (!isMethodistOrAdmin) {
                throw new AuthException("Only methodist or admin can approve or reject modules");
            }
        }

        // Use ModuleState enum to validate and set state
        ModuleState.setState(module, targetState);


        ModuleState moduleState = Arrays.stream(ModuleState.values()).filter(f -> f.getValue().equals(dto.getState())).findFirst().orElseThrow();
        StateHModule stateHModule = stateHModuleService.createStateHModule(module, moduleState, dto.getDescription());

        // Save module
        Module updatedModule = moduleRepository.saveAndFlush(module);
        Course course = updatedModule.getCourse();
        if (course.getModules().stream().allMatch(m -> m.getModuleState().equals("002"))) {
            stateHMetService.saveStateForCourse(course, CourseState.READY_TO_SEND_TO_FACULTY_HEAD, null);
            CourseState.setState(course, CourseState.READY_TO_SEND_TO_FACULTY_HEAD.getValue());
            course = courseRepository.save(course);
        }

        updatedModule.getStateHistory().add(stateHModule);
        prepareCourseForUser(course);
        CourseDto courseDto = new CourseDto(course);
        response.setData(courseDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private void prepareCourseForUser(Course course) {
        User currentUser = sessionService.getCurrentUser();

        boolean isTeacher = currentUser.getRoles().stream()
                .anyMatch(role -> "TEACHER".equals(role.getName()));

        if (isTeacher) {
            course.setModules(
                    course.getModules().stream().filter(m -> m.getTeacher().getId().equals(currentUser.getId())).collect(Collectors.toList())
            );
        }
    }


    private void validateModuleCompletion(Module module) {
        // Check if topic count matches
        Integer expectedTopicCount = module.getTopicCount();
        List<Topic> topics = module.getTopics();

        if (expectedTopicCount == null) {
            throw new BadRequestException(
                    "Module '" + module.getName() + "' does not have a topic count defined"
            );
        }

        if (topics == null || topics.size() != expectedTopicCount) {
            throw new BadRequestException(
                    "Module '" + module.getName() + "' has " + (topics == null ? 0 : topics.size()) +
                            " topics, but requires " + expectedTopicCount + " topics to be created"
            );
        }

        // Check each topic for completeness
        List<String> incompleteTopics = new ArrayList<>();
        for (Topic topic : topics) {
            List<String> missingItems = new ArrayList<>();

//            if (topic.getContentFile() == null) {
//                missingItems.add("content (video/audio)");
//            }
            if (topic.getLectureFile() == null) {
                missingItems.add("lecture (PDF)");
            }
            if (topic.getPresentationFile() == null) {
                missingItems.add("presentation (PDF)");
            }

            if (!missingItems.isEmpty()) {
                incompleteTopics.add("Topic '" + topic.getName() + "' is missing: " + String.join(", ", missingItems));
            }
        }

        if (!incompleteTopics.isEmpty()) {
            throw new BadRequestException(
                    "Cannot mark module '" + module.getName() + "' as SENT. The following topics are incomplete:\n" +
                            String.join("\n", incompleteTopics)
            );
        }
    }

    @Transactional
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public BaseResponse createTopic(String moduleId, TopicCreateDto dto) {
        BaseResponse response = new BaseResponse();

        if (topicRepository.existsByModuleIdAndName(moduleId, dto.getName())) {
            throw new AlreadyExistsException("Topic with name '" + dto.getName() + "' already exists in this module");
        }

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found with id: " + moduleId));

        if (!module.getCourse().getState().equals("002")) {
            throw new BadRequestException("Module state is not 002");
        }

        List<Topic> topics = module.getTopics();
        if (topics.stream().anyMatch(t -> Objects.equals(t.getOrder(), dto.getOrder()))) {
            throw new BadRequestException("Topic has already been created with order " + dto.getOrder());
        }

        if (module.getTopicCount() < dto.getOrder()) {
            throw new BadRequestException("Maximum order for the module topic is " + module.getTopicCount());
        }

        String currentUserId = sessionService.getCurrentUserId();
        User user = userRepository.findById(currentUserId).get();

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(auth -> auth.getName().equals("ADMIN") ||
                        auth.getName().equals("SUPER_ADMIN"));

        if (!isAdmin && module.getTeacher() != null && !module.getTeacher().getId().equals(currentUserId)) {
            throw new AuthException();
        }

        Topic topic = new Topic();
        topic.setName(dto.getName());
        topic.setModule(module);
        topic.setOrder(dto.getOrder());

        Topic savedTopic = topicRepository.save(topic);

        TopicDto topicDto = new TopicDto(savedTopic);
        response.setData(topicDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

        return response;
    }

    public BaseResponse getById(String id) {
        BaseResponse response = new BaseResponse();
        Optional<Module> byId = moduleRepository.findById(id);

        if (byId.isEmpty()) {
            throw new NotFoundException("Module with id: " + id + " not found");
        }

        ModuleDto moduleDto = new ModuleDto(byId.get());
        response.setData(moduleDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }
}
