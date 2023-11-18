package org.quiztastic.questionservice.service;

import lombok.RequiredArgsConstructor;
import org.quiztastic.questionservice.dto.AddQuestionRequest;
import org.quiztastic.questionservice.model.EntityStatus;
import org.quiztastic.questionservice.model.Question;
import org.quiztastic.questionservice.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    Logger logger = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository questionRepository;

    private final WebClient.Builder webClientBuilder;

    public List<Question> getQuestionsByUser(String username) {
        return questionRepository.findAllByUsernameOrderByCreationDesc(username);
    }

    public void addQuestion(AddQuestionRequest questionRequest, String username) throws Exception {
        if (questionRequest.getTitle() == null || questionRequest.getTitle().isBlank()) {
            logger.error(MessageFormat.format("User {0} | Invalid title. Received: {1}", username, questionRequest.getTitle()));
            throw new Exception();
        }

        if (questionRequest.getCorrect() == null || questionRequest.getCorrect().isBlank()) {
            logger.error(MessageFormat.format("User {0} | Invalid correct answer. Received: {1}", username, questionRequest.getCorrect()));
            throw new Exception();
        }

        if (questionRequest.getWrong1() == null || questionRequest.getWrong1().isBlank()) {
            logger.error(MessageFormat.format("User {0} | Invalid wrong answer 1. Received: {1}", username, questionRequest.getWrong1()));
            throw new Exception();
        }

        if (questionRequest.getWrong2() == null || questionRequest.getWrong2().isBlank()) {
            logger.error(MessageFormat.format("User {0} | Invalid wrong answer 2. Received: {1}", username, questionRequest.getWrong2()));
            throw new Exception();
        }

        if (questionRequest.getWrong3() == null || questionRequest.getWrong3().isBlank()) {
            logger.error(MessageFormat.format("User {0} | Invalid wrong answer 3. Received: {1}", username, questionRequest.getWrong3()));
            throw new Exception();
        }

        Question question = Question.builder()
                .title(questionRequest.getTitle().trim())
                .correct(questionRequest.getCorrect().trim())
                .wrong1(questionRequest.getWrong1().trim())
                .wrong2(questionRequest.getWrong2().trim())
                .wrong3(questionRequest.getWrong3().trim())
                .creation(new Date())
                .status(EntityStatus.ACTIVE)
                .username(username)
                .build();

        questionRepository.save(question);
        logger.info(MessageFormat.format("User {0} | Question added successfully", username));
    }
}
