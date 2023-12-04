package org.quiztastic.questionservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.questionservice.dto.*;
import org.quiztastic.questionservice.model.KafkaOperation;
import org.quiztastic.questionservice.service.JwtService;
import org.quiztastic.questionservice.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/question", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class QuestionController {

    private final ServletWebServerApplicationContext webServerAppContext;

    Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionService questionService;

    private final JwtService jwtService;

    private final KafkaTemplate<String, Map<String, String>> kafkaTemplate;

    @GetMapping("/user/{id}")
    public ResponseEntity<GetQuestionResponse> requestGetQuestionByIdAndUser(
            @RequestHeader(value = "Authorization", required = false) String jwtBearer,
            @PathVariable Long id
    ) {
        String username = getAuthUsername(jwtBearer);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            GetQuestionResponse question = questionService.getQuestionByIdAndUser(id, username);
            return ResponseEntity.ok(question);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // active questions created by user
    @GetMapping("/user")
    public ResponseEntity<List<GetQuestionShortResponse>> requestGetQuestionsByUser(
            @RequestHeader(value = "Authorization", required = false) String jwtBearer
    ) {
        String username = getAuthUsername(jwtBearer);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<GetQuestionShortResponse> questionList = questionService.getQuestionsByUser(username);
            return ResponseEntity.ok(questionList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // active questions where user not answered and not created by him
    @GetMapping("/other")
    public ResponseEntity<List<Map<String, String>>> requestGetNotAnsweredQuestions(
            @RequestHeader(value = "Authorization", required = false) String jwtBearer
    ) {
        String username = getAuthUsername(jwtBearer);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Map<String, String>> questionList = questionService.getNotAnsweredQuestions(username);
            return ResponseEntity.ok(questionList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // questions where user answered and not created by him
    @GetMapping("/other/answer")
    public ResponseEntity<List<AnsweredQuestionResponse>> requestGetAnsweredQuestions(
            @RequestHeader(value = "Authorization", required = false) String jwtBearer
    ) {
        String username = getAuthUsername(jwtBearer);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<AnsweredQuestionResponse> questionList = questionService.getAnsweredQuestions(username);
            return ResponseEntity.ok(questionList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> requestAddQuestion(
            @RequestHeader(value = "Authorization", required = false) String jwtBearer,
            @RequestBody AddQuestionRequest questionRequest
    ) {
        String username = getAuthUsername(jwtBearer);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            questionService.addQuestion(questionRequest, username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // for testing load balancer
    @GetMapping("/port")
    public Integer getRunningPort() {
        return webServerAppContext.getWebServer().getPort();
    }

    /**
     * not really working as intended
     * sets the status of the old question to REMOVED, but adds the updated question with status ACTIVE
     * this means that the updated question can be answered by the others,
     * even if they have already answered to the old question
     * must be refactored
     **/
//    @PostMapping("/update")
//    public ResponseEntity<Void> requestUpdateQuestion(
//            @RequestHeader(value = "Authorization", required = false) String jwtBearer,
//            @RequestBody UpdateQuestionRequest questionRequest
//    ) {
//        String username = getAuthUsername(jwtBearer);
//        if (username == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        try {
//            questionService.updateQuestion(questionRequest, username);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PostMapping("/delete")
    public ResponseEntity<Void> requestDeleteQuestion(
            @RequestHeader(value = "Authorization", required = false) String jwtBearer,
            @RequestBody IdDTO request
    ) {
        String username = getAuthUsername(jwtBearer);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            questionService.deleteQuestion(request.getId(), username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<GenericResponse> requestAnswerQuestion(
            @RequestHeader(value = "Authorization", required = false) String jwtBearer,
            @RequestBody AnswerQuestionRequest questionRequest
    ) {
        String username = getAuthUsername(jwtBearer);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            AnswerQuestionResponse questionResponse = questionService.answerQuestion(questionRequest, username);

            Map<String, String> payload = new LinkedHashMap<>();
            payload.put("operation", KafkaOperation.UPDATE_LEADERBOARD.name());
            payload.put("token", jwtBearer);
            payload.put("isCorrect", questionResponse.getIsCorrect().toString());

            logger.info(MessageFormat.format("User {0} | Sending operation {1} with payload {2}", username, KafkaOperation.UPDATE_LEADERBOARD.name(), payload));
            kafkaTemplate.send("notificationTopic", payload);

            return ResponseEntity.ok(GenericResponse.builder().message(questionResponse.getCorrect()).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String getAuthUsername(String jwtBearer) {
        if (!jwtService.isJwtBearerValid(jwtBearer, null)) {
            return null;
        }

        try {
            return jwtService.extractUsernameJwtBearer(jwtBearer, null);
        } catch (Exception e) {
            return null;
        }
    }

}
