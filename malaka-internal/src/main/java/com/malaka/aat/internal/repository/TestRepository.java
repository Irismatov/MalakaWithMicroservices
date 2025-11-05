package com.malaka.aat.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.malaka.aat.internal.model.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, String> {

}
