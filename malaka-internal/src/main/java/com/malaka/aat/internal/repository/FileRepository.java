package com.malaka.aat.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.malaka.aat.internal.model.File;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, String> {
    @Query("from File f where f.hash = :hash order by f.instime limit 1")
    Optional<File> findByHash(@Param("hash") String hash);
}
