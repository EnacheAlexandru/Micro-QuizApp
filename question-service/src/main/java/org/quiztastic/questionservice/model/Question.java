package org.quiztastic.questionservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String correct;

    private String wrong1;

    private String wrong2;

    private String wrong3;

    private Date creation;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;

    private String username;
}
