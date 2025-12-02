package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeDetailVO {

    private Integer recipeNo;
    private String title;
    private String summary;
    private String thumbnailUrl;
    private String category;
    private Float rating;
    private Integer reviewCount;
    private Integer servings;
    private Integer prepTime;
    private String difficulty;
    private Integer spicyLevel;
    private LocalDateTime inDate;

    private Integer writerNo;
    private String writerNickname;
    private String writerProfile;


    private List<DetailIngredientVO> ingredients;
    private List<RecipeStepVO> steps;
    private List<ReviewsListVO> reviews;

    private Map<Integer, Double> spicyLevelPercentages;

    @Data
    public static class DetailIngredientVO {
        private String ingredientName;
        private Float amount;
        private String unit;
        private Boolean isGroupBuying;
        private List<IngredientVO> alternatives;
    }

}
