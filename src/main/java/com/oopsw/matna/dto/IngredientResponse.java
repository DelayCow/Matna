package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class IngredientResponse {
    private Integer ingredientNo;
    private String ingredientName;
}
