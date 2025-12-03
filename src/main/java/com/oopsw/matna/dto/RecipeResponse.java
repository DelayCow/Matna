package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class RecipeResponse {
    private Integer recipeNo;
    private String title;
    private String thumbnailUrl;
    private String writerNickname;
    private String writerProfile;
    private Float averageRating;
    private Integer reviewCount;
    private Integer servings;
    private Integer prepTime;
    private String difficulty;
    private Integer spicyLevel;
}
