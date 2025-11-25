package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeDetailVO {

    private Integer recipeNo;
    private String title;
    private String summary;
    private String thumbnailUrl;
    private Float rating;
    private Integer reviewCount;
    private Integer servings;
    private Integer prepTime;
    private String difficulty;
    private Integer spicyLevel;
    private LocalDateTime inDate;


    private String writerNickname;
    private String writerProfile;


    private List<DetailIngredientVO> ingredients;
    private List<DetailStepVO> steps;


    @Data
    public static class DetailIngredientVO {
        private String name;
        private Float amount;
        private String unit;

        private List<AlternativeVO> alternatives;
    }

    @Data
    @AllArgsConstructor
    public static class AlternativeVO {
        private String name;
        private Float amount;
        private String unit;
    }


    @Data
    public static class DetailStepVO {
        private Integer stepOrder;
        private String content;
        private String imageUrl;
    }

}
