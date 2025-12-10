package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class RecipeRegisterVO {
    private Integer recipeNo;
    private String title;
    private String summary;
    private String category;
    private String thumnailUrl;
    private Integer spicyLevel;
    private Integer prepTime;
    private Integer servings;
    private String difficulty;
    private List<IngredientVO> ingredient;
    private List<RecipeStepVO> step;
}
