package org.quiztastic.questionservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.questionservice.dto.*;
import org.quiztastic.questionservice.service.JwtService;
import org.quiztastic.questionservice.service.QuestionService;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/question", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class QuestionController {

//    private final ServletWebServerApplicationContext webServerAppContext;

    private final QuestionService questionService;

    private final JwtService jwtService;

    @GetMapping("/user/{id}")
    public ResponseEntity<GetQuestionResponse> requestGetQuestionByIdAndUser(
            @RequestHeader(value = "Authorization", required = false) String jwtHeader,
            @PathVariable Long id
    ) {
        String username = getAuthUsername(jwtHeader);
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
            @RequestHeader(value = "Authorization", required = false) String jwtHeader
    ) {
        String username = getAuthUsername(jwtHeader);
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
            @RequestHeader(value = "Authorization", required = false) String jwtHeader
    ) {
        String username = getAuthUsername(jwtHeader);
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
            @RequestHeader(value = "Authorization", required = false) String jwtHeader
    ) {
        String username = getAuthUsername(jwtHeader);
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
            @RequestHeader(value = "Authorization", required = false) String jwtHeader,
            @RequestBody AddQuestionRequest questionRequest
    ) {
        String username = getAuthUsername(jwtHeader);
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

//    @GetMapping("/port")
//    public Integer getRunningPort() {
//        return webServerAppContext.getWebServer().getPort();
//    }

    @PostMapping("/update")
    public ResponseEntity<Void> requestUpdateQuestion(
            @RequestHeader(value = "Authorization", required = false) String jwtHeader,
            @RequestBody UpdateQuestionRequest questionRequest
    ) {
        String username = getAuthUsername(jwtHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            questionService.updateQuestion(questionRequest, username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<GenericResponse> requestAnswerQuestion(
            @RequestHeader(value = "Authorization", required = false) String jwtHeader,
            @RequestBody AnswerQuestionRequest questionRequest
    ) {
        String username = getAuthUsername(jwtHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String correct = questionService.answerQuestion(questionRequest, username);
            return ResponseEntity.ok(GenericResponse.builder().message(correct).build());
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
