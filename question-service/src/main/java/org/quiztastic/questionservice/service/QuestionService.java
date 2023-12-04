package org.quiztastic.questionservice.service;

import lombok.RequiredArgsConstructor;
import org.quiztastic.questionservice.dto.*;
import org.quiztastic.questionservice.model.Answer;
import org.quiztastic.questionservice.model.EntityStatus;
import org.quiztastic.questionservice.model.Question;
import org.quiztastic.questionservice.repository.AnswerRepository;
import org.quiztastic.questionservice.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    Logger logger = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository questionRepository;

    private final AnswerRepository answerRepository;

    public GetQuestionResponse getQuestionByIdAndUser(Long id, String username) throws Exception {
        Question question = getQuestionByIdAndUser(id, username, false, true);

        return GetQuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .correct(question.getCorrect())
                .wrong1(question.getWrong1())
                .wrong2(question.getWrong2())
                .wrong3(question.getWrong3())
                .creation(question.getCreation())
                .build();
    }

    public List<GetQuestionShortResponse> getQuestionsByUser(String username) {
        List<Question> questionList = questionRepository.findActiveQuestionsByUsername(username);

        return questionList.stream()
                .map(q -> GetQuestionShortResponse.builder()
                        .id(q.getId())
                        .title(q.getTitle())
                        .correct(q.getCorrect())
                        .wrong1(q.getWrong1())
                        .wrong2(q.getWrong2())
                        .wrong3(q.getWrong3())
                        .creation(q.getCreation())
                        .build()
                ).collect(Collectors.toList());
    }

    public List<Map<String, String>> getNotAnsweredQuestions(String username) {
        List<Question> questionList = questionRepository.findNotAnsweredActiveQuestions(username);

        return questionList.stream()
                .map(q -> {
                        Map<String, String> mappedQuestion = new LinkedHashMap<>() {
                        };
                        mappedQuestion.put("id", q.getId().toString());
                        mappedQuestion.put("title", q.getTitle());

                        List<Integer> positionList = Arrays.asList(0, 1, 2, 3);
                        Collections.shuffle(positionList);

                        for (int i = 0; i < 4; i++) {
                            String key = "option" + i;
                            String value;
                            if (positionList.get(i) == 0) {
                                value = q.getCorrect();
                            } else if (positionList.get(i) == 1) {
                                value = q.getWrong1();
                            } else if (positionList.get(i) == 2) {
                                value = q.getWrong2();
                            } else {
                                value = q.getWrong3();
                            }
                            mappedQuestion.put(key, value);
                        }

                        mappedQuestion.put("creation", q.getCreation().toString());
                        mappedQuestion.put("username", q.getUsername());

                        return mappedQuestion;
                    }
                ).collect(Collectors.toList());
    }

    public List<AnsweredQuestionResponse> getAnsweredQuestions(String username) {
        List<Answer> answerList = answerRepository.findAnsweredActiveQuestions(username);

        return answerList.stream()
                .map(a -> AnsweredQuestionResponse.builder()
                        .id(a.getQuestion().getId())
                        .title(a.getQuestion().getTitle())
                        .correct(a.getQuestion().getCorrect())
                        .wrong1(a.getQuestion().getWrong1())
                        .wrong2(a.getQuestion().getWrong2())
                        .wrong3(a.getQuestion().getWrong3())
                        .status(a.getQuestion().getStatus())
                        .questionCreation(a.getQuestion().getCreation())
                        .username(a.getQuestion().getUsername())
                        .option(a.getChosenOption())
                        .answerCreation(a.getCreation())
                        .build()
                ).collect(Collectors.toList());
    }

    public AnswerQuestionResponse answerQuestion(AnswerQuestionRequest questionRequest, String username) throws Exception {
        // cannot answer if question is removed, user already answered, user created the question

        if (username == null) {
            logger.error("Invalid username. Received: null");
            throw new Exception();
        }

        if (questionRequest.getId() == null) {
            logger.error(MessageFormat.format("User {0} | Invalid question id. Received: null", username));
            throw new Exception();
        }

        if (questionRequest.getAnswer() == null) {
            logger.error(MessageFormat.format("User {0} | Invalid answer. Received: null", username));
            throw new Exception();
        }

        if (!answerRepository.doesUserAlreadyAnswerQuestion(username, questionRequest.getId()).equals(0L)) {
            logger.error(MessageFormat.format("User {0} | Already answered question with id {1}", username, questionRequest.getId()));
            throw new Exception();
        }

        Question question = getQuestionByIdAndUser(questionRequest.getId(), username, false, false);

        if (!question.getCorrect().equals(questionRequest.getAnswer()) &&
                !question.getWrong1().equals(questionRequest.getAnswer()) &&
                !question.getWrong2().equals(questionRequest.getAnswer()) &&
                !question.getWrong3().equals(questionRequest.getAnswer())
        ) {
            logger.error(MessageFormat.format("User {0} | Answers for question with id {1} not found", username, questionRequest.getId()));
            throw new Exception();
        }

        int chosenOption;
        if (question.getCorrect().equals(questionRequest.getAnswer())) {
            chosenOption = 0;
        } else if (question.getWrong1().equals(questionRequest.getAnswer())) {
            chosenOption = 1;
        } else if (question.getWrong2().equals(questionRequest.getAnswer())) {
            chosenOption = 2;
        } else {
            chosenOption = 3;
        }

        Answer answer = Answer.builder()
                .username(username)
                .creation(new Date())
                .chosenOption(chosenOption)
                .question(question)
                .build();

        answerRepository.save(answer);
        logger.info(MessageFormat.format("User {0} | Answer to question with id {1} saved successfully", username, questionRequest.getId()));

        return AnswerQuestionResponse.builder()
                .correct(question.getCorrect())
                .isCorrect(chosenOption == 0)
                .build();
    }

    public Question getQuestionByIdAndUser(Long id, String username, boolean canBeRemoved, boolean isCreator) throws Exception {
        if (username == null) {
            logger.error("Invalid username. Received: null");
            throw new Exception();
        }

        if (id == null) {
            logger.error(MessageFormat.format("User {0} | Invalid question id. Received: null", username));
            throw new Exception();
        }

        Optional<Question> question;
        try {
            question = questionRepository.findById(id);
        } catch (Exception e) {
            throw new Exception();
        }

        if (question.isEmpty()) {
            logger.error(MessageFormat.format("User {0} | Question with id {1} not found", username, id));
            throw new Exception();
        }

        if (isCreator && !username.equals(question.get().getUsername())) {
            logger.error(MessageFormat.format("User {0} | Not creator of question with id {1}", username, id));
            throw new Exception();
        } else if (!isCreator && username.equals(question.get().getUsername())) {
            logger.error(MessageFormat.format("User {0} | Should not be creator of question with id {1}", username, id));
            throw new Exception();
        }

        if (!canBeRemoved) {
            if (EntityStatus.REMOVED.equals(question.get().getStatus())) {
                logger.error(MessageFormat.format("User {0} | Question with id {1} is removed", username, id));
                throw new Exception();
            }
        }

        logger.info(MessageFormat.format("User {0} | Question with id {1} identified successfully", username, id));
        return question.get();
    }

    public void addQuestion(AddQuestionRequest questionRequest, String username) throws Exception {
        if (username == null) {
            logger.error("Invalid username. Received: null");
            throw new Exception();
        }

        if (questionRequest.getTitle() == null || questionRequest.getTitle().isBlank() || questionRequest.getTitle().length() > 150) {
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
            question = getQuestionByIdAndUser(questionRequest.getId(), username, false, true);
        } catch (Exception e) {
            throw new Exception();
        }

        AddQuestionRequest questionToAdd = AddQuestionRequest.builder()
                .title(questionRequest.getTitle())
                .correct(questionRequest.getCorrect())
                .wrong1(questionRequest.getWrong1())
                .wrong2(questionRequest.getWrong2())
                .wrong3(questionRequest.getWrong3())
                .build();

        try {
            addQuestion(questionToAdd, username);
        } catch (Exception e) {
            throw new Exception();
        }

        question.setStatus(EntityStatus.REMOVED);

        try {
            questionRepository.save(question);
            logger.info(MessageFormat.format("User {0} | Question with id {1} updated successfully", username, questionRequest.getId()));
        } catch (Exception e) {
            logger.error(MessageFormat.format("User {0} | Error updating question with id {1}", username, questionRequest.getId()));
            throw new Exception();
        }
    }

    public void deleteQuestion(Long id, String username) throws Exception {
        if (username == null) {
            logger.error("Invalid username. Received: null");
            throw new Exception();
        }

        if (id == null) {
            logger.error(MessageFormat.format("User {0} | Invalid id. Received: null", username));
            throw new Exception();
        }

        Question question;
        try {
            question = getQuestionByIdAndUser(id, username, false, true);
        } catch (Exception e) {
            throw new Exception();
        }

        question.setStatus(EntityStatus.REMOVED);

        try {
            questionRepository.save(question);
            logger.info(MessageFormat.format("User {0} | Question with id {1} deleted successfully", username, question.getId()));
        } catch (Exception e) {
            logger.error(MessageFormat.format("User {0} | Error updating question with id {1}", username, question.getId()));
            throw new Exception();
        }
    }
}
