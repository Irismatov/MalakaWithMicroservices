package com.malaka.aat.internal.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.AlreadyExistsException;
import com.malaka.aat.core.exception.custom.BadRequestException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.internal.clients.MalakaExternalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.malaka.aat.internal.dto.auth.UserRegisterDto;
import com.malaka.aat.internal.dto.user.*;
import com.malaka.aat.internal.model.Role;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.LangSpr;
import com.malaka.aat.internal.repository.UserRepository;
import com.malaka.aat.internal.service.spr.LangSprService;
import com.malaka.aat.internal.service.spr.RoleService;
import com.malaka.aat.internal.util.ServiceUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final RoleService roleService;
    private final LangSprService langSprService;
    private final MalakaExternalClient malakaExternalClient;

    public BaseResponse save(UserRegisterDto userRegisterDto) {
        User user = UserRegisterDto.mapDtoToEntity(userRegisterDto);

        // Check for unique username
        if (isUsernameExists(userRegisterDto.getUsername())) {
            throw new AlreadyExistsException("User already exists with username " + userRegisterDto.getUsername());
        }

        // Check for unique pinfl (if provided)
        if (userRegisterDto.getPinfl() != null && !userRegisterDto.getPinfl().isBlank()) {
            if (isPinflExists(userRegisterDto.getPinfl())) {
                throw new AlreadyExistsException("User already exists with pinfl " + userRegisterDto.getPinfl());
            }
        }

        Long lang = userRegisterDto.getLang();
        LangSpr byId = langSprService.findById(lang);
        user.setLang(byId);

        // Set password (encoded)
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        // Always assign USER role for registration
        Set<Role> roles = roleService.findRolesByNames("USER");
        user.setRoles(roles);

        userRepository.save(user);

        BaseResponse response = new BaseResponse();
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse createUser(UserCreateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        if (isUsernameExists(dto.getUsername())) {
            throw new AlreadyExistsException("User already exists with username: " + dto.getUsername());
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setMiddleName(dto.getMiddleName());
        user.setPinfl(dto.getPinfl());
        user.setPhone(dto.getPhone());

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleId : dto.getRoleIds()) {
                Role role = roleService.findById(roleId);
                roles.add(role);
            }
            user.setRoles(roles);
        }

        userRepository.save(user);
        Page<UserListDto> lastUpdatedUsers = getLastUpdatedUsers();
        response.setData(lastUpdatedUsers, 0);

        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination getList(int page, int size, UserFilterDto filterDto) {
        ResponseWithPagination response = new ResponseWithPagination();
        PageRequest pageRequest = ServiceUtil.preparePageRequest(page, size);

        // Add wildcard characters for LIKE queries
        String username = addWildcards(filterDto.getUsername());
        String firstName = addWildcards(filterDto.getFirstName());
        String lastName = addWildcards(filterDto.getLastName());
        String middleName = addWildcards(filterDto.getMiddleName());
        String pinfl = addWildcards(filterDto.getPinfl());
        String phone = addWildcards(filterDto.getPhone());

        Page<User> users = userRepository.findAllWithFilters(
                username,
                firstName,
                lastName,
                middleName,
                pinfl,
                phone,
                filterDto.getRoleId(),
                pageRequest
        );

        Page<UserListDto> userPage = users.map(this::convertToUserListDto);

        response.setData(userPage, page);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public BaseResponse getByIdDto(String id) {
        BaseResponse response = new BaseResponse();
        Optional<User> optionalUser = userRepository.findDtoById(id);
        if (optionalUser.isEmpty()) {
            throw new BadRequestException("User not found with id: " + id);
        }
        UserListDto userDto = convertToUserListDto(optionalUser.get());
        response.setData(userDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination update(String id, UserUpdateDto dto) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new BadRequestException("User not found with id: " + id);
        }

        User user = optionalUser.get();

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            if (!user.getUsername().equals(dto.getUsername()) && isUsernameExists(dto.getUsername())) {
                throw new AlreadyExistsException("User already exists with username: " + dto.getUsername());
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getMiddleName() != null) {
            user.setMiddleName(dto.getMiddleName());
        }

        if (dto.getLang() != null) {
            LangSpr byId = langSprService.findById(dto.getLang());
            user.setLang(byId);
        }

        if (dto.getPinfl() != null) {
            user.setPinfl(dto.getPinfl());
        }

        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleId : dto.getRoleIds()) {
                Role role = roleService.findById(roleId);
                roles.add(role);
            }
            user.setRoles(roles);
        }

        userRepository.save(user);
        Page<UserListDto> lastUpdatedUsers = getLastUpdatedUsers();
        response.setData(lastUpdatedUsers, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public ResponseWithPagination delete(String id) {
        ResponseWithPagination response = new ResponseWithPagination();

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new BadRequestException("User not found with id: " + id);
        }

        userRepository.delete(optionalUser.get());
        Page<UserListDto> lastUpdatedUsers = getLastUpdatedUsers();
        response.setData(lastUpdatedUsers, 0);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    private Page<UserListDto> getLastUpdatedUsers() {
        PageRequest pageRequest = ServiceUtil.prepareDefaultPageRequest();
        Page<User> users = userRepository.findAllWithFilters(null, null, null, null, null, null, null, pageRequest);
        return users.map(this::convertToUserListDto);
    }

    private UserListDto convertToUserListDto(User user) {
        UserListDto dto = new UserListDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMiddleName(user.getMiddleName());
        dto.setLang(user.getLang().getId());
        dto.setPinfl(user.getPinfl());
        dto.setPhone(user.getPhone());

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            String roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            dto.setRoleNames(roleNames);
        }

        dto.setCreatedAt(user.getInstime());
        dto.setUpdatedAt(user.getUpdtime());

        return dto;
    }

    private boolean isUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean isPinflExists(String pinfl) {
        return userRepository.findByPinfl(pinfl).isPresent();
    }

    private String addWildcards(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return "%" + value + "%";
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found with username " + username));
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }

    public BaseResponse getMe() {
        BaseResponse response = new BaseResponse();
        User currentUser = sessionService.getCurrentUser();
        UserDto userDto = UserDto.mapEntityToDto(currentUser);
        response.setData(userDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    @PostAuthorize("!returnObject.roles.?[name == 'FACULTY_HEAD'].empty")
    public User getFacultyHead(String id) {
        return userRepository.findById(id).orElseThrow
                (() -> new NotFoundException("User not found with id " + id));
    }

    public BaseResponse getUsersForReference(UserFilterDto filterDto) {
        BaseResponse response = new BaseResponse();

        // Add wildcard characters for LIKE queries
        String username = addWildcards(filterDto.getUsername());
        String firstName = addWildcards(filterDto.getFirstName());
        String lastName = addWildcards(filterDto.getLastName());
        String middleName = addWildcards(filterDto.getMiddleName());
        String pinfl = addWildcards(filterDto.getPinfl());
        String phone = addWildcards(filterDto.getPhone());

        List<User> users = userRepository.findAllWithFiltersNoPagination(
                username,
                firstName,
                lastName,
                middleName,
                pinfl,
                phone,
                filterDto.getRoleId()
        );

        List<UserListDto> userDtos = users.stream()
                .map(this::convertToUserListDto)
                .collect(Collectors.toList());

        response.setData(userDtos);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    /**
     * Get all users with TEACHER role
     */
    public BaseResponse getTeachers() {
        BaseResponse response = new BaseResponse();

        // Find TEACHER role
        Role teacherRole = roleService.findByName("TEACHER");

        // Query users with TEACHER role
        List<User> teachers = userRepository.findAllWithFiltersNoPagination(
                null, null, null, null, null, null, teacherRole.getId()
        );

        List<UserListDto> teacherDtos = teachers.stream()
                .map(this::convertToUserListDto)
                .collect(Collectors.toList());

        response.setData(teacherDtos);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }


    public BaseResponse getStudents(Long type) {
        try {
            BaseResponse response = malakaExternalClient.getStudentsByType(type);
            return response;
        } catch (Exception e) {
            throw new SystemException("Error occurred when sending a request to a external service");
        }
    }

    public BaseResponse getStudentsByCourseIdAndType(String courseId, Long type) {
        try {
            BaseResponse response = malakaExternalClient.getStudentsByCourseIdAndType(courseId, type);
            return response;
        } catch (Exception e) {
            throw new SystemException("Error occurred when sending a request to a external service");
        }
    }
}
