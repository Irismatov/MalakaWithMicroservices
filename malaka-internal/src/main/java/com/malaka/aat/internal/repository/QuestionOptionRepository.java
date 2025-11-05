package com.malaka.aat.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.malaka.aat.internal.model.QuestionOption;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, String> {

    void deleteByQuestionId(String questionId);
}
