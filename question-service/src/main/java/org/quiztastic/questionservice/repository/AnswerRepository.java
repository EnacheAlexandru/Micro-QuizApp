package org.quiztastic.questionservice.repository;

import org.quiztastic.questionservice.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.username = ?1 AND a.question.id = ?2")
    Long doesUserAlreadyAnswerQuestion(String username, Long id);
}
