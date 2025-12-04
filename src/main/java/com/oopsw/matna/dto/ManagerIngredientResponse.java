package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Getter
@ToString
public class ManagerIngredientResponse {
    private Integer ingredientId;
    private String ingredientName;
    private String creatorName;
    private LocalDateTime inDate;
    private LocalDateTime approveDate;
}
