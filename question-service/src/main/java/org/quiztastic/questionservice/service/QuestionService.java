package org.quiztastic.questionservice.service;

import lombok.RequiredArgsConstructor;
import org.quiztastic.questionservice.model.Question;
import org.quiztastic.questionservice.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAllByOrderByCreationDesc();
    }
}
