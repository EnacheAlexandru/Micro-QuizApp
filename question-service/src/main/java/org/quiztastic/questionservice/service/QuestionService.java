package org.quiztastic.questionservice.service;

import lombok.RequiredArgsConstructor;
import org.quiztastic.questionservice.dto.AddQuestionRequest;
import org.quiztastic.questionservice.dto.UpdateQuestionRequest;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    Logger logger = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository questionRepository;

    private final WebClient.Builder webClientBuilder;

    public List<Question> getQuestionsByUser(String username) {
        return questionRepository.findAllByUsernameOrderByCreationDesc(username);
    }

    public Question getQuestionById(Long id, String username) throws Exception {
        if (username == null) {
            logger.error("Invalid username. Received: null");
            throw new Exception();
        }

        if (id == null) {
            logger.error(MessageFormat.format("User {0} | Invalid id. Received: null", username));
            throw new Exception();
        }

        Optional<Question> question;
        try {
            question = questionRepository.findById(id);
        } catch (Exception e) {
            logger.error(MessageFormat.format("User {0} | Error updating question", username));
            throw new Exception();
        }

        if (question.isEmpty()) {
            logger.error(MessageFormat.format("User {0} | Question with id {1} not found", username, id));
            throw new Exception();
        }

        if (!username.equals(question.get().getUsername())) {
            logger.error(MessageFormat.format("User {0} | Question found for user {1}", username, question.get().getUsername()));
            throw new Exception();
        }

        logger.info(MessageFormat.format("User {0} | Question identified successfully", username));
        return question.get();
    }

    public void addQuestion(AddQuestionRequest questionRequest, String username) throws Exception {
        if (username == null) {
            logger.error("Invalid username. Received: null");
            throw new Exception();
        }

        if (questionRequest.getTitle() == null || questionRequest.getTitle().isBlank() || questionRequest.getTitle().length() > 100) {
            logger.error(MessageFormat.format("User {0} | Invalid title. Received: {1}", username, questionRequest.getTitle()));
            throw new Exception();
        }

        if (questionRequest.getCorrect() == null || questionRequest.getCorrect().isBlank() || questionRequest.getCorrect().length() > 100) {
            logger.error(MessageFormat.format("User {0} | Invalid correct answer. Received: {1}", username, questionRequest.getCorrect()));
            throw new Exception();
        }

        if (questionRequest.getWrong1() == null || questionRequest.getWrong1().isBlank() || questionRequest.getWrong1().length() > 100) {
            logger.error(MessageFormat.format("User {0} | Invalid wrong answer 1. Received: {1}", username, questionRequest.getWrong1()));
            throw new Exception();
        }

        if (questionRequest.getWrong2() == null || questionRequest.getWrong2().isBlank() || questionRequest.getWrong2().length() > 100) {
            logger.error(MessageFormat.format("User {0} | Invalid wrong answer 2. Received: {1}", username, questionRequest.getWrong2()));
            throw new Exception();
        }

        if (questionRequest.getWrong3() == null || questionRequest.getWrong3().isBlank() || questionRequest.getWrong3().length() > 100) {
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

        try {
            questionRepository.save(question);
            logger.info(MessageFormat.format("User {0} | Question added successfully", username));
        } catch (Exception e) {
            logger.error(MessageFormat.format("User {0} | Error saving question", username));
            throw new Exception();
        }
    }

    public void updateQuestion(UpdateQuestionRequest questionRequest, String username) throws Exception {
        if (username == null) {
            logger.error("Invalid username. Received: null");
            throw new Exception();
        }

        if (questionRequest.getId() == null) {
            logger.error(MessageFormat.format("User {0} | Invalid id. Received: null", username));
            throw new Exception();
        }

        Question question;
        try {
            question = getQuestionById(questionRequest.getId(), username);
        } catch (Exception e) {
            throw new Exception();
        }

        boolean isSomethingToEdit = false;
        if (questionRequest.getTitle() != null) {
            if (questionRequest.getTitle().isBlank() || questionRequest.getTitle().length() > 100) {
                logger.error(MessageFormat.format("User {0} | Invalid title. Received: {1}", username, questionRequest.getTitle()));
                throw new Exception();
            } else {
                if (!questionRequest.getTitle().equals(question.getTitle())) {
                    question.setTitle(questionRequest.getTitle());
                    isSomethingToEdit = true;
                }
            }
        }

        if (questionRequest.getCorrect() != null) {
            if (questionRequest.getCorrect().isBlank() || questionRequest.getCorrect().length() > 100) {
                logger.error(MessageFormat.format("User {0} | Invalid correct answer. Received: {1}", username, questionRequest.getCorrect()));
                throw new Exception();
            } else {
                if (!questionRequest.getCorrect().equals(question.getCorrect())) {
                    question.setCorrect(questionRequest.getCorrect());
                    isSomethingToEdit = true;
                }
            }
        }

        if (questionRequest.getWrong1() != null) {
            if (questionRequest.getWrong1().isBlank() || questionRequest.getWrong1().length() > 100) {
                logger.error(MessageFormat.format("User {0} | Invalid wrong answer 1. Received: {1}", username, questionRequest.getWrong1()));
                throw new Exception();
            } else {
                if (!questionRequest.getWrong1().equals(question.getWrong1())) {
                    question.setWrong1(questionRequest.getWrong1());
                    isSomethingToEdit = true;
                }
            }
        }

        if (questionRequest.getWrong2() != null) {
            if (questionRequest.getWrong2().isBlank() || questionRequest.getWrong2().length() > 100) {
                logger.error(MessageFormat.format("User {0} | Invalid wrong answer 2. Received: {1}", username, questionRequest.getWrong2()));
                throw new Exception();
            } else {
                if (!questionRequest.getWrong2().equals(question.getWrong2())) {
                    question.setWrong2(questionRequest.getWrong2());
                    isSomethingToEdit = true;
                }
            }
        }

        if (questionRequest.getWrong3() != null) {
            if (questionRequest.getWrong3().isBlank() || questionRequest.getWrong3().length() > 100) {
                logger.error(MessageFormat.format("User {0} | Invalid wrong answer 3. Received: {1}", username, questionRequest.getWrong3()));
                throw new Exception();
            } else {
                if (!questionRequest.getWrong3().equals(question.getWrong3())) {
                    question.setWrong3(questionRequest.getWrong3());
                    isSomethingToEdit = true;
                }
            }
        }

        if (isSomethingToEdit) {
            try {
                questionRepository.save(question);
                logger.info(MessageFormat.format("User {0} | Question updated successfully", username));
            } catch (Exception e) {
                logger.error(MessageFormat.format("User {0} | Error updating question", username));
                throw new Exception();
            }
        } else {
            logger.warn(MessageFormat.format("User {0} | Nothing to update", username));
        }
    }
}
