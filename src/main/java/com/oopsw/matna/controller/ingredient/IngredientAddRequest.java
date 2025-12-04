package com.oopsw.matna.controller.ingredient;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngredientAddRequest {
    private Integer creatorNo;
    private String ingredientName;
}
