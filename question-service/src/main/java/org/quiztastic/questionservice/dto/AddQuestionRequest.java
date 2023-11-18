package org.quiztastic.questionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AddQuestionRequest {

    private String title;

    private String correct;

    private String wrong1;

    private String wrong2;

    private String wrong3;
}
