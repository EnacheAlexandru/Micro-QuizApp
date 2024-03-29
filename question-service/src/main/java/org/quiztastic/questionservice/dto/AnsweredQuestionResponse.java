package org.quiztastic.questionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.quiztastic.questionservice.model.EntityStatus;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AnsweredQuestionResponse {

    private Long id;

    private String title;

    private String correct;

    private String wrong1;

    private String wrong2;

    private String wrong3;

    private EntityStatus status;

    private Date questionCreation;

    private String username;

    private Integer option;

    private Date answerCreation;
}
