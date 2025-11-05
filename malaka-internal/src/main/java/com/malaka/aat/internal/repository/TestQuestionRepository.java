package com.malaka.aat.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.malaka.aat.internal.model.TestQuestion;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, String> {
    void deleteByTestId(String testId);
}
