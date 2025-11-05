package com.malaka.aat.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.malaka.aat.internal.model.File;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> findByHash(String hash);
}
