package com.malaka.aat.external.config;

import com.malaka.aat.external.model.Role;
import com.malaka.aat.external.model.User;
import com.malaka.aat.external.model.spr.LangSpr;
import com.malaka.aat.external.model.spr.StudentTypeSpr;
import com.malaka.aat.external.repository.RoleRepository;
import com.malaka.aat.external.repository.UserRepository;
import com.malaka.aat.external.repository.spr.LangSprRepository;
import com.malaka.aat.external.repository.spr.StudentTypeSprRepository;
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

    private final RoleRepository roleRepository;
    private final LangSprRepository langSprRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentTypeSprRepository studentTypeSprRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultLanguages();
        initializeDefaultRoles();
        initializeDefaultUsers();
        initializeDefaultStudentTypes();
    }

    private void initializeDefaultStudentTypes() {
        int created = 0;
        created += createStudentTypeIfNotExists(0L, "INTERNAL") ? 1 : 0;
        created += createStudentTypeIfNotExists(1L, "EXTERNAL") ? 1 : 0;
        if (created > 0) {
            log.info("Created {} new student types", created);
        }
    }

    private boolean createStudentTypeIfNotExists(long id, String name) {
        StudentTypeSpr studentTypeSpr = new StudentTypeSpr();
        if (studentTypeSprRepository.findById(id).isPresent()) {
            return false;
        }
        studentTypeSpr.setId(id);
        studentTypeSpr.setName(name);
        studentTypeSprRepository.save(studentTypeSpr);
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

    private void initializeDefaultRoles() {
        int created = 0;
        created += createRoleIfNotExists("ADMIN", "Administrator role with full access") ? 1 : 0;
        created += createRoleIfNotExists("USER", "User role for external users") ? 1 : 0;
        created += createRoleIfNotExists("STUDENT", "Student role for course participants") ? 1 : 0;

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

    private void initializeDefaultUsers() {
        int created = 0;
        created += createUserIfNotExists("ADMIN", "Abduqodirovich", "Husanov", "Botir", "1234567890123456") ? 1 : 0;
        created += createUserIfNotExists("USER", "Shuxratovich", "Rahimov", "Sardor", "2345678901234567") ? 1 : 0;
        created += createUserIfNotExists("STUDENT", "Dilshodovich", "Toshmatov", "Jasur", "3456789012345678") ? 1 : 0;
        // Service account for malaka-internal to access malaka-external APIs
        created += createServiceAccountIfNotExists("internal-service", "ADMIN", "InternalService@2025") ? 1 : 0;

        if (created > 0) {
            log.info("Created {} new default user(s)", created);
        }
    }

    private boolean createUserIfNotExists(
            String roleName,
            String middleName,
            String lastName,
            String firstName,
            String pinfl
    ) {
        if (userRepository.findByPinfl(pinfl).isPresent()) {
            return false;
        }

        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            log.error("Role '{}' not found, cannot create user '{}'", roleName, pinfl);
            return false;
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMiddleName(middleName);
        user.setPinfl(pinfl);
        user.setRoles(Set.of(role.get()));

        userRepository.save(user);
        return true;
    }


    private boolean createServiceAccountIfNotExists(
            String pinfl,
            String roleName,
            String password
    ) {
        if (userRepository.findByPinfl(pinfl).isPresent()) {
            return false;
        }

        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            log.error("Role '{}' not found, cannot create service account '{}'", roleName, pinfl);
            return false;
        }

        User serviceAccount = new User();
        serviceAccount.setPassword(passwordEncoder.encode(password));
        serviceAccount.setFirstName("Internal");
        serviceAccount.setLastName("Service");
        serviceAccount.setMiddleName("Account");
        serviceAccount.setPinfl(pinfl);
        serviceAccount.setRoles(Set.of(role.get()));

        userRepository.save(serviceAccount);
        log.info("Created service account: {}", pinfl);
        return true;
    }
}
