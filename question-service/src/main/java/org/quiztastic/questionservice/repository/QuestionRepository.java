package org.quiztastic.questionservice.repository;

import org.quiztastic.questionservice.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.username = ?1 AND q.status = 'ACTIVE' ORDER BY q.creation DESC")
    List<Question> findActiveQuestionsByUsername(String username);
}
