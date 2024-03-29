package org.quiztastic.leaderboardservice.dto;

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
public class NewRecordDTO {

    private String username;

    private Long points;

    private Boolean isNewRecord;

}
