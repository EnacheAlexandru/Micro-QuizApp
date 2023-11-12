package org.quiztastic.questionservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.questionservice.model.Question;
import org.quiztastic.questionservice.service.QuestionService;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/question", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class QuestionController {

    private final ServletWebServerApplicationContext webServerAppContext;

    private final QuestionService questionService;

    @GetMapping("/all")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/port")
    public Integer getRunningPort() {
        return webServerAppContext.getWebServer().getPort();
    }

}
