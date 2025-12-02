package com.malaka.aat.external.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.malaka.aat.external.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByPinfl(String pinfl);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(:firstName)) " +
            "AND (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(:lastName)) " +
            "AND (:middleName IS NULL OR LOWER(u.middleName) LIKE LOWER(:middleName)) " +
            "AND (:pinfl IS NULL OR u.pinfl LIKE :pinfl) " +
            "AND (:phone IS NULL OR u.phone LIKE :phone) " +
            "AND (:roleId IS NULL OR EXISTS (SELECT 1 FROM u.roles r2 WHERE r2.id = :roleId)) " +
            "ORDER BY u.updtime DESC")
    Page<User> findAllWithFilters(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("pinfl") String pinfl,
            @Param("phone") String phone,
            @Param("roleId") String roleId,
            Pageable pageable
    );

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE u.id = :id")
    Optional<User> findDtoById(@Param("id") String id);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(:firstName)) " +
            "AND (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(:lastName)) " +
            "AND (:middleName IS NULL OR LOWER(u.middleName) LIKE LOWER(:middleName)) " +
            "AND (:pinfl IS NULL OR u.pinfl LIKE :pinfl) " +
            "AND (:phone IS NULL OR u.phone LIKE :phone) " +
            "AND (:roleId IS NULL OR EXISTS (SELECT 1 FROM u.roles r2 WHERE r2.id = :roleId)) " +
            "ORDER BY u.updtime DESC")
    List<User> findAllWithFiltersNoPagination(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("pinfl") String pinfl,
            @Param("phone") String phone,
            @Param("roleId") String roleId
    );

}
