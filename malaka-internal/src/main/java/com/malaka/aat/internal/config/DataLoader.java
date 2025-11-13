package com.malaka.aat.internal.config;

import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.internal.model.Role;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.*;
import com.malaka.aat.internal.repository.RoleRepository;
import com.malaka.aat.internal.repository.UserRepository;
import com.malaka.aat.internal.repository.spr.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FacultySprRepository facultySprRepository;
    private final DepartmentSprRepository departmentSprRepository;
    private final CourseFormatSprRepository courseFormatSprRepository;
    private final CourseTypeSprRepository courseTypeSprRepository;
    private final CourseStudentTypeSprRepository courseStudentTypeSprRepository;
    private final LangSprRepository langSprRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultLanguages();
        initializeDefaultRoles();
        initializeDefaultEmployees();
        initializeDefaultFaculties();
        initializeDefaultDepartments();
        initializeDefaultCourseFormats();
        initializeDefaultCourseTypes();
        initializeDefaultCourseStudentTypes();
    }

    private void initializeDefaultFaculties() {
        int created = 0;

        created += createFacultyIfNotExists("Bojxona ishi fakulteti") ? 1 : 0;
        created += createFacultyIfNotExists("QT va MOF") ?  1 : 0;



        if (created > 0) {
            log.info("Created {} new faculty(s)", created);
        }
    }

    private boolean createFacultyIfNotExists(String name) {
        if (facultySprRepository.findByName(name).isPresent()) {
            return false;
        }

        FacultySpr facultySpr = new FacultySpr();
        facultySpr.setName(name);
        facultySprRepository.save(facultySpr);
        return true;
    }

    private void initializeDefaultDepartments() {
        int created = 0;

        created += createDefaultDepartmentsIfNotExists("Maxsus fanlar kafedrasi", "QT va MOF") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Bojxona nazorati kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Bojxona to'lovlari va statistika kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Maxsus-huquqiy fanlar kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Tovarlarni tasniflash va notarif nazorat kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Umumhuquqiy fanlar kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Matematika va axborot texnologiyalari kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Tillarni o‘rganish kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Harbiy va jismoniy tayyorgarlik kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Ijtimoiy-gumanitar fanlar kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Iqtisodiy fanlar kafedrasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("BNTVning o‘quv laboratoriyasi", "Bojxona ishi fakulteti") ? 1 : 0;
        created += createDefaultDepartmentsIfNotExists("Psixologik tayyorgarlikni rivojlantirish markazi", "Bojxona ishi fakulteti") ? 1 : 0;




        if (created > 0) {
            log.info("Created {} new department(s)", created);
        }
    }

    private boolean createDefaultDepartmentsIfNotExists(String name, String facultyName) {
        if (departmentSprRepository.findByName(name).isPresent()) {
            return false;
        }

        FacultySpr facultySpr = facultySprRepository.findByName(facultyName).orElseThrow(()
                -> new NotFoundException("Faculty not found with name: " + facultyName));


        DepartmentSpr departmentSpr = new DepartmentSpr();
        departmentSpr.setName(name);
        departmentSpr.setFacultySpr(facultySpr);
        departmentSprRepository.save(departmentSpr);
        return true;
    }

    private void initializeDefaultRoles() {
        int created = 0;
        created += createRoleIfNotExists("ADMIN", "Administrator role with full access") ? 1 : 0;
        created += createRoleIfNotExists("TEACHER", "Teacher role for managing courses and students") ? 1 : 0;
        created += createRoleIfNotExists("METHODIST", "Methodist role for managing educational content") ? 1 : 0;
        created += createRoleIfNotExists("LIBRARIAN", "Librarian role for managing library resources") ? 1 : 0;
        created += createRoleIfNotExists("SUPER_ADMIN", "Super administrator with highest level access") ? 1 : 0;
        created += createRoleIfNotExists("FACULTY_HEAD", "Faculty head role for managing faculty") ? 1 : 0;
        created += createRoleIfNotExists("DEPARTMENT_HEAD", "Department head role for managing department") ? 1 : 0;
        created += createRoleIfNotExists("STUDENT", "Student role") ? 1 : 0;
        created += createRoleIfNotExists("USER",  "User role") ? 1 : 0;


        if (created > 0) {
            log.info("Created {} new role(s)", created);
        }
    }

    private boolean createRoleIfNotExists(String name, String description) {
        if (roleRepository.findByName(name).isPresent()) {
            return false;
        }

        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        roleRepository.save(role);
        return true;
    }

    private void initializeDefaultEmployees() {
        int created = 0;
        created += createEmployeeIfNotExists("admin", "ADMIN", "Botirovich", "Abduqodir", "Husanov", "1234567890123456") ? 1 : 0;
        created += createEmployeeIfNotExists("superadmin", "SUPER_ADMIN", "Akmalovich", "Shomurodov", "Eldor", "3234567890123456") ? 1 : 0;
        created += createEmployeeIfNotExists("teacher", "TEACHER", "Soatovich", "Toshmatov", "Dilshod", "4234567890123456") ? 1 : 0;
        created += createEmployeeIfNotExists("methodist", "METHODIST", "Jasurovna", "Ergasheva", "Gulnora", "5234567890123456") ? 1 : 0;
        created += createEmployeeIfNotExists("librarian", "LIBRARIAN", "Toxirovna", "Rahmanova", "Nodira", "6234567890123456") ? 1 : 0;
        created += createEmployeeIfNotExists("faculty_head", "FACULTY_HEAD", "Anvarovich", "Ismoilov", "Aziz", "7234567890123456") ? 1 : 0;
        created += createEmployeeIfNotExists("department_head", "DEPARTMENT_HEAD", "Shokirovna", "Kamilova", "Feruza", "8234567890123456") ? 1 : 0;
        created += createEmployeeIfNotExists("teacher-2", "TEACHER", "Husanovcih", "Hasanov", "JASUR", "833356789812316") ? 1 : 0;
        created += createEmployeeIfNotExists("teacher-3", "TEACHER", "Maratovich", "Sergeev", "Igor", "833356789812316") ? 1 : 0;
        // Service account for malaka-external to access malaka-internal APIs
        created += createServiceAccountIfNotExists("external-service", "ADMIN", "ExternalService@2024") ? 1 : 0;


        if (created > 0) {
            log.info("Created {} new default user(s)", created);
        }
    }

    private boolean createEmployeeIfNotExists(
            String username,
            String roleName,
            String middleName,
            String lastName,
            String firstName,
            String pinfl
    ) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false;
        }

        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            log.error("Role '{}' not found, cannot create user '{}'", roleName, username);
            return false;
        }

        User employee = new User();
        employee.setUsername(username);
        employee.setPassword(passwordEncoder.encode("12345678"));
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setMiddleName(middleName);
        employee.setPinfl(pinfl);
        employee.setRoles(Set.of(role.get()));

        userRepository.save(employee);
        return true;
    }

    /**
     * Creates a service account for inter-service communication.
     * Service accounts have specific passwords and minimal required information.
     */
    private boolean createServiceAccountIfNotExists(
            String username,
            String roleName,
            String password
    ) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false;
        }

        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            log.error("Role '{}' not found, cannot create service account '{}'", roleName, username);
            return false;
        }

        User serviceAccount = new User();
        serviceAccount.setUsername(username);
        serviceAccount.setPassword(passwordEncoder.encode(password));
        serviceAccount.setFirstName("External");
        serviceAccount.setLastName("Service");
        serviceAccount.setMiddleName("Account");
        serviceAccount.setPinfl("9999999999999999"); // Dummy PINFL for service account
        serviceAccount.setRoles(Set.of(role.get()));

        userRepository.save(serviceAccount);
        log.info("Created service account: {}", username);
        return true;
    }

    private void initializeDefaultCourseFormats() {
        int created = 0;

        // Create default course formats matching the old enum values
        created += createCourseFormatIfNotExists(0L, "OFFLINE", "Kurslar to'liq dastur ichida tugatiladi ZOOM darslar mavjud emas") ? 1 : 0;
        created += createCourseFormatIfNotExists(1L, "ONLINE", "Kurslar to'liq dastur ichida tugatiladi, ZOOM darslari bo'lishi mumkin") ? 1 : 0;
        created += createCourseFormatIfNotExists(2L, "ARALASH", "1oy online va 2 oy muassasada o'qitiladigan kurs") ? 1 : 0;
        created += createCourseFormatIfNotExists(3L, "An’anaviy", "Faqat muassasada o'rgatiladi, faqatgina yakuniy testdan o'tish majburiy") ? 1 : 0;

        if (created > 0) {
            log.info("Created {} new course format(s)", created);
        }
    }

    private boolean createCourseFormatIfNotExists(Long id, String name, String description) {
        if (courseFormatSprRepository.findById(id).isPresent()) {
            return false;
        }

        CourseFormatSpr courseFormatSpr = new CourseFormatSpr();
        courseFormatSpr.setId(id);
        courseFormatSpr.setName(name);
        courseFormatSpr.setDescription(description);
        courseFormatSprRepository.save(courseFormatSpr);
        return true;
    }

    private void initializeDefaultCourseTypes() {
        int created = 0;

        // Create default course types matching the old enum values (starting from 0)
        created += createCourseTypeIfNotExists(0L, "ASOSIY", "Asosiy kurs") ? 1 : 0;
        created += createCourseTypeIfNotExists(1L, "QO'SHIMCHA", "Qo'shimcha kurs") ? 1 : 0;
        created += createCourseTypeIfNotExists(2L, "Sohaviy", "Faqt ichki hodimlar uchuh") ? 1 : 0;

        if (created > 0) {
            log.info("Created {} new course type(s)", created);
        }
    }

    private boolean createCourseTypeIfNotExists(Long id, String name, String description) {
        if (courseTypeSprRepository.findById(id).isPresent()) {
            return false;
        }

        CourseTypeSpr courseTypeSpr = new CourseTypeSpr();
        courseTypeSpr.setId(id);
        courseTypeSpr.setName(name);
        courseTypeSpr.setDescription(description);
        courseTypeSprRepository.save(courseTypeSpr);
        return true;
    }

    private void initializeDefaultCourseStudentTypes() {
        int created = 0;

        // Create default course student types matching the old enum values (starting from 0)
        created += createCourseStudentTypeIfNotExists(0L, "XODIM", "Internal student type") ? 1 : 0;
        created += createCourseStudentTypeIfNotExists(1L, "TIF ISHTIROKCHISI", "External student type") ? 1 : 0;

        if (created > 0) {
            log.info("Created {} new course student type(s)", created);
        }
    }

    private boolean createCourseStudentTypeIfNotExists(Long id, String name, String description) {
        if (courseStudentTypeSprRepository.findById(id).isPresent()) {
            return false;
        }

        CourseStudentTypeSpr courseStudentTypeSpr = new CourseStudentTypeSpr();
        courseStudentTypeSpr.setId(id);
        courseStudentTypeSpr.setName(name);
        courseStudentTypeSpr.setDescription(description);
        courseStudentTypeSprRepository.save(courseStudentTypeSpr);
        return true;
    }

    private void initializeDefaultLanguages() {
        int created = 0;

        // Create default languages matching the old Lang enum values (starting from 0)
        created += createLanguageIfNotExists(0L, "UZ") ? 1 : 0;
        created += createLanguageIfNotExists(1L, "RU") ? 1 : 0;

        if (created > 0) {
            log.info("Created {} new language(s)", created);
        }
    }

    private boolean createLanguageIfNotExists(Long id, String name) {
        if (langSprRepository.findById(id).isPresent()) {
            return false;
        }

        LangSpr langSpr = new LangSpr();
        langSpr.setId(id);
        langSpr.setName(name);
        langSprRepository.save(langSpr);
        return true;
    }
}
